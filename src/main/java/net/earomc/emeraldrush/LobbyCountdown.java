package net.earomc.emeraldrush;

import net.earomc.emeraldrush.config.EmeraldRushConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class LobbyCountdown {
    private final Plugin plugin;
    private final GameInstance gameInstance;
    private BukkitTask finalCountdownTask;
    private BukkitTask idleLoopTask;
    private int count = EmeraldRushConfig.FINAL_COUNTDOWN_SECONDS;

    public LobbyCountdown(Plugin plugin, GameInstance gameInstance) {
        this.plugin = plugin;
        this.gameInstance = gameInstance;
    }

    public void startFinalCountdown() {
        if (idleLoopTask != null) {
            idleLoopTask.cancel();
        }
        finalCountdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            switch (count) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 10:
                case 20:
                case 30:
                    Bukkit.broadcastMessage("§cGame is starting in §b" + count + " §cseconds");
                    break;
                case 1:
                    Bukkit.broadcastMessage("§cGame is starting in §b1 §csecond");
                    break;
                case 0: {
                    if (gameInstance.assignPlayersToTeams()) {
                        gameInstance.setPhase(Phase.IN_GAME);
                        finalCountdownTask.cancel();
                    } else {
                        startIdleLoop();
                        Bukkit.broadcastMessage("§cCould not start game due to uneven player distribution");
                    }
                    break;
                }
            }
            count--;
        }, 0L, 20L);
    }
    public void startIdleLoop() {
        if (finalCountdownTask != null) {
            finalCountdownTask.cancel();
        }
        this.idleLoopTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Bukkit.broadcastMessage(EmeraldRushConfig.MIN_PLAYERS - gameInstance.getPlayers().size() + " players missing to start");
        }, 0L, EmeraldRushConfig.IDLE_LOOP_DURATION_SECONDS * 20L);
    }
}
