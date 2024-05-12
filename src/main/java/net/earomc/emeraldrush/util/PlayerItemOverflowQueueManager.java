package net.earomc.emeraldrush.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class PlayerItemOverflowQueueManager implements Listener {

    public PlayerItemOverflowQueueManager() {
    }

    private final Map<Player, Queue<ItemStack>> playerQueueMap = new HashMap<>();

    public void addItem(Player player, ItemStack... itemStacks) {
        Map<Integer, ItemStack> overflownItems = player.getInventory().addItem(itemStacks);
        if (overflownItems.isEmpty()) return;
        Queue<ItemStack> queue = getQueue(player);
        overflownItems.forEach((slot, item) -> queue.offer(item));
    }

    @NotNull
    private Queue<ItemStack> getQueue(Player player) {
        return playerQueueMap.computeIfAbsent(player, p -> new ArrayDeque<>());
    }

    private boolean hasQueue(Player player) {
        return playerQueueMap.containsKey(player);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        addAsManyQueuedItemsAsPossible(player);
    }

    /**
     * When a player drops an item this method is called. It adds as many queued items as possible to a player's inventory and then removes
     * the items that could be added from the queue.
     * To check if the method can add an item to the inventory, the overflown items
     * @param player
     */
    private void addAsManyQueuedItemsAsPossible(Player player) {
        // peek and collect items from queue, that can fit until an item can't fit
        if (!hasQueue(player)) return;
        Queue<ItemStack> queue = getQueue(player);
        if (queue.isEmpty()) return;
        PlayerInventory inventory = player.getInventory();
        Queue<ItemStack> copiedQueue = new ArrayDeque<>(queue);

        boolean spaceForItem = true;
        while (spaceForItem) {
            ItemStack queuedItem = copiedQueue.poll();
            if (queuedItem == null) {
                break;
            }
            Map<Integer, ItemStack> overflownItems = inventory.addItem(queuedItem);
            spaceForItem = overflownItems.isEmpty();
            if (spaceForItem) {
                queue.remove(queuedItem);
            }
        }

        if (queue.isEmpty()) {
            playerQueueMap.remove(player);
        }
    }
}
