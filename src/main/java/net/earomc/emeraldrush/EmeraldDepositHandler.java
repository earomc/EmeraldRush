package net.earomc.emeraldrush;

import net.earomc.emeraldrush.map.InGameMap;
import net.earomc.emeraldrush.team.Team;
import net.earomc.emeraldrush.util.InventoryUtil;
import net.earomc.emeraldrush.util.area.Area;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;

public class EmeraldDepositHandler implements Listener {

    private final InGameMap inGameMap;
    private final GameInstance gameInstance;

    public EmeraldDepositHandler(InGameMap inGameMap, GameInstance gameInstance) {
        this.inGameMap = inGameMap;
        this.gameInstance = gameInstance;
    }

    @EventHandler
    public void onMoveIntoDepositArea(PlayerMoveEvent event) {
        Team team1 = gameInstance.getTeam1();
        Team team2 = gameInstance.getTeam2();

        Player player = event.getPlayer();
        Location locTo = event.getTo();
        Location locFrom = event.getFrom();
        Area depositArea1 = inGameMap.getEmeraldDepositArea1();
        Area depositArea2 = inGameMap.getEmeraldDepositArea2();

        if (depositArea1.isInside(locTo) && depositArea1.isOutside(locFrom) && team1.getPlayers().contains(player)) {
            depositEmeralds(player, team1);
        }
        if (depositArea2.isInside(locTo) && depositArea2.isOutside(locFrom) && team2.getPlayers().contains(player)) {
            depositEmeralds(player, team2);
        }
    }

    private void depositEmeralds(Player player, Team team) {
        PlayerInventory inventory = player.getInventory();
        int lives = InventoryUtil.countEmeralds(inventory);
        if (lives > 0) {
            inventory.remove(Material.EMERALD);
            team.addLives(lives);
            player.sendMessage(ChatColor.GREEN + "Deposited " + lives + " emeralds!");
        }
    }
}
