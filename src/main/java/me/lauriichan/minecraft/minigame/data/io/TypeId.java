package me.lauriichan.minecraft.minigame.data.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE
})
public @interface TypeId {

    public String name() default "";

    public Class<?> input();

    public Class<?> output();

}
