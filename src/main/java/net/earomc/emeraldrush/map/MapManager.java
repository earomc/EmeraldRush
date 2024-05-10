package net.earomc.emeraldrush.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public class MapManager {
    private InGameMap inGameMap;
    private LobbyMap lobbyMap;

    public MapManager() {
        //TODO: Select maps from config or smth...
        List<Location> emeraldSpawns = Arrays.asList(
                createIgLoc(5, 10, 10),
                createIgLoc(-5, 10, -10),
                createIgLoc(0, 10, 0));

        Location spawnLocationTeam1 = createIgLoc(5, 10, 15);
        Location spawnLocationTeam2 = createIgLoc(-5, 10, -15);

        Location lifeBlockLocation1 = createIgLoc(0, 10, 15);
        Location lifeBlockLocation2 = createIgLoc(0, 10, -15);

        this.inGameMap = new InGameMap(emeraldSpawns, spawnLocationTeam1, spawnLocationTeam2, lifeBlockLocation1, lifeBlockLocation2);

        this.lobbyMap = new LobbyMap(createLbLoc(0, 10, 0));

    }

    //TODO: Remove temporary methods.
    private Location createIgLoc(int x, int y, int z) {
        return new Location(Bukkit.getWorld("ingame"), x, y, z);
    }
    private Location createLbLoc(int x, int y, int z) {
        return new Location(Bukkit.getWorld("lobby"), x, y, z);
    }


    public InGameMap getInGameMap() {
        return inGameMap;
    }

    public LobbyMap getLobbyMap() {
        return lobbyMap;
    }
}
