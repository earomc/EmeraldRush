package net.earomc.emeraldrush.loadouts;

import net.earomc.emeraldrush.util.ItemBuilder;
import net.earomc.emeraldrush.util.LoadOut;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import static net.earomc.emeraldrush.util.LoadOut.*;

public class LoadOutLobby implements LoadOut {
    @Override
    public void load(Player player) {
        reset(player);
        player.getInventory().setItem(8, new ItemBuilder.DyeBuilder(DyeColor.RED).name("§cLeave").build());
    }
}
