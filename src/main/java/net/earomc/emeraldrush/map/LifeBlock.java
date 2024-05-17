package net.earomc.emeraldrush.map;

import net.earomc.emeraldrush.EmeraldRush;
import net.earomc.emeraldrush.config.EmeraldRushConfig;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LifeBlock {
    private final Location location;

    public LifeBlock(Location location) {
        this.location = location;
    }

    public void setLives(int lives) {
        if (lives > EmeraldRushConfig.MAX_LIVES) throw new IllegalStateException("Lives can't be above " + EmeraldRushConfig.MAX_LIVES);
        location.getWorld().playEffect(location, Effect.FLAME, null, 4);
        for (Player player : EmeraldRush.instance().getGameInstance().getPlayers()) {
            player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1f, 1f);
        }
        setBlock(lives);
        // send flame particles
        // play dragon hurt sound
        // at 10 lives: diamond
        // at 9-6: emerald
        // at 5-4: redstone
        // at 3-1: coal
    }

    public void setBlock(int lives) {
        Material material;
        if (lives <= 3) {
            material = Material.COAL;
        } else if (lives <= 5) {
            material = Material.REDSTONE_BLOCK;
        } else if (lives <= 9) {
            material = Material.EMERALD_BLOCK;
        } else {
            material = Material.DIAMOND_BLOCK;
        }
        location.getBlock().setType(material);
    }

    public Location getLocation() {
        return location;
    }
}
