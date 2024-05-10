package net.earomc.emeraldrush;

import java.util.HashSet;

public class FlagSet<T extends Enum<?>> {
    private HashSet<T> flagSet = new HashSet<>();
    public void setFlag(T flag) {
        flagSet.add(flag);
    }

    public void removeFlag(T flag) {
        flagSet.remove(flag);
    }

    public boolean hasFlag(T flag) {
        return flagSet.contains(flag);
    }

}
