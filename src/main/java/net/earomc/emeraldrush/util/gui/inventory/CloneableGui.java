package net.earomc.emeraldrush.util.gui.inventory;

public interface CloneableGui<T extends InventoryGui> extends Cloneable {
    T clone() throws CloneNotSupportedException;
}
