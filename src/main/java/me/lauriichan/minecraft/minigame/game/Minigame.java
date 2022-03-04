package me.lauriichan.minecraft.minigame.game;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.lauriichan.minecraft.minigame.annotation.AnnotationId;

@Target(TYPE)
@Retention(RUNTIME)
@AnnotationId(name = "Minigame")
public @interface Minigame {

    String id();

    String name() default "";

}
