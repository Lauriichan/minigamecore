package me.lauriichan.minecraft.minigame.migration;

public abstract class MigrationProvider implements Comparable<MigrationProvider> {

    private final long id;

    public MigrationProvider() {
        this.id = getDate();
    }

    protected abstract long getDate();

    public final long getId() {
        return id;
    }

    @Override
    public int compareTo(MigrationProvider o) {
        return Long.compare(o.id, id);
    }

}
