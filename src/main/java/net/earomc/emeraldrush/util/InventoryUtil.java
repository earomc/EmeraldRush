package net.earomc.emeraldrush.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class InventoryUtil {

    public static int countEmeralds(PlayerInventory playerInventory) {
        int count = 0;
        for (ItemStack content : playerInventory.getContents()) {
            if (content != null && content.getType() == Material.EMERALD) {
                count += content.getAmount();
            }
        }
        return count;
    }

    /**
     * Removes "amountToRemove" items from the given inventory that are similar to the itemStack.
     * Uses the {@link ItemStack#isSimilar(ItemStack)} method from Bukkit.
     *
     * @param inventory      The inventory to remove the items from.
     * @param amountToRemove The amount of items to remove.
     * @param itemStack      The itemStack that will be removed from the inventory.
     * @return Returns how many items it removed from the inventory.
     */
    public static int removeItemsFromInventory(Inventory inventory, int amountToRemove, ItemStack itemStack) {
        return removeItemsFromInventory(inventory, amountToRemove, itemStack1 -> itemStack1 != null && itemStack1.isSimilar(itemStack));
    }

    /**
     * Removes "amountToRemove" items from the given inventory that match the given predicate.
     *
     * @param inventory      The inventory to remove the items from.
     * @param amountToRemove The amount of items to remove.
     * @param predicate      The predicate under which the item will be removed.
     * @return Returns how many items it removed from the inventory.
     */
    public static int removeItemsFromInventory(Inventory inventory, int amountToRemove, Predicate<ItemStack> predicate) {
        HashMap<Integer, ItemStack> slotsWithItem = new HashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (predicate.test(item)) {
                slotsWithItem.put(i, item);
            }
        }
        int itemsRemoved = 0;
        for (Map.Entry<Integer, ItemStack> entry : slotsWithItem.entrySet()) {
            ItemStack item = entry.getValue();
            int slot = entry.getKey();
            int amount = item.getAmount();
            int newAmount = amount - amountToRemove;
            if (newAmount > 0) {
                item.setAmount(newAmount);
                inventory.setItem(slot, item);
                itemsRemoved += amountToRemove;
                return itemsRemoved;
            } else {
                inventory.setItem(slot, null);
                if (newAmount == 0) {
                    itemsRemoved += amountToRemove;
                    return itemsRemoved;
                } else {
                    amountToRemove = -newAmount;
                }
            }
        }
        return itemsRemoved;
    }

}
