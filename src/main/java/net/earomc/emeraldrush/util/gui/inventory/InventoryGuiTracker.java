package net.earomc.emeraldrush.util.gui.inventory;

import net.earomc.emeraldrush.EmeraldRush;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Static singleton that can be accessed through {@link InventoryGuiTracker#instance()}.
 * <p>Is called internally by all inventory GUIs and is used to track
 * which player is using which gui.
 * </p>
 * <p>
 * Can be used to see whether or not a player is using a specific GUI that
 * or any GUI that is tracked here.
 * </p>
 */
public final class InventoryGuiTracker {

    private static InventoryGuiTracker INSTANCE;
    private final HashMap<Player, SingleInventoryGui> guiMap;

    private InventoryGuiTracker() {
        guiMap = new HashMap<>();
    }

    public static InventoryGuiTracker instance() {
        if (INSTANCE == null) {
            INSTANCE = new InventoryGuiTracker();
            EmeraldRush.instance().getLogger().log(Level.ALL, "Created new InventoryGuis instance!");
        }
        return INSTANCE;
    }

    public boolean isUsingGui(Player player) {
        return guiMap.containsKey(player);
    }

    @Nullable
    public SingleInventoryGui getGui(Player player) {
        return guiMap.get(player);
    }

    public boolean isUsingGui(Player player, SingleInventoryGui gui) {
        InventoryGui current = guiMap.get(player);
        return current != null && current.equals(gui);
    }

    void onOpenGui(SingleInventoryGui gui, Player player) {
        //calling onCloseGui just to make sure no other inventory is set as active or still exists in open state.
        onCloseGui(player);
        setActiveGui(player, gui);
    }

    private void setActiveGui(Player player, SingleInventoryGui gui) {
        guiMap.put(player, gui);
    }

    /**
     * Meant to be called from a listener when player closes their inventory.
     *
     * @param player the player whose inventory is being closed.
     */

    void onCloseGui(Player player) {
        SingleInventoryGui removed = guiMap.remove(player);
        if (removed != null) {
            removed.onClose(player);
        }
    }

}
