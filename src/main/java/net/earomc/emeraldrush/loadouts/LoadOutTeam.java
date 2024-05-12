package net.earomc.emeraldrush.loadouts;

import net.earomc.emeraldrush.util.ItemBuilder;
import net.earomc.emeraldrush.util.LoadOut;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class LoadOutTeam implements LoadOut {

    // if true, is team 1, if false is team2
    private final boolean isTeam1;

    public LoadOutTeam(boolean isTeam1) {
        this.isTeam1 = isTeam1;
    }

    @Override
    public void load(Player player) {
        LoadOut.reset(player);

        ItemStack chestPlate;
        ItemStack helmet;
        ItemStack wool;
        if (isTeam1) {
            chestPlate = new ItemBuilder.LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, Color.BLUE).unbreakable().build();
            helmet = new ItemBuilder.LeatherArmorBuilder(Material.LEATHER_HELMET, Color.BLUE).unbreakable().build();
            wool = new ItemBuilder.WoolBuilder(DyeColor.BLUE).amount(99).build();
        } else {
            chestPlate = new ItemBuilder.LeatherArmorBuilder(Material.LEATHER_CHESTPLATE, Color.RED).unbreakable().build();
            helmet = new ItemBuilder.LeatherArmorBuilder(Material.LEATHER_HELMET, Color.RED).unbreakable().build();
            wool = new ItemBuilder.WoolBuilder(DyeColor.RED).amount(99).build();
        }

        ItemStack pickaxe = new ItemBuilder(Material.IRON_PICKAXE).unbreakable().enchant(Enchantment.DIG_SPEED, 5).build();
        ItemStack axe = new ItemBuilder(Material.WOOD_AXE).unbreakable().enchant(Enchantment.DIG_SPEED, 1).build();
        ItemStack shears = new ItemBuilder(Material.SHEARS).unbreakable().build();
        ItemStack sword = new ItemBuilder(Material.IRON_SWORD).unbreakable().build();

        PlayerInventory inv = player.getInventory();
        inv.setItem(0, sword);
        inv.setItem(1, wool);
        inv.setItem(2, shears);
        inv.setItem(3, pickaxe);
        inv.setItem(4, axe);

        inv.setHelmet(helmet);
        inv.setChestplate(chestPlate);
        inv.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).unbreakable().build());
        inv.setBoots(new ItemBuilder(Material.IRON_BOOTS).unbreakable().build());
    }
}
