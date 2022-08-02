package me.lauriichan.minecraft.minigame.command;

import java.util.Comparator;

final class ArgComparator implements Comparator<String> {

    private final String arg;

    public ArgComparator(final String arg) {
        this.arg = arg;
    }

    @Override
    public int compare(final String o1, final String o2) {
        if (o1.startsWith(arg)) {
            return 1;
        }
        if (o2.startsWith(arg)) {
            return -1;
        }
        if (o1.endsWith(arg)) {
            return 1;
        }
        if (o2.endsWith(arg)) {
            return -1;
        }
        return 0;
    }

}
