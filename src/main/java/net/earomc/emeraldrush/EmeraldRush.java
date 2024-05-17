package net.earomc.emeraldrush;

import net.earomc.emeraldrush.commands.debug.SpawnShopVillagerCommand;
import net.earomc.emeraldrush.commands.debug.TeamCommand;
import net.earomc.emeraldrush.items.SpecialItemHandler;
import net.earomc.emeraldrush.items.SpecialItemRegistry;
import net.earomc.emeraldrush.items.impl.DefaultSpecialItems;
import net.earomc.emeraldrush.map.InGameMap;
import net.earomc.emeraldrush.map.MapManager;
import net.earomc.emeraldrush.map.handlers.BlockBreakPlaceHandler;
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
    private GameInstance gameInstance;

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
        this.gameInstance = new GameInstance(mapManager, emeraldSpawnerHandler, inGameScoreboardManager);
        PlayerItemOverflowQueueManager itemOverflowQueueManager = new PlayerItemOverflowQueueManager();
        PlayerFlagManager playerFlagManager = new PlayerFlagManager();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerListener(gameInstance, mapManager), this);
        pluginManager.registerEvents(new LifeBlockHandler(inGameMap.getLifeBlock1(), inGameMap.getLifeBlock2(), gameInstance), this);
        pluginManager.registerEvents(new InventoryGuiListener(), this);
        pluginManager.registerEvents(itemOverflowQueueManager, this);
        pluginManager.registerEvents(new ShopVillagerHandler(gameInstance, itemOverflowQueueManager), this);
        pluginManager.registerEvents(new EmeraldDepositHandler(inGameMap, gameInstance), this);
        pluginManager.registerEvents(new WorldEventsHandler(), this);
        pluginManager.registerEvents(new BlockBreakPlaceHandler(gameInstance), this);

        //TODO: remove dbg commands
        getCommand("sps").setExecutor(new SpawnShopVillagerCommand());
        getCommand("tm").setExecutor(new TeamCommand(gameInstance));

        SpecialItemRegistry specialItemRegistry = new SpecialItemRegistry();
        SpecialItemHandler specialItemHandler = new SpecialItemHandler(specialItemRegistry);
        pluginManager.registerEvents(specialItemHandler, this);

        new DefaultSpecialItems(specialItemRegistry).register();

    }

    public static EmeraldRush instance() {
        return instance;
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }
}
