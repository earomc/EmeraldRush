package net.earomc.emeraldrush.map;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class InGameMap {
    private List<Location> emeraldSpawns = new ArrayList<>();

    private Location spawnLocationTeam1;
    private Location spawnLocationTeam2;

    private LifeBlock lifeBlock1;
    private LifeBlock lifeBlock2;


    public InGameMap(List<Location> emeraldSpawns, Location spawnLocationTeam1, Location spawnLocationTeam2, Location lifeBlockLocation1, Location lifeBlockLocation2) {
        this.emeraldSpawns = emeraldSpawns;
        this.spawnLocationTeam1 = spawnLocationTeam1;
        this.spawnLocationTeam2 = spawnLocationTeam2;

        this.lifeBlock1 = new LifeBlock(lifeBlockLocation1);
        this.lifeBlock2 = new LifeBlock(lifeBlockLocation2);
    }

    public List<Location> getEmeraldSpawns() {
        return emeraldSpawns;
    }

    public Location getSpawnLocationTeam1() {
        return spawnLocationTeam1;
    }

    public Location getSpawnLocationTeam2() {
        return spawnLocationTeam2;
    }

    public LifeBlock getLifeBlock1() {
        return lifeBlock1;
    }

    public LifeBlock getLifeBlock2() {
        return lifeBlock2;
    }
}
