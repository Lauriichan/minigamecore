package me.lauriichan.minecraft.minigame.migration;

public final class MigrationTarget<M extends MigrationProvider> {

    private final Migration point;
    private final M migration;

    public MigrationTarget(Migration point, M migration) {
        this.migration = migration;
        this.point = point;
    }

    public Migration getPoint() {
        return point;
    }

    public M getMigration() {
        return migration;
    }

}
