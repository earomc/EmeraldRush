package net.earomc.emeraldrush.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LobbyMap {
    private Location spawnLocation;
    public LobbyMap(String worldName, Vector spawnLocation) {
        this.spawnLocation = new Location(
                Bukkit.getWorld(worldName),
                spawnLocation.getX(),
                spawnLocation.getY(),
                spawnLocation.getZ());
    }
    public LobbyMap(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }
}
