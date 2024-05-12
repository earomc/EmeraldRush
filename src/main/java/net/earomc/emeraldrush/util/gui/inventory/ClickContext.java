package net.earomc.emeraldrush.util.gui.inventory;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * Contains all necessary information of an InventoryClickEvent that is passed by through
 * by a {@link InventoryGuiListener} to the inventory that is clicked if that inventory is tracked by the
 * {@link InventoryGuiTracker} (That should always be the case though).
 * The object is handled internally in each inventory and calls the
 */
public class ClickContext {
    private final int slotId;
    private final ClickType clickType;
    private final InventoryAction inventoryAction;
    private final InventoryType.SlotType slotType;
    private final ItemStack cursor;

    public ClickContext(ClickType clickType, InventoryAction inventoryAction, int slotId, InventoryType.SlotType slotType, ItemStack cursor) {
        this.clickType = clickType;
        this.inventoryAction = inventoryAction;
        this.slotId = slotId;
        this.slotType = slotType;
        this.cursor = cursor;
    }

    public int slotId() {
        return slotId;
    }

    public ClickType clickType() {
        return clickType;
    }

    public InventoryAction inventoryAction() {
        return inventoryAction;
    }

    public InventoryType.SlotType slotType() {
        return slotType;
    }

    public ItemStack cursor() {
        return cursor;
    }
}
