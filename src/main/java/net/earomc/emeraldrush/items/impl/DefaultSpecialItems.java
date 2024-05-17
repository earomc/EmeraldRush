package net.earomc.emeraldrush.items.impl;

import net.earomc.emeraldrush.items.SpecialItemRegistry;
import net.earomc.emeraldrush.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DefaultSpecialItems {

    private SpecialItemRegistry registry;

    public static ItemStack TNT = new ItemBuilder(Material.TNT).name("§e§lTNT").build();
    public static ItemStack INSTA_BOOM_TNT = new ItemBuilder(Material.TNT).name("§e§lInsta-Boom TNT").build();
    public static ItemStack FIREBALL = new ItemBuilder(Material.FIREBALL).name("§e§lFireball").build();

    public DefaultSpecialItems(SpecialItemRegistry specialItemRegistry) {
        this.registry = specialItemRegistry;
    }

    public void register() {
        registry.registerSpecialItem(TNT, event -> {
            event.setCancelled(true);
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;
            Location location = clickedBlock.getRelative(BlockFace.UP).getLocation();
            TNTPrimed tntPrimed = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
            tntPrimed.setFuseTicks(20 * 3);
        });
        registry.registerSpecialItem(INSTA_BOOM_TNT, event -> {
            event.setCancelled(true);
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;
            Location location = clickedBlock.getRelative(BlockFace.UP).getLocation();
            TNTPrimed tntPrimed = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
            tntPrimed.setFuseTicks(0);
        });
        registry.registerSpecialItem(FIREBALL, event -> {
            event.setCancelled(true);
            Location ballLocation;
            Location playerLocation = event.getPlayer().getLocation();
            Vector direction = playerLocation.getDirection();
            if (event.getClickedBlock() == null) {
                ballLocation = playerLocation.clone().add(direction.clone().multiply(2));
            } else {
                ballLocation = event.getPlayer().getLocation();
            }
            Fireball fireball = (Fireball) playerLocation.getWorld().spawnEntity(ballLocation, EntityType.FIREBALL);
            fireball.setYield(2f);
            fireball.setIsIncendiary(false);
            fireball.setVelocity(direction);
        });
    }
}
