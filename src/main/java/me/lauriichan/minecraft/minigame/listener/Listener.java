package me.lauriichan.minecraft.minigame.listener;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.bukkit.event.EventHandler;

import me.lauriichan.minecraft.minigame.annotation.AnnotationId;
import me.lauriichan.minecraft.minigame.game.GamePhase;
import me.lauriichan.minecraft.minigame.game.NullPhase;

@Target(METHOD)
@Retention(RUNTIME)
@AnnotationId(name = "Listener")
public @interface Listener {
    
    EventHandler handler() default @EventHandler;

    Class<? extends GamePhase> phase() default NullPhase.class;

}
