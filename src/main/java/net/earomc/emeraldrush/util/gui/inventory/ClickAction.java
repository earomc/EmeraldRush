package net.earomc.emeraldrush.util.gui.inventory;

import org.bukkit.entity.Player;

/**
 * Something that happens when you click an ItemStack/{@link Clickable} in an {@link InventoryGui}.
 * It's a functional interface and can therefore be expressed as a lambda expression:
 * <p>Code example:</p>
 * <code>
 * (player, context) -> player.sendMessage("Hey! You clicked on slot " + context.slotId() + "!")
 * </code>
 */
@FunctionalInterface
public interface ClickAction {
    void perform(Player player, ClickContext context);
}
