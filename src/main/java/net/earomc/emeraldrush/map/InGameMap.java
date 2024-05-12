package net.earomc.emeraldrush.map;

import net.earomc.emeraldrush.shop.ShopVillager;
import net.earomc.emeraldrush.util.area.Area;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public class InGameMap {
    private final List<EmeraldSpawner> emeraldSpawners;

    private final Location spawnLocationTeam1;
    private final Location spawnLocationTeam2;

    private final Area emeraldDepositArea1;
    private final Area emeraldDepositArea2;

    private final LifeBlock lifeBlock1;
    private final LifeBlock lifeBlock2;

    private final Location shopLocation1;
    private final Location shopLocation2;

    private final Area bound;

    public InGameMap(List<Location> emeraldSpawns,
                     Location spawnLocationTeam1,
                     Location spawnLocationTeam2,
                     Area emeraldDepositArea1,
                     Area emeraldDepositArea2,
                     Location lifeBlockLocation1,
                     Location lifeBlockLocation2,
                     Location shopLocation1,
                     Location shopLocation2,
                     Area bound
    ) {
        this.emeraldSpawners = emeraldSpawns.stream().map(EmeraldSpawner::new).collect(Collectors.toList());

        this.spawnLocationTeam1 = spawnLocationTeam1;
        this.spawnLocationTeam2 = spawnLocationTeam2;

        this.emeraldDepositArea1 = emeraldDepositArea1;
        this.emeraldDepositArea2 = emeraldDepositArea2;

        this.lifeBlock1 = new LifeBlock(lifeBlockLocation1);
        this.lifeBlock2 = new LifeBlock(lifeBlockLocation2);

        this.shopLocation1 = shopLocation1;
        this.shopLocation2 = shopLocation2;

        this.bound = bound;
    }

    public List<EmeraldSpawner> getEmeraldSpawners() {
        return emeraldSpawners;
    }

    public Area getEmeraldDepositArea1() {
        return emeraldDepositArea1;
    }

    public Area getEmeraldDepositArea2() {
        return emeraldDepositArea2;
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

    public void setup() {
        new ShopVillager(shopLocation1).spawn();
        new ShopVillager(shopLocation2).spawn();

        //TODO: Set up life blocks
    }

    public Area getBound() {
        return bound;
    }
}
