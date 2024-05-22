package net.earomc.emeraldrush.map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class EmeraldSpawner {

    private final Location location;

    public EmeraldSpawner(Location location) {
        this.location = location;
    }

    public void spawnEmerald() {
        Entity entity = location.getWorld().dropItemNaturally(location, new ItemStack(Material.EMERALD));
    }

    public Location getLocation() {
        return location;
    }
}
