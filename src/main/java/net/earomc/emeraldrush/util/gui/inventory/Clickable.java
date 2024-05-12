package net.earomc.emeraldrush.util.gui.inventory;

import org.bukkit.inventory.ItemStack;

/**
 * A clickable {@link ItemStack} in an {@link InventoryGui} basically.
 * Always has a {@link ClickAction} tied to it.
 */
public interface Clickable {
    ItemStack itemStack();
    ClickAction action();
}
