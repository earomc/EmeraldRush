package net.earomc.emeraldrush.commands.debug;

import com.comphenix.protocol.PacketType;
import net.earomc.emeraldrush.GameInstance;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamCommand implements CommandExecutor {

    private final GameInstance gameInstance;

    public TeamCommand(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        List<Player> players1 = gameInstance.getTeam1().getPlayers();
        List<Player> players2 = gameInstance.getTeam2().getPlayers();

        sender.sendMessage("Team 1:");
        if (players1.isEmpty()) {
            sender.sendMessage("empty");
        } else {
            players1.forEach(player1 -> sender.sendMessage(player1.getName()));
        }
        sender.sendMessage("Team 2:");
        if (players2.isEmpty()) {
            sender.sendMessage("empty");
        } else {
            players2.forEach(player1 -> sender.sendMessage(player1.getName()));
        }
        return false;
    }
}
