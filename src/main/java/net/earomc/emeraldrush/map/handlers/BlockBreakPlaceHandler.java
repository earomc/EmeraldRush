package net.earomc.emeraldrush.map.handlers;

import net.earomc.emeraldrush.GameInstance;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockBreakPlaceHandler implements Listener {

    private final Set<Location> playerPlacedBlocks = new HashSet<>();
    private final GameInstance gameInstance;

    public BlockBreakPlaceHandler(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!playerPlacedBlocks.contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (gameInstance.getPlayers().contains(event.getPlayer())) {
            playerPlacedBlocks.add(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onExplosionDamage(EntityExplodeEvent event) {
        List<Block> blocksToRemove = new ArrayList<>();
        List<Block> explodedBlocks = event.blockList();
        for (Block block : explodedBlocks) {
            if (!playerPlacedBlocks.contains(block.getLocation())) {
                blocksToRemove.add(block);
            }
        }
        explodedBlocks.removeAll(blocksToRemove);
    }
}
