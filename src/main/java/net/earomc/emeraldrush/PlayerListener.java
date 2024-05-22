package net.earomc.emeraldrush;

import net.earomc.emeraldrush.config.EmeraldRushConfig;
import net.earomc.emeraldrush.events.LivesUpdateEvent;
import net.earomc.emeraldrush.loadouts.LoadOutLobby;
import net.earomc.emeraldrush.loadouts.LoadOutTeam;
import net.earomc.emeraldrush.map.InGameMap;
import net.earomc.emeraldrush.map.MapManager;
import net.earomc.emeraldrush.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class PlayerListener implements Listener {

    private final GameInstance gameInstance;
    private final MapManager mapManager;
    private Set<Player> spectating = new HashSet<>();

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
        if (mapManager.getInGameMap().getBound().isOutside(event.getTo().toVector())) {
            Player player = event.getPlayer();
            Phase currentPhase = gameInstance.getCurrentPhase();
            switch (currentPhase) {
                case IN_GAME:
                    handleInGameRespawn(player);
                    break;
                case WINNING:
                    spectate(player);
                    break;
            }
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
                player.spigot().respawn();
                handleInGameRespawn(player);
                break;
        }
    }

    //@EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!gameInstance.getPlayers().contains(player)) return;
        switch (gameInstance.getCurrentPhase()) {
            case LOBBY:
                break;
            case IN_GAME:
                handleInGameRespawn(player);
                break;
        }
    }

    private void handleInGameDeath(Player player) {
    }

    // Minecraft respawn is different from "in-game respawn" Minecraft respawn is initiated right after death,
    // ingame respawn will take place only "RESPAWN_DELAY_SECONDS" of time after the Minecraft respawn.
    private void handleInGameRespawn(Player player) {
        Team team = gameInstance.getTeam(player);
        team.addLives(-1);
        if (team.getLives() <= 0) {
            team.setDead(player);
            if (team.isAllDead()) {
                gameInstance.win(gameInstance.getOpponentTeam(team));
            }
        } else {
            spectate(player);
            player.sendMessage("§aRespawning in " + EmeraldRushConfig.RESPAWN_DELAY_SECONDS + " seconds");

            Bukkit.getScheduler().runTaskLater(EmeraldRush.instance(), () -> {
                boolean inTeam1 = gameInstance.inTeam1(player);
                new LoadOutTeam(inTeam1).load(player, true);
                teleportToTeamSpawn(player, inTeam1);
                spectating.remove(player);
            }, EmeraldRushConfig.RESPAWN_DELAY_SECONDS * 20L);
        }
    }

    private void spectate(Player player) {
        InGameMap inGameMap = gameInstance.getMapManager().getInGameMap();
        Location mapCenter = inGameMap.getBound().getCenter().toLocation(inGameMap.getWorld());
        // teleport to 5 blocks above map center
        player.teleport(mapCenter);
        spectating.add(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * EmeraldRushConfig.RESPAWN_DELAY_SECONDS, 1, false, false));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setVelocity(new Vector());
        PlayerInventory inventory = player.getInventory();
        inventory.setArmorContents(null);
        inventory.setContents(new ItemStack[]{});
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
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

}
