package net.earomc.emeraldrush.team;

import net.earomc.emeraldrush.events.LivesUpdateEvent;
import net.earomc.emeraldrush.map.LifeBlock;
import net.earomc.emeraldrush.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.earomc.emeraldrush.config.EmeraldRushConfig.MAX_LIVES;
import static net.earomc.emeraldrush.config.EmeraldRushConfig.START_LIVES;

public class Team {
    private final List<Player> players = new ArrayList<>();
    private final Set<Player> deadPlayers = new HashSet<>(); // players that are dead. not being able to respawn.
    private int size;
    private int lives = START_LIVES;
    public Team(int size) {
        this.size = size;
    }

    public Team() {
    }

    private LifeBlock lifeBlock;

    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Can be negative to subtract lives.
     *
     * @param lives
     * @return On trying to add lives, how many lives there were above the max lives.
     */
    public int addLives(int lives) {
        int before = this.lives;
        int newLives = this.lives + lives;
        if (newLives <= MAX_LIVES && newLives >= 0) {
            this.lives = newLives;
            this.lifeBlock.setLives(this.lives);
            Bukkit.getPluginManager().callEvent(new LivesUpdateEvent(this, before, this.lives));
            return 0;
        } else {
            return newLives - MAX_LIVES;
        }
    }

    public void setDead(Player player) {
        if (players.contains(player)) {
            this.deadPlayers.add(player);
        } else throw new IllegalArgumentException("Player is not part of the team and therefore cannot be set dead for this team.");
    }

    /**
     * @param livesCost The cost of the item.
     * @param buyer The buyer of the item. Has to be in this team.
     * @return Returns true if an item with the cost could be bought, false if not.
     */
    public boolean purchaseItem(int livesCost, Player buyer) {
        if (!players.contains(buyer))
            throw new IllegalArgumentException("Buyer " + buyer.getPlayer().getName() + "not in team");
        int emeralds = InventoryUtil.countEmeralds(buyer.getInventory());
        if (emeralds + lives >= livesCost) {
            int emeraldsRemoved = InventoryUtil.removeItemsFromInventory(buyer.getInventory(), livesCost, itemStack -> itemStack != null && itemStack.getType() == Material.EMERALD);
            addLives(- (livesCost - emeraldsRemoved));
            return true;
        }
        return false;
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

    public int getSize() {
        return size;
    }

    public LifeBlock getLifeBlock() {
        return lifeBlock;
    }

    public void setLifeBlock(LifeBlock lifeBlock) {
        this.lifeBlock = lifeBlock;
    }

    public boolean isAllDead() {
        return this.players.size() == this.deadPlayers.size();
    }

    public boolean isDead(Player player) {
        return this.deadPlayers.contains(player);
    }
}
