package me.lauriichan.minecraft.minigame.data.automatic.message;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import me.lauriichan.minecraft.minigame.util.JavaAccessor;

final class MessageSync {

    private final Object instance;

    private final Field field;
    private final VarHandle handle;
    private final boolean _static;

    private final Message message;

    public MessageSync(final Object instance, final Field field) {
        MessageId messageId = JavaAccessor.getAnnotation(field, MessageId.class);
        if (messageId == null) {
            throw new IllegalArgumentException("Field needs @Message annotation");
        }
        this.instance = instance;
        this.field = Objects.requireNonNull(field);
        if (Objects.equals(field.getType(), String.class)) {
            throw new IllegalArgumentException("Field is not of type String!");
        }
        this.handle = Modifier.isFinal(field.getModifiers()) ? null : JavaAccessor.accessField(field, true);
        this._static = Modifier.isStatic(field.getModifiers());
        Message message = Message.get(messageId.key());
        if (message != null) {
            this.message = message;
            return;
        }
        Object tmp;
        if (_static) {
            tmp = JavaAccessor.getStaticValue(handle);
        } else {
            tmp = JavaAccessor.getValue(instance, handle);
        }
        this.message = Message.register(messageId.key(), tmp.toString());
    }
    
    public Message getMessage() {
        return message;
    }

    public Field getField() {
        return field;
    }

    public Object getInstance() {
        return instance;
    }

    public void update() {
        if (handle == null) {
            if (_static) {
                JavaAccessor.setStaticValue(field, message.getTranslation());
                return;
            }
            JavaAccessor.setValue(instance, field, message.getTranslation());
            return;
        }
        if (_static) {
            JavaAccessor.setStaticValue(handle, message.getTranslation());
            return;
        }
        JavaAccessor.setValue(instance, handle, message.getTranslation());
    }

}
