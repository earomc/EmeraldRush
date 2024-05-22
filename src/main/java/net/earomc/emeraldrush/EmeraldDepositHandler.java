package net.earomc.emeraldrush;

import net.earomc.emeraldrush.map.InGameMap;
import net.earomc.emeraldrush.team.Team;
import net.earomc.emeraldrush.util.InventoryUtil;
import net.earomc.emeraldrush.util.area.Area;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;

import static net.earomc.emeraldrush.config.EmeraldRushConfig.MAX_LIVES;

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

        if (depositArea1.isInside(locTo.toVector()) && depositArea1.isOutside(locFrom.toVector()) && team1.getPlayers().contains(player)) {
            depositEmeralds(player, team1);
        }
        if (depositArea2.isInside(locTo.toVector()) && depositArea2.isOutside(locFrom.toVector()) && team2.getPlayers().contains(player)) {
            depositEmeralds(player, team2);
        }
    }

    private void depositEmeralds(Player player, Team team) {
        PlayerInventory inventory = player.getInventory();
        int emeralds = InventoryUtil.countEmeralds(inventory);
        int emeraldsToDeposit = Math.max(0, Math.min(MAX_LIVES - team.getLives(), emeralds));
        if (emeraldsToDeposit > 0) {

            int emeraldsDeposited = InventoryUtil.removeItemsFromInventory(inventory, emeraldsToDeposit,
                    itemStack -> itemStack != null && itemStack.getType() == Material.EMERALD);
            team.addLives(emeraldsDeposited);
            player.sendMessage(ChatColor.GREEN + "Deposited " + emeraldsDeposited + " emeralds!");
            Bukkit.broadcastMessage("Team " + (gameInstance.isTeam1(team) ? "1" : "2") + " has " + team.getLives() + " lives.");
        }
    }
}
