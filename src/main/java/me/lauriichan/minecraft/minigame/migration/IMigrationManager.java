package me.lauriichan.minecraft.minigame.migration;

import java.util.List;
import java.util.logging.Logger;

public interface IMigrationManager {
    
    Logger getLogger();
    
    <M extends MigrationProvider> List<MigrationTarget<M>> getTargets(Class<MigrationType<?, M>> type);

}
