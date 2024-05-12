package net.earomc.emeraldrush.util.gui.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class SingleInventoryGui extends InventoryGui {

    protected final Unsafe unsafe = new Unsafe();
    protected final HashMap<Integer, ClickAction> slotToActionMap;

    public SingleInventoryGui(@Nullable String title) {
        super(title);
        this.slotToActionMap = new HashMap<>();
    }

    void onClick(Player player, ClickContext context) {
        ClickAction action = getAction(context.slotId());
        if (action != null) {
            action.perform(player, context);
            //player.playSound(player.getLocation(), Sound.CLICK, 0.3f, 1);
        }
    }

    private ClickAction getAction(int slot) {
        return slotToActionMap.get(slot);
    }

    @Nullable
    public abstract Inventory getInventory();

    public void open(Player player) {
        super.open(player);
        Inventory inventory = getInventory();
        if (inventory != null) {
            //player.closeInventory();
            player.openInventory(inventory);
        }
        InventoryGuiTracker.instance().onOpenGui(this, player);
    }

    public int firstEmpty() {
        if (getInventory() != null) {
            for (int i = 0; i < getSize(); i++) {
                if (getInventory().getItem(i) == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void setItem(int slot, ItemStack stack) {
        validateSlotBoundaries(slot);
        unsafe.setItem(slot, stack);
    }

    public void setClickable(int slot, ItemStack stack, ClickAction action) {
        validateSlotBoundaries(slot);
        unsafe.setClickable(slot, stack, action);
    }

    public void setClickable(int slot, Clickable clickable) {
        setClickable(slot, clickable.itemStack(), clickable.action());
    }

    public void fill(ItemStack itemStack) {
        if (getInventory() != null) {
            for (int i = 0; i < this.getSize(); i++) {
                setItem(i, itemStack);
            }
        }
    }

    public void fill(ItemStack itemStack, ClickAction action) {
        if (getInventory() != null) {
            for (int i = 0; i < this.getSize(); i++) {
                setClickable(i, itemStack, action);
            }
        }
    }

    public void addItems(ItemStack... stacks) {
        Inventory inventory = getInventory();
        if (inventory != null) {
            for (ItemStack stack : stacks) {
                inventory.setItem(firstEmpty(), stack);
            }
        } else throw new UnsupportedOperationException("Override this method or getInventory() and make it return not null!", new NullPointerException("Inventory is null"));
    }

    public Unsafe getUnsafe() {
        return unsafe;
    }

    @SuppressWarnings("ConstantConditions")
    protected class Unsafe {
        public void setClickable(int slot, ItemStack stack, ClickAction action) {
            slotToActionMap.put(slot, action);
            getInventory().setItem(slot, stack);
        }

        public void setItem(int slot, ItemStack stack) {
            getInventory().setItem(slot, stack);
        }
    }
}
