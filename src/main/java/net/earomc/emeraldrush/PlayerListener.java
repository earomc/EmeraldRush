package net.earomc.emeraldrush;

import net.earomc.emeraldrush.config.EmeraldRushConfig;
import net.earomc.emeraldrush.map.MapManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final GameInstance gameInstance;
    private final LobbyCountdown lobbyCountdown;
    private final MapManager mapManager;

    public PlayerListener(GameInstance gameInstance, LobbyCountdown lobbyCountdown, MapManager mapManager) {
        this.gameInstance = gameInstance;
        this.lobbyCountdown = lobbyCountdown;
        this.mapManager = mapManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        switch (gameInstance.getCurrentPhase()) {
            case IN_GAME:
                if (!player.hasPermission(Permissions.JOIN_DURING_GAME)) {
                    player.kickPlayer("§cTrying to join while in game");
                } else {
                    // handle spectator shit.
                }
                break;
            case LOBBY:
                gameInstance.registerPlayer(player);
                player.teleport(mapManager.getLobbyMap().getSpawnLocation());
                if (gameInstance.getPlayers().size() >= EmeraldRushConfig.MIN_PLAYERS) {
                    lobbyCountdown.startFinalCountdown();
                }
        }
    }
}
