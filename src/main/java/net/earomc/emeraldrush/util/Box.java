package net.earomc.emeraldrush.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Box {
    private final int xMin;
    private final int yMin;
    private final int zMin;

    private final int xMax;
    private final int yMax;
    private final int zMax;

    public Box(Vector location1, Vector location2) {
        xMin = Math.min(location1.getBlockX(), location2.getBlockX());
        yMin = Math.min(location1.getBlockY(), location2.getBlockY());
        zMin = Math.min(location1.getBlockZ(), location2.getBlockZ());

        xMax = Math.max(location1.getBlockX(), location2.getBlockX());
        yMax = Math.max(location1.getBlockY(), location2.getBlockY());
        zMax = Math.max(location1.getBlockZ(), location2.getBlockZ());

        //System.out.println("Box: " + xMin + "," + yMin+ "," + zMin + " / " + xMax+ "," + yMax+ "," + zMax);
    }

    public Vector getCenter() {
        return new Vector(xMin + ((xMax - xMin) / 2), yMin + ((yMax - yMin) / 2), zMin + ((zMax - zMin) / 2));
    }

    public boolean isInside(Location location) {
        return isInside(location.getX(), location.getY(), location.getZ());
    }

    public boolean isInside(Vector location) {
        return isInside(location.getX(), location.getY(), location.getZ());
    }

    public boolean isInside(double x, double y, double z) {
        return isBetween(x, xMin, xMax) && isBetween(y, yMin, yMax) && isBetween(z, zMin, zMax);
    }

    /**
     * Returns true if arg is between the lower and upper value or equal to the lower or equal value.
     * @param arg Input
     * @param lower lower
     * @param upper upper
     * @return true if arg is between the lower and upper value or equal to the lower or equal value else returns false.
     */
    private boolean isBetween(double arg, int lower, int upper) {
        double _lower = Math.min(lower, upper);
        double _upper = Math.max(lower, upper);
        return (_lower <= arg && arg <= _upper);
    }
}
