package net.earomc.emeraldrush;

import net.earomc.emeraldrush.map.MapManager;
import net.earomc.emeraldrush.team.Team;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class GameInstance {
    private final Set<Player> players = new HashSet<>();
    private Team team1;
    private Team team2;
    private final MapManager mapManager;

    private Phase currentPhase;


    public GameInstance(MapManager mapManager) {
        this.mapManager = mapManager;
        this.team1 = new Team();
        this.team2 = new Team();
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

    public Set<Player> getPlayers() {
        return players;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void setPhase(Phase phase) {
        handlePhaseChange(currentPhase, phase);
        this.currentPhase = phase;
    }

    private void handlePhaseChange(Phase previousPhase, Phase newPhase) {
        // handle ending of previous phase
        switch (previousPhase) {
            case LOBBY:
                break;
        }
        // handle start of new phase
        switch (newPhase) {
            case LOBBY:
                break;
            case IN_GAME:
                // tp players to arena
                team1.getPlayers().forEach(player -> {
                    //TODO: Equip players.
                    player.teleport(mapManager.getInGameMap().getSpawnLocationTeam1());
                });
                team2.getPlayers().forEach(player -> {
                    //TODO: Equip players.
                    player.teleport(mapManager.getInGameMap().getSpawnLocationTeam2());
                });
                break;
            default:
                //
        }
    }


}
