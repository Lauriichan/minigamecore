package me.lauriichan.minecraft.minigame.util;

public final class Reference<E> {

    private E value;
    private boolean locked = false;

    private Reference(E value) {
        this.value = value;
    }

    public E get() {
        return value;
    }

    public Reference<E> set(E value) {
        if (!locked) {
            this.value = value;
        }
        return this;
    }

    public Reference<E> lock() {
        this.locked = true;
        return this;
    }
    
    public boolean isPresent() {
        return value != null;
    }
    
    public boolean isEmpty() {
        return value == null;
    }

    public static <E> Reference<E> of() {
        return new Reference<>(null);
    }

    public static <E> Reference<E> of(E value) {
        return new Reference<>(value);
    }

}
