package me.lauriichan.minecraft.minigame.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface AnnotationId {
    
    String name();

}
