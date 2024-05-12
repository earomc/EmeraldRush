package net.earomc.emeraldrush;

import net.earomc.emeraldrush.config.EmeraldRushConfig;
import net.earomc.emeraldrush.loadouts.LoadOutLobby;
import net.earomc.emeraldrush.loadouts.LoadOutTeam;
import net.earomc.emeraldrush.map.MapManager;
import net.earomc.emeraldrush.util.LoadOut;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class PlayerListener implements Listener {

    private final GameInstance gameInstance;
    private final MapManager mapManager;

    public PlayerListener(GameInstance gameInstance, MapManager mapManager) {
        this.gameInstance = gameInstance;
        this.mapManager = mapManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        switch (gameInstance.getCurrentPhase()) {
            case LOBBY:
                gameInstance.registerPlayer(player);
                player.teleport(mapManager.getLobbyMap().getSpawnLocation());
                new LoadOutLobby().load(player, true);
                if (gameInstance.getPlayers().size() >= EmeraldRushConfig.MIN_PLAYERS) {
                    gameInstance.getLobbyCountdown().startFinalCountdown();
                }
                break;
            case IN_GAME:
                if (!player.hasPermission(Permissions.JOIN_DURING_GAME)) {
                    player.kickPlayer("§cTrying to join while in game");
                } else {
                    // TODO: possibly handle spectator shit.
                }
                break;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        switch (gameInstance.getCurrentPhase()) {
            case LOBBY:
                event.setCancelled(true);
                break;
            case IN_GAME:
                break;
        }
    }

    @EventHandler
    public void onPlayerMoveOutOfBound(PlayerMoveEvent event) {
        if (mapManager.getInGameMap().getBound().isOutside(event.getTo())) {
            //event.getPlayer().setHealth(0); // kill player
            handleInGameDeath(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!gameInstance.getPlayers().contains(player)) return;
        switch (gameInstance.getCurrentPhase()) {
            case LOBBY:
                break;
            case IN_GAME:
                handleInGameDeath(player);
                break;
        }
        //TODO: Properly handle death
    }

    private void handleInGameDeath(Player player) {
        Location location = player.getLocation().clone();
        Bukkit.getScheduler().runTaskLater(EmeraldRush.instance(), () -> player.spigot().respawn(), 1L);
        player.teleport(location);
        int respawnDelaySeconds = 3; //TODO add to config
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * respawnDelaySeconds, 1, false, false));
        player.setAllowFlight(true);
        player.setFlying(true);

        PlayerInventory inventory = player.getInventory();
        inventory.setArmorContents(null);
        inventory.setContents(new ItemStack[]{});
        player.sendMessage("§aRespawning in "+ respawnDelaySeconds + " seconds");

        Bukkit.getScheduler().runTaskLater(EmeraldRush.instance(), () -> {
            boolean inTeam1 = gameInstance.inTeam1(player);
            new LoadOutTeam(inTeam1).load(player, true);
            teleportToTeamSpawn(player, inTeam1);
        }, respawnDelaySeconds * 20L);

    }

    private void teleportToTeamSpawn(Player player, boolean inTeam1) {
        Location location;
        if (inTeam1) {
            location = mapManager.getInGameMap().getSpawnLocationTeam1();
        } else {
            location = mapManager.getInGameMap().getSpawnLocationTeam2();
        }
        player.teleport(location);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player entity = event.getPlayer();
    }

}
