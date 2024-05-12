package net.earomc.emeraldrush;

import net.earomc.emeraldrush.commands.debug.SpawnShopVillagerCommand;
import net.earomc.emeraldrush.commands.debug.TeamCommand;
import net.earomc.emeraldrush.map.InGameMap;
import net.earomc.emeraldrush.map.MapManager;
import net.earomc.emeraldrush.map.handlers.EmeraldSpawnerHandler;
import net.earomc.emeraldrush.map.handlers.LifeBlockHandler;
import net.earomc.emeraldrush.scoreboard.impl.InGameScoreboardManager;
import net.earomc.emeraldrush.shop.ShopVillagerHandler;
import net.earomc.emeraldrush.util.PlayerItemOverflowQueueManager;
import net.earomc.emeraldrush.util.gui.inventory.InventoryGuiListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EmeraldRush extends JavaPlugin {

    private static EmeraldRush instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        MapManager mapManager = new MapManager();
        InGameMap inGameMap = mapManager.getInGameMap();
        EmeraldSpawnerHandler emeraldSpawnerHandler = new EmeraldSpawnerHandler(this, inGameMap.getEmeraldSpawners());

        InGameScoreboardManager inGameScoreboardManager = new InGameScoreboardManager();
        GameInstance gameInstance = new GameInstance(mapManager, emeraldSpawnerHandler, inGameScoreboardManager);
        PlayerItemOverflowQueueManager itemOverflowQueueManager = new PlayerItemOverflowQueueManager();
        PlayerFlagManager playerFlagManager = new PlayerFlagManager();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerListener(gameInstance, mapManager), this);
        pluginManager.registerEvents(new LifeBlockHandler(inGameMap.getLifeBlock1(), inGameMap.getLifeBlock2()), this);
        pluginManager.registerEvents(new InventoryGuiListener(), this);
        pluginManager.registerEvents(itemOverflowQueueManager, this);
        pluginManager.registerEvents(new ShopVillagerHandler(gameInstance, itemOverflowQueueManager), this);
        pluginManager.registerEvents(new EmeraldDepositHandler(inGameMap, gameInstance), this);

        getCommand("sps").setExecutor(new SpawnShopVillagerCommand());
        getCommand("tm").setExecutor(new TeamCommand(gameInstance));

    }

    public static EmeraldRush instance() {
        return instance;
    }
}
