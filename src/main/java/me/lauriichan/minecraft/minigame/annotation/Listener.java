package me.lauriichan.minecraft.minigame.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.lauriichan.minecraft.minigame.game.GamePhase;

@Retention(SOURCE)
@Target(TYPE)
public @interface Listener {

    Class<? extends GamePhase> phase();

}
