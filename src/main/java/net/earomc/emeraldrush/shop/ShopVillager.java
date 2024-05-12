package net.earomc.emeraldrush.shop;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.UUID;

public class ShopVillager {
    private final Location location;
    private Villager villager;

    public ShopVillager(Location location) {
        this.location = location;
    }

    public void spawn() {
        villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setAdult();
        villager.setProfession(Villager.Profession.PRIEST);
        villager.setCustomName("§e§lShop");
        villager.setCustomNameVisible(true);
        villager.setCanPickupItems(false);
        System.out.println("Spawning shop villager at " + location.toString());
    }

    public UUID getUniqueId() {
        return villager.getUniqueId();
    }
}
