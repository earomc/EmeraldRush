package net.earomc.emeraldrush.items;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SpecialItemRegistry {

    private HashMap<ItemStack, Consumer<PlayerInteractEvent>> specialItems = new HashMap<>();

    public void registerSpecialItem(ItemStack itemStack, Consumer<PlayerInteractEvent> onInteract) {
        specialItems.put(itemStack, onInteract);
    }

    public boolean isSpecialItem(ItemStack itemStack) {
        return getSpecialItem(itemStack) != null;
    }

    public Consumer<PlayerInteractEvent> getSpecialItem(ItemStack itemStack) {
        for (Map.Entry<ItemStack, Consumer<PlayerInteractEvent>> entry : specialItems.entrySet()) {
            if (entry.getKey().isSimilar(itemStack)) return entry.getValue();
        }
        return null;
    }

}
