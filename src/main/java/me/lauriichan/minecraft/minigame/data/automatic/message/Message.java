package me.lauriichan.minecraft.minigame.data.automatic.message;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Message {

    String id() default "";

    String[] content() default {};

}
