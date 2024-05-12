package net.earomc.emeraldrush.util.area;

import org.bukkit.Location;

public interface Area {
    boolean isInside(Location location);
    default boolean isOutside(Location location) {
        return !isInside(location);
    }
}
