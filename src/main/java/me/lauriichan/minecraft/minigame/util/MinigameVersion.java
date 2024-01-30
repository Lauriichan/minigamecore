package me.lauriichan.minecraft.minigame.util;

public enum MinigameVersion {
    
    V1(null),
    V2("org.bukkit.entity.Illusioner");
    
    public static final MinigameVersion VERSION = detect();
    
    private static MinigameVersion detect() {
        MinigameVersion[] versions = MinigameVersion.values();
        for (int i = versions.length - 1; i > 0; i--) {
            try {
                Class.forName(versions[i].activationClass);
            } catch (ClassNotFoundException e) {
                continue;
            }
            return versions[i];
        }
        return V1;
    }
    
    private final String activationClass;
    
    private MinigameVersion(String activationClass) {
        this.activationClass = activationClass;
    }

}
