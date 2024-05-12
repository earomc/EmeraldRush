package net.earomc.emeraldrush.shop;

import com.comphenix.protocol.PacketType;
import net.earomc.emeraldrush.GameInstance;
import net.earomc.emeraldrush.team.Team;
import net.earomc.emeraldrush.util.PlayerItemOverflowQueueManager;
import net.earomc.emeraldrush.util.gui.inventory.ClickAction;
import net.earomc.emeraldrush.util.gui.inventory.Clickable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ShopItemClickable implements Clickable {
    private final GameInstance gameInstance;
    private final String title;
    private final ItemStack icon;
    private final int livesCost;
    private final Consumer<Player> onPurchase;


    public ShopItemClickable(GameInstance gameInstance, String title, ItemStack icon, int livesCost, Consumer<Player> onPurchase) {
        this.gameInstance = gameInstance;
        this.title = title;
        this.icon = icon;
        this.livesCost = livesCost;
        this.onPurchase = onPurchase;
    }

    @Override
    public ItemStack itemStack() {
        return icon;
    }

    @Override
    public ClickAction action() {
        return (player, context) -> {
            Team team = gameInstance.getTeam(player);
            if (team == null) return;
            boolean couldBuy = team.purchaseItem(livesCost, player);
            if (couldBuy) {
                onPurchase.accept(player);
            } else {
                player.sendMessage("§cNot enough lives or emeralds");
            }
        };
    }
}
