package me.lauriichan.minecraft.minigame.data.automatic.message;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import me.lauriichan.minecraft.minigame.MinigameCore;
import me.lauriichan.minecraft.minigame.inject.InjectListener;
import me.lauriichan.minecraft.minigame.inject.InjectManager;
import me.lauriichan.minecraft.minigame.util.JavaAccessor;

public final class MessageManager implements InjectListener {

    private final MessageConfiguration configuration;
    private final ArrayList<MessageSync> synchronize = new ArrayList<>();

    public MessageManager(final MinigameCore core, final InjectManager inject) {
        this.configuration = new MessageConfiguration(this, core);
        inject.listen(this);
    }

    public MessageConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void onInjectClass(Class<?> type) {
        Field[] fields = JavaAccessor.getFields(type);
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            syncValue(null, field);
        }
    }

    @Override
    public void onInjectInstance(Class<?> type, Object instance) {
        Field[] fields = JavaAccessor.getFields(type);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            syncValue(instance, field);
        }
    }

    private void syncValue(Object instance, Field field) {
        MessageId messageId = JavaAccessor.getAnnotation(field, MessageId.class);
        if (messageId == null) {
            return;
        }
        MessageSync sync = new MessageSync(instance, field);
        sync.update();
        synchronize.add(sync);
    }

    void update() {
        for (int index = 0; index < synchronize.size(); index++) {
            synchronize.get(index).update();
        }
    }

}
