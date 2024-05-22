package net.earomc.emeraldrush.util.firework;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Supplier;

public class FireworkSpawner {
    private final Plugin plugin;
    private final RandomFireworkGenerator randomFireworkGenerator;
    private BukkitTask spawnTask;
    private int counter = 0;

    public FireworkSpawner(Plugin plugin) {
        this.plugin = plugin;
        this.randomFireworkGenerator = new RandomFireworkGenerator();
    }

    public void spawnRandomFirework(Location location) {
        World world = location.getWorld();
        Firework firework = world.spawn(location, Firework.class);

        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta = randomFireworkGenerator.randomizeMeta(fireworkMeta);
        firework.setFireworkMeta(fireworkMeta);
    }

    public void spawnRandomFireworks(Supplier<Location> location, int amount, int delayTicks) {
        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (counter >= amount) {
                spawnTask.cancel();
                counter = 0;
            }
            spawnRandomFirework(location.get());
            counter++;
        }, delayTicks, delayTicks);
    }
}
