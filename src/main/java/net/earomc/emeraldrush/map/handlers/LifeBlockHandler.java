package net.earomc.emeraldrush.map.handlers;

import net.earomc.emeraldrush.map.LifeBlock;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LifeBlockHandler implements Listener {
    private final LifeBlock lifeBlock1;
    private final LifeBlock lifeBlock2;

    public LifeBlockHandler(LifeBlock lifeBlock1, LifeBlock lifeBlock2) {
        this.lifeBlock1 = lifeBlock1;
        this.lifeBlock2 = lifeBlock2;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (location.equals(lifeBlock1.getLocation())) {
            event.setCancelled(true);
            lifeBlock1.updateState();
        }
        if (location.equals(lifeBlock2.getLocation())) {
            event.setCancelled(true);
            lifeBlock2.updateState();
        }
    }
}
