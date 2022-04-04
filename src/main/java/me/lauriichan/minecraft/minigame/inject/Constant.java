package me.lauriichan.minecraft.minigame.inject;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.lauriichan.minecraft.minigame.annotation.AnnotationId;

@Retention(RUNTIME)
@Target(TYPE)
@AnnotationId(name = "Constant")
public @interface Constant {

}
