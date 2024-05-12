package net.earomc.emeraldrush;

import net.earomc.emeraldrush.loadouts.LoadOutTeam;
import net.earomc.emeraldrush.map.MapManager;
import net.earomc.emeraldrush.map.handlers.EmeraldSpawnerHandler;
import net.earomc.emeraldrush.scoreboard.impl.InGameScoreboard;
import net.earomc.emeraldrush.scoreboard.impl.InGameScoreboardManager;
import net.earomc.emeraldrush.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameInstance {
    private final ArrayList<Player> players = new ArrayList<>();
    private final Team team1;
    private final Team team2;
    private final MapManager mapManager;
    private final EmeraldSpawnerHandler emeraldSpawnerHandler;
    private final InGameScoreboardManager inGameScoreboardManager;
    private final LobbyCountdown lobbyCountdown;

    private Phase currentPhase;

    public GameInstance(MapManager mapManager, EmeraldSpawnerHandler emeraldSpawnerHandler, InGameScoreboardManager inGameScoreboardManager) {
        this.mapManager = mapManager;
        this.emeraldSpawnerHandler = emeraldSpawnerHandler;
        this.inGameScoreboardManager = inGameScoreboardManager;
        this.lobbyCountdown = new LobbyCountdown(EmeraldRush.instance(), this);
        this.team1 = new Team();
        this.team2 = new Team();
        setPhase(Phase.LOBBY);
    }

    public void registerPlayer(Player player) {
        players.add(player);
    }

    /**
     * Splits the players evenly into the teams.
     * @return Returns false if the players could not be split evenly
     */
    public boolean assignPlayersToTeams() {
        boolean isEven = players.size() % 2 == 0;
        if (!isEven) return false;

        int teamSize = players.size() / 2;
        team1.setSize(teamSize);
        team2.setSize(teamSize);

        int i = 0;
        for (Player player : players) {
            if (i < teamSize) {
                team1.addPlayer(player);
            } else {
                team2.addPlayer(player);
            }
            i++;
        }
        return true;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void setPhase(Phase phase) {
        //TODO: REMOVE dbg messages
        Bukkit.broadcastMessage("Set phase to: " + phase);
        System.out.println("SOUT: Set phase to: " + phase);

        handlePhaseChange(currentPhase, phase);
        this.currentPhase = phase;
    }

    private void handlePhaseChange(@Nullable Phase previousPhase, Phase newPhase) {
        // handle ending of previous phase
        if (previousPhase != null) {
            switch (previousPhase) {
                case LOBBY:
                    break;
                case IN_GAME:
                    emeraldSpawnerHandler.stopSpawning();
            }
        }
        // handle start of new phase
        switch (newPhase) {
            case LOBBY:
                lobbyCountdown.startIdleLoop();
                break;
            case IN_GAME:
                Bukkit.getScheduler().runTaskLater(EmeraldRush.instance(), () -> {
                    mapManager.getInGameMap().setup();
                }, 20 * 1L);

                // tp players to arena
                LoadOutTeam loadOutTeam1 = new LoadOutTeam(true);
                LoadOutTeam loadOutTeam2 = new LoadOutTeam(false);

                team1.getPlayers().forEach(player -> {
                    inGameScoreboardManager.setScoreboard(player, new InGameScoreboard(team1));
                    loadOutTeam1.load(player, true);
                    player.teleport(mapManager.getInGameMap().getSpawnLocationTeam1());
                });
                team2.getPlayers().forEach(player -> {
                    inGameScoreboardManager.setScoreboard(player, new InGameScoreboard(team2));
                    loadOutTeam2.load(player, true);
                    player.teleport(mapManager.getInGameMap().getSpawnLocationTeam2());
                });
                emeraldSpawnerHandler.startSpawning();
                break;
            default:
                //
        }
    }

    @Nullable
    public Team getTeam(Player player) {
        if (team1.getPlayers().contains(player)) return team1;
        if (team2.getPlayers().contains(player)) return team2;
        return null;
    }

    public boolean inTeam1(Player player) {
        if (team1.getPlayers().contains(player)) return true;
        if (team2.getPlayers().contains(player)) return false;
        throw new IllegalStateException("Player " + player.getName() + " should be in a team");
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public LobbyCountdown getLobbyCountdown() {
        return lobbyCountdown;
    }
}
