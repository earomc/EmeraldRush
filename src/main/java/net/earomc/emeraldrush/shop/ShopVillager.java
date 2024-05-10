package net.earomc.emeraldrush.shop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Villager;

public class ShopVillager {
    private final Location location;

    public ShopVillager(Location location) {
        this.location = location;
    }

    public void spawn() {
        Villager villager = location.getWorld().spawn(location, Villager.class);
        villager.setAdult();
        villager.setProfession(Villager.Profession.PRIEST);
        villager.setCustomName("§e§lShop");
        villager.setCustomNameVisible(true);
    }
}
