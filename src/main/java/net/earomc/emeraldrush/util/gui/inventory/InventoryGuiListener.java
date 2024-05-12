package net.earomc.emeraldrush.util.gui.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryGuiListener implements Listener {

    private final InventoryGuiTracker guis;

    public InventoryGuiListener() {
        this.guis = InventoryGuiTracker.instance();
    }

    @EventHandler
    public void onClickGuiSlot(InventoryClickEvent event) {
        //Bukkit.broadcast(Text.white("InventoryClickEvent called!"));
        Player player = (Player) event.getWhoClicked();
        SingleInventoryGui gui = guis.getGui(player);

        if (gui != null) {
            event.setCancelled(true);

            //Bukkit.broadcast("InventoryClickEvent cancelled from gui " + gui + "!");
            ClickContext context = new ClickContext(
                    event.getClick(),
                    event.getAction(),
                    event.getRawSlot(),
                    event.getSlotType(),
                    event.getCursor()
            );
            gui.onClick(player, context);
        } //else Bukkit.broadcast("Gui from player" + player.getName() + " is null!");

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryGui gui = guis.getGui(player);
        if (gui != null) {
            guis.onCloseGui(player);
        }
    }

}
