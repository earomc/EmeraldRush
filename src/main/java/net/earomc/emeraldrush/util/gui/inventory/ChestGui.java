package net.earomc.emeraldrush.util.gui.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

/**
 * A simple Minecraft chest-like Inventory with clickable items.
 */

public class ChestGui extends SingleInventoryGui implements CloneableGui<ChestGui> {

    protected int rows;
    private final int size;
    private Inventory inventory;

    protected ChestGui(@Nullable String title, int rows) {
        super(title);
        this.size = rows * 9;
        this.rows = rows;
    }

    public static ChestGui fromRows(String title, int rows) {
        return new ChestGui(title, rows);
    }

    public static ChestGui fromSlots(String title, int slots) {
        return new ChestGui(title, (int) Math.ceil(slots / 9d));
    }

    @Override
    public ChestGui clone() throws CloneNotSupportedException {
        return (ChestGui) super.clone();
    }

    @Nullable
    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            initInventory();
        }
        return inventory;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        initInventory();
    }

    /**
     * Initialized the internal inventory field with an empty bukkit inventory.
     */
    private void initInventory() {
        this.inventory = Bukkit.createInventory(null, rows * 9, this.title);
    }
}
