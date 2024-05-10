package net.earomc.emeraldrush;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerFlagManager {
    private final HashMap<Player, FlagSet<PlayerFlag>> flagMap = new HashMap<>();

    public boolean hasFlag(Player player, PlayerFlag flag) {
        return getFlags(player).hasFlag(flag);
    }

    private FlagSet<PlayerFlag> getFlags(Player player) {
        return flagMap.computeIfAbsent(player, ignore -> new FlagSet<>());
    }

    public void unregisterPlayer(Player player) {
        flagMap.remove(player);
    }
}
