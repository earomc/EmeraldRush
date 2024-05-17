package net.earomc.emeraldrush.map.handlers;

import net.earomc.emeraldrush.GameInstance;
import net.earomc.emeraldrush.map.LifeBlock;
import net.earomc.emeraldrush.team.Team;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LifeBlockHandler implements Listener {
    private final LifeBlock lifeBlock1;
    private final LifeBlock lifeBlock2;

    private final GameInstance gameInstance;

    public LifeBlockHandler(LifeBlock lifeBlock1, LifeBlock lifeBlock2, GameInstance gameInstance) {
        this.lifeBlock1 = lifeBlock1;
        this.lifeBlock2 = lifeBlock2;
        this.gameInstance = gameInstance;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (location.equals(lifeBlock1.getLocation())) {
            event.setCancelled(true);
            Team team1 = gameInstance.getTeam1();
            team1.addLives(-1);
            lifeBlock1.setLives(team1.getLives());
        }
        if (location.equals(lifeBlock2.getLocation())) {
            event.setCancelled(true);
            Team team2 = gameInstance.getTeam2();
            team2.addLives(-1);
            lifeBlock1.setLives(team2.getLives());
        }
    }
}
