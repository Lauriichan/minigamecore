package me.lauriichan.minecraft.minigame;

import org.bukkit.command.CommandSender;

import me.lauriichan.minecraft.minigame.command.annotation.Action;
import me.lauriichan.minecraft.minigame.command.annotation.Argument;
import me.lauriichan.minecraft.minigame.command.annotation.Command;
import me.lauriichan.minecraft.minigame.config.Config;

@Command(name = "test")
public class TestCommand {

    @Config(key = "test.value")
    private final String testValue = "";

    @Action(path = "set")
    public void onTest(@Argument(sender = true) CommandSender sender, int id) {
        
    }

}
