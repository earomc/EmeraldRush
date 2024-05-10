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
    private boolean idling = false; // is either idling or in final countdown.
    private int count = EmeraldRushConfig.FINAL_COUNTDOWN_SECONDS;



    public LobbyCountdown(Plugin plugin, GameInstance gameInstance) {
        this.plugin = plugin;
        this.gameInstance = gameInstance;
    }

    public void startFinalCountdown() {
        if (idling) {
            idleLoopTask.cancel();
        }
        finalCountdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            idling = false;
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
                    // todo: check if teams can be
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
        if (!idling) {
            finalCountdownTask.cancel();;
        }
        idleLoopTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            idling = true;
        }, 0L, EmeraldRushConfig.IDLE_LOOP_DURATION_SECONDS * 20L);
    }
}
