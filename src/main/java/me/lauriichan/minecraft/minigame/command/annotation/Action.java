package me.lauriichan.minecraft.minigame.command.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
@Repeatable(Action.Actions.class)
public @interface Action {

    String path();
    
    @Target(METHOD)
    @Retention(RUNTIME)
    @interface Actions {
        
        Action[] value();

    }


}
