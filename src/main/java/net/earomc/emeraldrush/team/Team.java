package net.earomc.emeraldrush.team;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private List<Player> players = new ArrayList<>();
    private int size;
    private int lives;

    public Team(int size) {
        this.size = size;
    }

    public Team() {
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Can be negative to subtract lives.
     * @param lives
     */
    public void addLives(int lives) {
        this.lives += lives;
    }

    public int getLives() {
        return lives;
    }

    public PlayerAddResult addPlayer(Player player) {
        if (players.size() >= this.size) {
            return PlayerAddResult.FAIL_TEAM_FULL;
        }
        players.add(player);
        return PlayerAddResult.SUCCESS;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public enum PlayerAddResult {
        FAIL_TEAM_FULL,
        SUCCESS
    }
}
