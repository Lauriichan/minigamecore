package me.lauriichan.minecraft.minigame.data.io;

@SuppressWarnings("rawtypes")
final class DataInfo {

    private final TypeId id;
    private final IDataExtension handle;

    public DataInfo(final TypeId id, final IDataExtension handle) {
        this.id = id;
        this.handle = handle;
    }

    public Class<?> getHandleType() {
        return handle.getClass();
    }

    public String getId() {
        return id.name();
    }

    public Class<?> getInputType() {
        return id.input();
    }

    public Class<?> getOutputType() {
        return id.output();
    }

    public boolean hasId() {
        return id.name() != null && !id.name().isBlank();
    }

    public boolean isValid() {
        return id.input() != null && id.output() != null;
    }

    @SuppressWarnings("unchecked")
    public Object convert(final Object input) {
        if (!getInputType().isAssignableFrom(input.getClass())) {
            return null;
        }
        final Object out = handle.convert(id.input().cast(input));
        return out == null ? null : id.output().cast(out);
    }

    @Override
    public int hashCode() {
        return id.name().hashCode() << 0 | id.input().hashCode() << 12 | id.output().hashCode() << 24;
    }

}
