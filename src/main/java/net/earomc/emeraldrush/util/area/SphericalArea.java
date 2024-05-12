package net.earomc.emeraldrush.util.area;

import org.bukkit.Location;

public class SphericalArea implements Area {
    private final Location center;
    private final int radius;

    public SphericalArea(Location center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean isInside(Location location) {
        return location.distance(center) <= radius;
    }

}
