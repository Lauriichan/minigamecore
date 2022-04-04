package me.lauriichan.minecraft.minigame.data.automatic.message;

public final class Messages {

    private Messages() {
        throw new UnsupportedOperationException("Constant class");
    }

    public static final Message DATA_LOAD_START;
    public static final Message DATA_LOAD_SUCCESS;
    public static final Message DATA_LOAD_FAILED;
    public static final Message DATA_SAVE_START;
    public static final Message DATA_SAVE_SUCCESS;
    public static final Message DATA_SAVE_FAILED;
    
    static {
        DATA_LOAD_START = Message.register("data.load.start", "Trying to load data from '$name'...");
        DATA_LOAD_SUCCESS = Message.register("data.load.success", "Data was successfully loaded from $name!");
        DATA_LOAD_FAILED = Message.register("data.load.failed", "Something went wrong while loading data from $name!");
        DATA_SAVE_START = Message.register("data.save.start", "Trying to save data to '$name'...");
        DATA_SAVE_SUCCESS = Message.register("data.save.success", "Data was successfully saved to $name!");
        DATA_SAVE_FAILED = Message.register("data.save.failed", "Something went wrong while saving data to $name!");
    }

}
