package net.earomc.emeraldrush.map.handlers;

import net.earomc.emeraldrush.GameInstance;
import net.earomc.emeraldrush.Phase;
import net.earomc.emeraldrush.map.LifeBlock;
import net.earomc.emeraldrush.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LifeBlockHandler implements Listener {

    private final GameInstance gameInstance;

    public LifeBlockHandler(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        LifeBlock lifeBlock = gameInstance.getMapManager().getInGameMap().getLifeBlock(event.getBlock().getLocation());
        if (gameInstance.getCurrentPhase() != Phase.IN_GAME) return;
        if (lifeBlock == null) {
            //TODO: Remove debug msg
            Bukkit.broadcastMessage(event.getBlock() + " Loc: §a" + event.getBlock().getLocation());
        } else {
            Team team = gameInstance.getTeam(event.getPlayer());
            if (team == null) return;
            Team oppTeam = gameInstance.getOpponentTeam(team);
            if (lifeBlock == oppTeam.getLifeBlock()) {
                oppTeam.addLives(-1);
            }
        }
    }
}
