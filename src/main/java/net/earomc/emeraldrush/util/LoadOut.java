package net.earomc.emeraldrush.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;


/**
 * A load-out is basically something you can apply to a player. It is used to be saved somewhere.
 * For example, you might have a LobbyLoadOut that is always loaded when a player joins the lobby.
 * You have all these static methods here you can use to reset the player first.
 * This way you have a clean slate before setting all your custom items.
 */
@FunctionalInterface
public interface LoadOut {

    void load(Player player);

    default void load(Player player, boolean update) {
        load(player);
        if (update) {
            player.updateInventory();
        }
    }

    static void reset(Player player) {
        PlayerInventory inv = player.getInventory();
        clearInventoryContents(player);
        clearArmorContents(player);
        removeAllPotionEffects(player);
        resetAttributes(player);
        resetFlight(player);
        resetScoreboard(player);
        player.setGameMode(Bukkit.getDefaultGameMode());
    }

    static void resetAttributes(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.setTotalExperience(0);
        player.setExhaustion(0);
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.resetMaxHealth();
        player.resetPlayerWeather();
        player.resetPlayerTime();
    }

    static void resetScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    static void resetFlight(Player player) {
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    static void removeAllPotionEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    static void clearArmorContents(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.setArmorContents(null); // this works
    }

    static void clearInventoryContents(Player player) {
        PlayerInventory inv = player.getInventory();
        inv.setContents(new ItemStack[0]);
        // inv.setContents(null); this doesnt
    }

}
