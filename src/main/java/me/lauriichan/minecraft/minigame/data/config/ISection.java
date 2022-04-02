package me.lauriichan.minecraft.minigame.data.config;

import java.util.Set;

public interface ISection<C, T> {
    
    ISection<C, T> getParent();
    
    ISection<C, T> getRoot();

    Set<String> keys();

    String name();

    void clear();

    boolean has(String path);
    
    boolean has(String path, T type);

    boolean hasValue(String path);

    boolean hasValue(String path, Class<?> sample);

    C get(String path);

    C get(String path, T type);

    Object getValue(String path);

    <P> P getValue(String path, Class<P> sample);

    <P> P getValueOrDefault(String path, P fallback);

    Number getValueOrDefault(String path, Number fallback);
    
    boolean isSection(String path);
    
    ISection<C, T> getSection(String path);
    
    ISection<C, T> createSection(String path);
    
    void set(String path, C value);
    
    void setValue(String path, Object value);

}
