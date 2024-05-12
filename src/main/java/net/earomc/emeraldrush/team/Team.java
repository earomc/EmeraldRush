package net.earomc.emeraldrush.team;

import net.earomc.emeraldrush.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
     *
     * @param lives
     */
    public void addLives(int lives) {
        this.lives += lives;
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
}
