package net.earomc.emeraldrush.util.gui.inventory;

import net.earomc.emeraldrush.EmeraldRush;
import net.earomc.emeraldrush.util.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * The super class of all Minecraft inventory-related GUIs.
 * Common implementations are {@link ChestGui} and {@link PagedGui}
 */
public abstract class InventoryGui implements Gui {

    @NotNull
    protected String title;

    @Nullable
    protected Consumer<Player> onCloseAction;
    @Nullable
    protected Consumer<Player> onOpenAction;

    public InventoryGui(@Nullable String title) {
        this.title = title != null ? title : "";
    }

    public void close(Player player) {
        player.closeInventory();
    }

    void onClose(Player player) {
        if (onCloseAction != null) {
            Bukkit.getScheduler().runTaskLater(EmeraldRush.instance(), () -> onCloseAction.accept(player), 1L);
        }
    }

    @Override
    public void open(Player player) {
        if (onOpenAction != null) {
            //Bukkit.getScheduler().runTaskLater(SMPCore.inst(), () -> {
                onOpenAction.accept(player);
            //}, 1L);
        }
    }

    public boolean hasSpace() {
        return firstEmpty() != -1;
    }

    public abstract void setClickable(int slot, ItemStack stack, ClickAction action);

    public void setClickable(int slot, Clickable clickable) {
        this.setClickable(slot, clickable.itemStack(), clickable.action());
    }

    public void addClickable(Clickable clickable) {
        addClickable(clickable.itemStack(), clickable.action());
    }

    public void addClickable(ItemStack stack, ClickAction action) {
        setClickable(firstEmpty(), stack, action);
    }

    public void addClickables(Clickable... clickables) {
        for (Clickable current : clickables) {
            setClickable(firstEmpty(), current.itemStack(), current.action());
        }
    }

    public void addClickables(List<Clickable> clickables) {
        for (Clickable current : clickables) {
            setClickable(firstEmpty(), current.itemStack(), current.action());
        }
    }

    public abstract int firstEmpty();

    public abstract void setItem(int slot, ItemStack stack);

    public abstract void fill(ItemStack itemStack);

    public abstract void fill(ItemStack itemStack, ClickAction action);

    public abstract void addItems(ItemStack... stacks);

    public void addItems(List<ItemStack> stacks) {
        addItems(stacks.toArray(new ItemStack[0]));
    }

    public void setCloseAction(Consumer<Player> closeAction) {
        this.onCloseAction = closeAction;
    }

    public void setOpenAction(Consumer<Player> openAction) {
        this.onOpenAction = openAction;
    }

    protected static void validateSlotBoundaries(int slotId, int size) {
        if (slotId < 0 || slotId >= size)
            throw new IllegalArgumentException("Slot " + slotId + " is out of slot boundaries!");
    }

    public boolean isInSlotBoundaries(int slotId) {
        return !(slotId < 0) && slotId < getSize();
    }

    protected final void validateSlotBoundaries(int slotId) {
        if (!isInSlotBoundaries(slotId)) {
            throw new IllegalArgumentException("Slot " + slotId + " is out of slot boundaries for " + this.getClass().getName() + "!");
        }
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    protected void setTitle(@Nullable String title) {
        this.title = title != null ? title : "";
    }

    public abstract int getSize();

    @Override
    public String toString() {
        return "InventoryGui{" + "title=" + getTitle() +
                ", onCloseAction=" + onCloseAction +
                ", onOpenAction=" + onOpenAction +
                '}';
    }
}
