package me.lauriichan.minecraft.minigame.data.io;

public interface IDataExtension<I, O> {

    O convert(I input);

}
