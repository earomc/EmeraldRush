package net.earomc.emeraldrush.map.handlers;

import net.earomc.emeraldrush.map.EmeraldSpawner;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class EmeraldSpawnerHandler {
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

}
