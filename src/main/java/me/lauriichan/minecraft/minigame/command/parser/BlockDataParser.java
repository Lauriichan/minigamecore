package me.lauriichan.minecraft.minigame.command.parser;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import me.lauriichan.minecraft.minigame.command.IArgumentParser;
import me.lauriichan.minecraft.minigame.command.ParamMap;
import me.lauriichan.minecraft.minigame.util.Tuple;

public final class BlockDataParser implements IArgumentParser<BlockData> {

    @Override
    public Tuple<BlockData, Integer> parse(Class<?> type, int offset, String[] arguments, ParamMap params) throws IllegalArgumentException {
        return Tuple.of(Bukkit.createBlockData(arguments[offset]), 1);
    }

}
