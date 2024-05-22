package net.earomc.emeraldrush.map.handlers;

import net.earomc.emeraldrush.config.EmeraldRushConfig;
import net.earomc.emeraldrush.map.EmeraldSpawner;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class EmeraldSpawnerHandler implements Listener {
    private final Plugin plugin;
    private final List<EmeraldSpawner> emeraldSpawners;
    private BukkitTask task;

    public EmeraldSpawnerHandler(Plugin plugin, List<EmeraldSpawner> emeraldSpawners) {
        this.plugin = plugin;
        this.emeraldSpawners = emeraldSpawners;
    }

    public void startSpawning() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (EmeraldSpawner spawner : emeraldSpawners) {
                spawner.spawnEmerald();
            }
        }, 20L * 15, 20L * 15);
    }

    public void stopSpawning() {
        task.cancel();
    }

    @EventHandler
    public void onPlaceBlockNearSpawner(BlockPlaceEvent event) {
        for (EmeraldSpawner emeraldSpawner : emeraldSpawners) {
            if (emeraldSpawner.getLocation().distance(event.getBlock().getLocation()) <= EmeraldRushConfig.EMERALD_SPAWNER_NO_BUILD_RADIUS) {
                event.setCancelled(true);
            }
        }
    }
}
