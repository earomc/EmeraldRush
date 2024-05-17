package net.earomc.emeraldrush.shop;

import net.earomc.emeraldrush.GameInstance;
import net.earomc.emeraldrush.items.impl.DefaultSpecialItems;
import net.earomc.emeraldrush.team.Team;
import net.earomc.emeraldrush.util.InventoryUtil;
import net.earomc.emeraldrush.util.ItemBuilder;
import net.earomc.emeraldrush.util.PlayerItemOverflowQueueManager;
import net.earomc.emeraldrush.util.gui.inventory.ChestGui;
import net.earomc.emeraldrush.util.gui.inventory.InventoryGui;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ShopVillagerHandler implements Listener {

    private final GameInstance gameInstance;
    private final PlayerItemOverflowQueueManager itemOverflowQueueManager;

    public ShopVillagerHandler(GameInstance gameInstance, PlayerItemOverflowQueueManager itemOverflowQueueManager) {
        this.gameInstance = gameInstance;
        this.itemOverflowQueueManager = itemOverflowQueueManager;
    }

    @EventHandler
    public void onVillagerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity == null) return;
        String customName = entity.getCustomName();
        if (customName == null) return;
        if (customName.equals(ShopVillager.CUSTOM_NAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity rightClicked = event.getRightClicked();
        String rcCustomName = rightClicked.getCustomName();
        if (!gameInstance.getPlayers().contains(player)) return;
        if (!(rcCustomName != null && rcCustomName.equals(ShopVillager.CUSTOM_NAME))) return;
        Team team = gameInstance.getTeam(player);
        if (team == null) return;
        event.setCancelled(true);
        InventoryGui gui = ChestGui.fromRows("§7Shop", 6);
        gui.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).build());
        int emeralds = InventoryUtil.countEmeralds(player.getInventory());
        int lives = team.getLives();
        gui.setItem(3, new ItemBuilder(Material.SKULL_ITEM)
                .name("§e§lLives: §r§d§l" + team.getLives())
                .appendLore(
                        "§r§7Purchasing items can",
                        "take your respawns away.",
                        "",
                        "§7>§2 " + emeralds + " Emeralds",
                        "§7>§d " + lives + " Respawns"
                )
                .build());

        ShopItemClickableFactory f = new ShopItemClickableFactory(itemOverflowQueueManager, gameInstance);
        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE);
        gui.setClickable(10, f.createShopItem("§e§lGolden Apple", goldenApple, 1, goldenApple.clone()));
        ItemStack fireball = new ItemStack(Material.FIREBALL);
        gui.setClickable(11, f.createShopItem("§e§lFireball", fireball, 2, DefaultSpecialItems.FIREBALL));

        gui.setClickable(12, f.createShopItem("§e§lTNT", new ItemStack(Material.TNT), 1, DefaultSpecialItems.TNT));
        ItemStack snowball = new ItemStack(Material.SNOW_BALL);
        gui.setClickable(13, f.createShopItem("§e§lSnowball", snowball, 2, snowball.clone()));
        ItemStack enderPearl = new ItemStack(Material.ENDER_PEARL);
        gui.setClickable(14, f.createShopItem("§e§lEnderpearl", enderPearl, 999, enderPearl.clone()));
        // TODO: Check enderpearl cost, add "Available in"
        gui.setClickable(19, f.createShopItem("§e§lDiamond Pickaxe", new ItemStack(Material.DIAMOND_PICKAXE), 4, player1 -> {
            PlayerInventory inventory = player1.getInventory();
            ItemStack diamondPickaxe = new ItemBuilder(Material.DIAMOND_PICKAXE).unbreakable().enchant(Enchantment.DIG_SPEED, 10).build();
            inventory.setItem(inventory.first(Material.IRON_PICKAXE), diamondPickaxe);
        }));

        gui.setClickable(20, f.createShopItem("§e§lDiamond Sword", new ItemStack(Material.DIAMOND_SWORD), 2, player1 -> {
            ItemStack diamondSword = new ItemBuilder(Material.DIAMOND_SWORD).unbreakable().build();
            PlayerInventory inventory = player1.getInventory();
            inventory.setItem(inventory.first(Material.IRON_SWORD), diamondSword);
        }));
        gui.setClickable(21, f.createShopItem("§e§lDiamond Armor", new ItemStack(Material.DIAMOND_CHESTPLATE), 6, player1 -> {
            PlayerInventory inventory = player1.getInventory();
            inventory.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).unbreakable().build());
            inventory.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).unbreakable().build());
        }));

        ItemStack kbStick = new ItemBuilder(Material.STICK).enchant(Enchantment.KNOCKBACK, 2).build();
        gui.setClickable(22, f.createShopItem("§e§lKnockback Stick", new ItemStack(Material.STICK), 2, kbStick));
        gui.setClickable(23, f.createShopItem("§e§lInsta-Boom TNT", new ItemStack(Material.TNT), 1, DefaultSpecialItems.INSTA_BOOM_TNT));

        ItemStack bowIcon = new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 1).hideEnchants().build();
        ItemStack bow = new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 1).unbreakable().build();

        gui.setClickable(28, f.createShopItem("§e§lBow §r§7(Power I)", bowIcon, 4, bow));
        ItemStack arrow = new ItemBuilder(Material.ARROW).amount(8).build();
        gui.setClickable(29, f.createShopItem("§e§lArrow", arrow, 1, arrow.clone()));

        ItemStack speedPot = new ItemBuilder.PotionBuilder(PotionType.SPEED)
                .addEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 45, 2, false, true))
                .hideAttributes()
                .build();
        gui.setClickable(37, f.createShopItem("§e§lSpeed II Potion §r§7(45s)", speedPot, 2, speedPot.clone()));

        ItemStack jumpPot = new ItemBuilder.PotionBuilder(PotionType.JUMP)
                .addEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 45, 5, false, true))
                .hideAttributes()
                .build();
        gui.setClickable(38, f.createShopItem("§e§lJump V Potion §r§7(45s)", jumpPot, 2, jumpPot.clone()));

        ItemStack invisPot = new ItemBuilder.PotionBuilder(PotionType.INVISIBILITY)
                .addEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 30, 1, false, true))
                .hideAttributes()
                .build();
        gui.setClickable(39, f.createShopItem("§e§lInvisibility Potion §r§7(30s)", invisPot, 2, invisPot.clone()));

        ItemStack damagePot = new ItemBuilder.PotionBuilder(PotionType.INSTANT_DAMAGE)
                .splash()
                .hideAttributes()
                .build();
        gui.setClickable(40, f.createShopItem("§e§lDamage Potion", damagePot, 2, damagePot.clone()));


        ItemStack wool;
        if (gameInstance.inTeam1(player)) {
            wool = new ItemBuilder.WoolBuilder(DyeColor.BLUE).amount(64).build();
        } else {
            wool = new ItemBuilder.WoolBuilder(DyeColor.RED).amount(64).build();
        }
        gui.setClickable(46, f.createShopItem("§e§lWool", wool, 1, wool.clone()));

        ItemStack oakWood = new ItemStack(Material.WOOD, 16);
        gui.setClickable(47, f.createShopItem("§e§lOak Wood", oakWood, 2, oakWood.clone()));

        ItemStack obsidian = new ItemStack(Material.OBSIDIAN);
        gui.setClickable(48, f.createShopItem("§e§lObsidian", obsidian, 3, obsidian.clone()));

        gui.open(player);
    }
}
