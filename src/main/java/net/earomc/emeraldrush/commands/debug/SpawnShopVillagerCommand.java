package net.earomc.emeraldrush.commands.debug;

import net.earomc.emeraldrush.shop.ShopVillager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnShopVillagerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        new ShopVillager(((Player)sender).getLocation()).spawn();
        return true;
    }
}
