package me.lauriichan.minecraft.minigame.command.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.command.parser.ObjectParser;

@Retention(SOURCE)
@Target(PARAMETER)
public @interface Argument {

    Class<? extends IArgumentParser<?>> parser() default ObjectParser.class;

    int index() default 0;
    
    boolean sender() default false;

}
