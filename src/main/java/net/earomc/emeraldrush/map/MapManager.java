package net.earomc.emeraldrush.map;

import net.earomc.emeraldrush.util.area.Area;
import net.earomc.emeraldrush.util.area.BoxArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapManager {
    private InGameMap inGameMap;
    private LobbyMap lobbyMap;

    public MapManager() {
        //TODO: Select maps from config or smth...

        Bukkit.createWorld(new WorldCreator("ingame"));
        Bukkit.createWorld(new WorldCreator("lobby"));


        //TODO: Remove debug msg
        Bukkit.getWorlds().forEach(world -> System.out.println(world.getName()));

        List<Location> emeraldSpawns = Arrays.asList(
                createIgLoc(5.5, 10.5, 10.5),
                createIgLoc(-4.5, 10.5, -9.5),
                createIgLoc(0.5, 10.5, 0.5));

        Location spawnLocationTeam1 = createIgLoc(5.5, 10.5, 15.5, -180);
        Location spawnLocationTeam2 = createIgLoc(-4.5, 10.5, -14.5, 0);

        Location lifeBlockLocation1 = createIgLoc(0.5, 10.5, 15.5);
        Location lifeBlockLocation2 = createIgLoc(0.5, 10.5, -14.5);

        Area emeraldDepositArea1 = new BoxArea(-1, 10, 14, 1, 15, 16);
        Area emeraldDepositArea2 = new BoxArea(1, 10, -14, -1, 15, -16);

        Location shopLocation1 = createIgLoc(-4.5, 10.5, 15.5, -90);
        Location shopLocation2 = createIgLoc(5.5, 10.5, -14.5, 90);

        this.inGameMap = new InGameMap(emeraldSpawns,
                spawnLocationTeam1,
                spawnLocationTeam2,
                emeraldDepositArea1,
                emeraldDepositArea2,
                lifeBlockLocation1,
                lifeBlockLocation2,
                shopLocation1,
                shopLocation2,
                new BoxArea(-30, 0, -30, 30, 30, 30)
        );

        this.lobbyMap = new LobbyMap(createLbLoc(0.5, 10, 0.5));
    }

    private Location createIgLoc(double x, double y, double z) {
        return new Location(Objects.requireNonNull(Bukkit.getWorld("ingame")), x, y, z);
    }

    private Location createIgLoc(double x, double y, double z, float yaw) {
        return new Location(Objects.requireNonNull(Bukkit.getWorld("ingame")), x, y, z, yaw,0);
    }
    private Location createLbLoc(double x, double y, double z) {
        return new Location(Bukkit.getWorld("lobby"), x, y, z);
    }


    public InGameMap getInGameMap() {
        return inGameMap;
    }

    public LobbyMap getLobbyMap() {
        return lobbyMap;
    }
}
