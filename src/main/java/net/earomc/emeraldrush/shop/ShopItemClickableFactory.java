package net.earomc.emeraldrush.shop;

import net.earomc.emeraldrush.GameInstance;
import net.earomc.emeraldrush.util.ItemBuilder;
import net.earomc.emeraldrush.util.PlayerItemOverflowQueueManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ShopItemClickableFactory {
    private final PlayerItemOverflowQueueManager itemOverflowQueue;
    private final GameInstance gameInstance;

    public ShopItemClickableFactory(PlayerItemOverflowQueueManager itemOverflowQueue, GameInstance gameInstance) {
        this.itemOverflowQueue = itemOverflowQueue;
        this.gameInstance = gameInstance;
    }

    public ShopItemClickable createShopItem(String title, ItemStack icon, int livesCost, ItemStack... purchaseableItems) {
        return createShopItem(title, icon, livesCost, player -> itemOverflowQueue.addItem(player, purchaseableItems));
    }

    public ShopItemClickable createShopItem(String title, ItemStack icon, int livesCost, Consumer<Player> onPurchase) {
        return new ShopItemClickable(gameInstance,
                title,
                new ItemBuilder.ItemEditor(icon).name(title).setLore(" ", "§eCost: §a" + livesCost + " Lives", " ", "§eClick to purchase!").build(),
                livesCost,
                onPurchase);
    }
}
