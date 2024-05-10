package net.earomc.emeraldrush.map;

import org.bukkit.Location;

public class LifeBlock {
    private Location location;

    public LifeBlock(Location location) {
        this.location = location;
    }

    public void updateState() {

    }

    public Location getLocation() {
        return location;
    }
}
