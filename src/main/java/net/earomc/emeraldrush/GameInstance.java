package net.earomc.emeraldrush;

import net.earomc.emeraldrush.loadouts.LoadOutTeam;
import net.earomc.emeraldrush.map.InGameMap;
import net.earomc.emeraldrush.map.LifeBlock;
import net.earomc.emeraldrush.map.MapManager;
import net.earomc.emeraldrush.map.handlers.EmeraldSpawnerHandler;
import net.earomc.emeraldrush.scoreboard.impl.InGameScoreboard;
import net.earomc.emeraldrush.scoreboard.impl.InGameScoreboardManager;
import net.earomc.emeraldrush.team.Team;
import net.earomc.emeraldrush.util.firework.FireworkSpawner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
     *
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
                InGameMap inGameMap = mapManager.getInGameMap();
                Bukkit.getScheduler().runTaskLater(EmeraldRush.instance(), () -> {
                    inGameMap.setup();
                    team1.setLifeBlock(inGameMap.getLifeBlock1());
                    team2.setLifeBlock(inGameMap.getLifeBlock2());
                }, 20 * 1L);

                // tp players to arena
                LoadOutTeam loadOutTeam1 = new LoadOutTeam(true);
                LoadOutTeam loadOutTeam2 = new LoadOutTeam(false);

                team1.getPlayers().forEach(player -> {
                    inGameScoreboardManager.setScoreboard(player, new InGameScoreboard(team1));
                    loadOutTeam1.load(player, true);
                    player.teleport(inGameMap.getSpawnLocationTeam1());
                });
                team2.getPlayers().forEach(player -> {
                    inGameScoreboardManager.setScoreboard(player, new InGameScoreboard(team2));
                    loadOutTeam2.load(player, true);
                    player.teleport(inGameMap.getSpawnLocationTeam2());
                });
                emeraldSpawnerHandler.startSpawning();
                break;
            case WINNING:
                Bukkit.getScheduler()
                        .runTaskLater(
                                EmeraldRush.instance(),
                                () -> Bukkit.getServer().shutdown(),
                                20L * 5
                        );
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

    public Team getOpponentTeam(Team team) {
        if (team == team1) return team2;
        if (team == team2) return team1;
        throw new IllegalArgumentException("Invalid team. " + team);
    }

    public boolean inTeam1(Player player) {
        if (team1.getPlayers().contains(player)) return true;
        if (team2.getPlayers().contains(player)) return false;
        throw new IllegalStateException("Player " + player.getName() + " should be in a team");
    }

    public boolean isTeam1(Team team) {
        if (team == team1) return true;
        if (team == team2) return false;
        throw new IllegalArgumentException("Unregistered team " + team);
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

    public void win(Team team) {
        FireworkSpawner fireworkSpawner = new FireworkSpawner(EmeraldRush.instance());
        for (Player player : team.getPlayers()) {
            fireworkSpawner.spawnRandomFireworks(player::getLocation, 5, 15);
        }
        if (team.getSize() > 1) {
            Bukkit.broadcastMessage("§aTeam " + (isTeam1(team) ? "1" : "2") + " has won the game!");
        } else {
            Bukkit.broadcastMessage("§a" + team.getPlayers().get(0).getName() + " has won the game!");
        }
        setPhase(Phase.WINNING);
    }

    public LifeBlock getLifeBlock(Team team) {
        if (team == team1) return mapManager.getInGameMap().getLifeBlock1();
        if (team == team2) return mapManager.getInGameMap().getLifeBlock2();
        throw new IllegalArgumentException("Invalid team. " + team);
    }

    public MapManager getMapManager() {
        return mapManager;
    }
}
