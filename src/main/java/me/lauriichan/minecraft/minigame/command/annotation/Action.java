package me.lauriichan.minecraft.minigame.command.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Repeatable(Action.Actions.class)
@Retention(SOURCE)
@Target(METHOD)
public @interface Action {

    String path();
    
    @Retention(RUNTIME)
    @Target(METHOD)
    @interface Actions {
        
        Action[] value();

    }


}
