package me.lauriichan.minecraft.minigame.data;

import me.lauriichan.minecraft.minigame.data.automatic.message.Message;
import me.lauriichan.minecraft.minigame.data.automatic.message.Text;

public final class CoreMessages {

    private CoreMessages() {
        throw new UnsupportedOperationException();
    }
    
    @Message(id = "data.load.start", content = "Trying to load data from '$name'...")
    public static Text DATA_LOAD_START;
    @Message(id = "data.load.success", content = "Data was successfully loaded from $name!")
    public static Text DATA_LOAD_SUCCESS;
    @Message(id = "data.load.failed", content = "Something went wrong while saving data to $name!")
    public static Text DATA_LOAD_FAILED;
    @Message(id = "data.save.start", content = "Trying to save data to '$name'...")
    public static Text DATA_SAVE_START;
    @Message(id = "data.save.success", content = "Data was successfully saved to $name!")
    public static Text DATA_SAVE_SUCCESS;
    @Message(id = "data.save.failed", content = "Something went wrong while saving data to $name!")
    public static Text DATA_SAVE_FAILED;
    
    @Message(id = "command.general.help.format", content = "$plugin.prefix &4/$name &c$path&7$arguments&8- &7$description")
    public static Text COMMAND_GENERAL_HELP_FORMAT;
    @Message(id = "command.general.help.optional", content = "{$name@$type}")
    public static Text COMMAND_GENERAL_HELP_OPTIONAL;
    @Message(id = "command.general.help.required", content = "<$name@$type>")
    public static Text COMMAND_GENERAL_HELP_REQUIRED;

}
