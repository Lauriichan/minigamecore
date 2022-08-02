package me.lauriichan.minecraft.minigame.command.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.command.parser.ObjectParser;

@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Argument {

    Class<? extends IArgumentParser<?>> parser() default ObjectParser.class;

    String name() default "";

    Param[] params() default {};

    int index() default 0;

    boolean sender() default false;

    boolean optional() default false;

}
