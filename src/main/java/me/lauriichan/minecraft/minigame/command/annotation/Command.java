package me.lauriichan.minecraft.minigame.command.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.lauriichan.minecraft.minigame.annotation.AnnotationId;

@Target(TYPE)
@Retention(RUNTIME)
@AnnotationId(name = "Command")
public @interface Command {

    String name();

    String[] aliases() default {};

    String description() default "";

}
