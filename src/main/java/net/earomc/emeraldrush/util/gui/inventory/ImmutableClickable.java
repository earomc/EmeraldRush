package net.earomc.emeraldrush.util.gui.inventory;

import org.bukkit.inventory.ItemStack;

public class ImmutableClickable implements Clickable {
    private final ItemStack itemStack;
    private final ClickAction action;

    public ImmutableClickable(ItemStack itemStack, ClickAction action) {
        this.itemStack = itemStack;
        this.action = action;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public ClickAction action() {
        return action;
    }
}
