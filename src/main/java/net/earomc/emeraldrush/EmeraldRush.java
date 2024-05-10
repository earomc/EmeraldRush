package net.earomc.emeraldrush;

import net.earomc.emeraldrush.map.InGameMap;
import net.earomc.emeraldrush.map.MapManager;
import net.earomc.emeraldrush.map.handlers.LifeBlockHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EmeraldRush extends JavaPlugin {

    @Override
    public void onEnable() {
        MapManager mapManager = new MapManager();
        GameInstance gameInstance = new GameInstance(mapManager);
        LobbyCountdown lobbyCountdown = new LobbyCountdown(this, gameInstance);
        lobbyCountdown.startIdleLoop();

        PlayerFlagManager playerFlagManager = new PlayerFlagManager();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerListener(gameInstance, lobbyCountdown, mapManager), this);
        InGameMap inGameMap = mapManager.getInGameMap();
        pluginManager.registerEvents(new LifeBlockHandler(inGameMap.getLifeBlock1(), inGameMap.getLifeBlock2()), this);
    }
}
