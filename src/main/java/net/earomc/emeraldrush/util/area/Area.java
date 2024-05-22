package net.earomc.emeraldrush.util.area;

import org.bukkit.util.Vector;


public interface Area {
    boolean isInside(Vector vector);
    Vector getCenter();
    default boolean isOutside(Vector vector) {
        return !isInside(vector);
    }
}
