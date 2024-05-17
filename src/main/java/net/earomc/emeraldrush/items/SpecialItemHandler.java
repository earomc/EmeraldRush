package net.earomc.emeraldrush.items;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.function.Consumer;

public class SpecialItemHandler implements Listener {
    private SpecialItemRegistry itemRegistry;

    public SpecialItemHandler(SpecialItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Consumer<PlayerInteractEvent> specialItem = itemRegistry.getSpecialItem(event.getItem());
        if (specialItem == null) return;
        specialItem.accept(event);
    }
}
