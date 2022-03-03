package me.lauriichan.minecraft.minigame.annotation;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.command.annotation.Action;
import me.lauriichan.minecraft.minigame.command.annotation.Command;
import me.lauriichan.minecraft.minigame.command.annotation.Parser;
import me.lauriichan.minecraft.minigame.game.Game;
import me.lauriichan.minecraft.minigame.game.Minigame;
import me.lauriichan.minecraft.minigame.listener.Listener;

@SuppressWarnings("unchecked")
public final class AnnotationProcessor extends AbstractProcessor {

    public static final String ANNOTATION_RESOURCE = "META-INF/generated/";

    private static final Class<? extends Annotation>[] ANNOTATIONS = new Class[] {
        Listener.class,
        Minigame.class,
        Command.class,
        Parser.class
    };

    private static final Class<?>[] NEEDED_FOR_ANNOTATION = new Class[] {
        null,
        Game.class,
        null,
        IArgumentParser.class
    };

    private static final ElementKind[] TARGET_ELEMENTS = new ElementKind[] {
        ElementKind.METHOD,
        ElementKind.CLASS,
        ElementKind.CLASS,
        ElementKind.CLASS
    };

    private static final Class<? extends Annotation>[][] INCOMPATIBLE = new Class[][] {
        null,
        new Class[] {
            Action.class
        },
        new Class[] {
            Command.class,
            Parser.class
        },
        new Class[] {
            Minigame.class,
            Parser.class
        },
        new Class[] {
            Command.class,
            Minigame.class
        }
    };

    private final HashMap<Class<? extends Annotation>, HashSet<String>> paths = new HashMap<>();

    private final TypeMirror[] annotationType = new TypeMirror[ANNOTATIONS.length];

    private Types types;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.types = processingEnv.getTypeUtils();
        this.elements = processingEnv.getElementUtils();
        for (int index = 0; index < ANNOTATIONS.length; index++) {
            Class<? extends Annotation> annotationClass = ANNOTATIONS[index];
            paths.put(annotationClass, new HashSet<>());
            annotationType[index] = elements.getTypeElement(annotationClass.getName()).asType();
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        for (int index = 0; index < annotationType.length; index++) {
            Class<? extends Annotation>[] incompatible = INCOMPATIBLE[index];
            Class<? extends Annotation> annotation = ANNOTATIONS[index];
            log(Kind.NOTE, "Processing @%s", annotation.getSimpleName());
            Class<?> target = NEEDED_FOR_ANNOTATION[index];
            ElementKind kind = TARGET_ELEMENTS[index];
            ArrayList<Element[]> elementArrays = new ArrayList<>();
            elementArrays.add(roundEnv.getElementsAnnotatedWith(annotation).stream().filter(element -> {
                if (element.getKind() != kind) {
                    return false;
                }
                if (incompatible == null) {
                    return true;
                }
                for (Class<?> incompatibleAnnotation : incompatible) {
                    if (hasAnnotation(element, incompatibleAnnotation)) {
                        return false;
                    }
                }
                return true;
            }).toArray(Element[]::new));
            lb_annotationLoop:
            for (TypeElement annotationType : annotations) {
                if (!hasAnnotation(annotationType, annotation)) {
                    continue;
                }
                if (incompatible != null) {
                    for (Class<?> incompatibleAnnotation : incompatible) {
                        if (hasAnnotation(annotationType, incompatibleAnnotation)) {
                            continue lb_annotationLoop;
                        }
                    }
                }
                elementArrays.add(roundEnv.getElementsAnnotatedWith(annotationType).toArray(Element[]::new));
            }
            HashSet<String> path = paths.get(annotation);
            for (Element[] elements : elementArrays) {
                if (kind != ElementKind.CLASS || target == null) {
                    for (Element element : elements) {
                        processElement(path, element, annotation);
                    }
                    continue;
                }
                TypeMirror mirror = this.elements.getTypeElement(target.getName()).asType();
                boolean interf = target.isInterface();
                for (Element element : elements) {
                    if (!(element instanceof TypeElement)) {
                        continue;
                    }
                    processTypeElement(path, (TypeElement) element, interf, mirror, annotation);
                }
            }
        }
        log(Kind.NOTE, "Writing information...");
        try {
            for (Entry<Class<? extends Annotation>, HashSet<String>> entry : paths.entrySet()) {
                String className = entry.getKey().getName();
                HashSet<String> set = entry.getValue();
                if (set.isEmpty()) {
                    continue;
                }
                FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "",
                    ANNOTATION_RESOURCE + className);
                try (Writer writer = file.openWriter()) {
                    for (String path : set) {
                        writer.append(path);
                        writer.append('\n');
                    }
                }
            }
        } catch (IOException e) {
            log(Kind.ERROR, Exceptions.stackTraceToString(e));
        }
        return false;
    }

    private void processElement(HashSet<String> path, Element element, Class<? extends Annotation> annotation) {
        if (element instanceof ExecutableElement) {
            ExecutableElement executable = (ExecutableElement) element;
            if (executable.getModifiers().contains(Modifier.ABSTRACT)) {
                log(Kind.WARNING, "Cannot have an abstract modifier on element '%s' with annotation '%s'!", executable.toString(),
                    annotation.getSimpleName());
                return;
            }
            if (executable.getModifiers().contains(Modifier.NATIVE)) {
                log(Kind.WARNING, "Cannot have an native modifier on element '%s' with annotation '%s'!", executable.toString(),
                    annotation.getSimpleName());
                return;
            }
            path.add(executable.getEnclosingElement().toString());
            log(Kind.NOTE, "Found annotation '%s' in '%s'", annotation.getSimpleName(), executable.getEnclosingElement().toString());
            return;
        }
        if (element instanceof VariableElement) {
            VariableElement variable = (VariableElement) element;
            path.add(variable.getEnclosingElement().toString());
            log(Kind.NOTE, "Found annotation '%s' in '%s'", annotation.getSimpleName(), variable.getEnclosingElement().toString());
            return;
        }
        if (!(element instanceof TypeElement)) {
            return;
        }
        TypeElement type = (TypeElement) element;
        if (type.getModifiers().contains(Modifier.ABSTRACT)) {
            log(Kind.WARNING, "Cannot have an abstract modifier on element '%s' with annotation '%s'!", type.toString(),
                annotation.getSimpleName());
            return;
        }
        path.add(type.toString());
        log(Kind.NOTE, "Found annotation '%s' in '%s'", annotation.getSimpleName(), type.toString());
    }

    private void processTypeElement(HashSet<String> path, TypeElement type, boolean interf, TypeMirror mirror,
        Class<? extends Annotation> annotation) {
        if (interf) {
            if (!isInterfaceNested(type.getInterfaces(), mirror)) {
                log(Kind.WARNING, "Found annotation '%s' in '%s' without interface '%s'!", annotation.getSimpleName(), type.toString(),
                    mirror.toString());
                return;
            }
            path.add(type.toString());
            log(Kind.NOTE, "Found annotation '%s' in '%s'", annotation.getSimpleName(), type.toString());
            return;
        }
        if (!isSuperclassNested(type.getSuperclass(), mirror)) {
            log(Kind.WARNING, "Found annotation '%s' on '%s' without superclass '%s'!", annotation.getSimpleName(), type.toString(),
                mirror.toString());
            return;
        }
        path.add(type.toString());
        log(Kind.NOTE, "Found annotation '%s' in '%s'", annotation.getSimpleName(), type.toString());
    }

    /*
     * Helpers
     */

    public boolean isInterfaceNested(List<? extends TypeMirror> list, TypeMirror searched) {
        for (TypeMirror mirror : list) {
            if (mirror == searched) {
                return true;
            }
            Element element = types.asElement(mirror);
            if (!(element instanceof TypeElement) || !isInterfaceNested(((TypeElement) element).getInterfaces(), searched)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public boolean isSuperclassNested(TypeMirror superClass, TypeMirror searched) {
        if (superClass == null) {
            return false;
        }
        if (superClass != searched) {
            Element element = types.asElement(superClass);
            if (!(element instanceof TypeElement)) {
                return false;
            }
            return isSuperclassNested(((TypeElement) element).getSuperclass(), searched);
        }
        return true;
    }

    public boolean hasAnnotation(Element element, Class<?> annotation) {
        return getAnnotationMirror(element, annotation) != null;
    }

    public AnnotationMirror getAnnotationMirror(Element element, Class<?> annotation) {
        String annotationName = annotation.getName();
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(annotationName)) {
                return mirror;
            }
        }
        return null;
    }

    public void log(Kind kind, String message, Object... arguments) {
        String out = String.format(message, arguments);
        processingEnv.getMessager().printMessage(kind, out);
        if (kind == Kind.ERROR) {
            System.out.println("[ERROR] " + out);
            return;
        }
        if (kind == Kind.WARNING) {
            System.out.println("[WARNING] " + out);
            return;
        }
        System.out.println("[INFO] " + out);
    }

}
