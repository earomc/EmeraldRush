package net.earomc.emeraldrush.util.area;

import net.earomc.emeraldrush.util.Box;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BoxArea implements Area {
    private final Box box;

    public BoxArea(Box box) {
        this.box = box;
    }

    public BoxArea(Vector location1, Vector location2) {
        this.box = new Box(location1, location2);
    }

    public BoxArea(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.box = new Box(new Vector(x1, y1, z1), new Vector(x2, y2, z2));
    }

    @Override
    public boolean isInside(Location location) {
        return box.isInside(location);
    }
}
