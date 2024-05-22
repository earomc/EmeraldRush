package net.earomc.emeraldrush.util.area;

import org.bukkit.util.Vector;

public class SphericalArea implements Area {
    private final Vector center;
    private final int radius;

    public SphericalArea(Vector center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean isInside(Vector location) {
        return location.distance(center) <= radius;
    }

    @Override
    public Vector getCenter() {
        return center;
    }
}
