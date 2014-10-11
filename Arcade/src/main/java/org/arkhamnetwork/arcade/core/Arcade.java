/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.FileConfiguration;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.YamlConfiguration;
import org.arkhamnetwork.arcade.commons.hooks.BukkitHook;
import org.arkhamnetwork.arcade.commons.hooks.MinecraftServerHook;
import org.arkhamnetwork.arcade.commons.hooks.SpigotHook;
import org.arkhamnetwork.arcade.commons.patches.TileEntityMemoryLeakFixTask;
import org.arkhamnetwork.arcade.commons.plugin.ArcadeMiniPlugin;
import org.arkhamnetwork.arcade.commons.plugin.ArcadePlugin;
import org.arkhamnetwork.arcade.commons.server.ServerType;
import org.arkhamnetwork.arcade.commons.userstorage.UserManager;
import org.arkhamnetwork.arcade.commons.utils.BarUtils;
import org.arkhamnetwork.arcade.commons.utils.ConfigUtils;
import org.arkhamnetwork.arcade.commons.utils.URLUtils;
import org.arkhamnetwork.arcade.core.configuration.ArcadeConfiguration;
import org.arkhamnetwork.arcade.core.configuration.ArcadeWebConfiguration;
import org.arkhamnetwork.arcade.core.configuration.MySQLCredentials;
import org.arkhamnetwork.arcade.core.listener.LobbySignsListener;
import org.arkhamnetwork.arcade.minigame.MinigameType;
import org.arkhamnetwork.arcade.miniplugin.mysqlconnector.mySQLConnector;
import org.arkhamnetwork.arcade.miniplugin.namedatabase.NameDatabase;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author devan_000
 */
public class Arcade extends ArcadePlugin {

    /**
     * Craft the ArcadePlugin instance
     */
    public Arcade() {
        super("Arcade", "0.1-SNAPSHOT");
    }

    @Getter
    private static Arcade instance;

    @Getter
    @Setter
    private static boolean serverOnline = false;

    @Getter
    private List<ArcadeMiniPlugin> loadedMiniPlugins = new ArrayList<>();

    @Getter
    private final String WEB_CONFIG_LOCATION = "Arcade.yml";

    @Override
    public void onEnable() {
        try {
            // Needed - ArcadePlugin.
            super.onEnable();

            instance = this;

            SpigotHook.injectAsyncCatcher(false);
            BukkitHook.disableChunkGC(this);
            MinecraftServerHook.disableAutoSave();

            // Create the data folder and the default configs.
            ConfigUtils.createFolderIfNotExists(getDataFolder());
            ConfigUtils.createConfigWithDefaultsIfNotExists(new File(
                    getDataFolder(), "config.yml"), getResource("config.yml"));
            ConfigUtils.createConfigWithDefaultsIfNotExists(new File(
                    getDataFolder(), "internalData.yml"),
                    getResource("internalData.yml"));
            ConfigUtils.createFolderIfNotExists(new File(getDataFolder(),
                    "cache"));

            // Load the default config.
            YamlConfiguration localConfig = YamlConfiguration
                    .loadConfiguration(new File(getDataFolder(), "config.yml"));
            ArcadeConfiguration.setServerType(ServerType.valueOf(localConfig
                    .getString("SERVER_TYPE").toUpperCase()));
            ArcadeConfiguration.setWebServerURL(URLUtils
                    .toWorkingURL(localConfig.getString("WEBSERVER")));
            ArcadeConfiguration.setMysqlCredentials(new MySQLCredentials(
                    localConfig.getString("MYSQL.HOSTNAME"), localConfig
                    .getInt("MYSQL.PORT"), localConfig
                    .getString("MYSQL.USER"), localConfig
                    .getString("MYSQL.PASSWORD"), localConfig
                    .getString("MYSQL.DATABASENAME")));
            ArcadeConfiguration.setServerName(localConfig
                    .getString("SERVERNAME"));
            ArcadeConfiguration.setGamemodes(localConfig
                    .getStringList("GAMEMODES"));

            // Download the Arcade.yml configuration from the web server.
            log("Attempting to download the web Arcade.yml.");
            File cacheConfigLocation = new File(Arcade.getInstance()
                    .getDataFolder() + File.separator + "cache", "Arcade.yml");
            if (cacheConfigLocation.exists()) {
                cacheConfigLocation.delete();
            }
            try {
                FileUtils.copyURLToFile(
                        new URL(ArcadeConfiguration.getWebServerURL()
                                + WEB_CONFIG_LOCATION), cacheConfigLocation);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            log("The web Arcade.yml downloaded successfuly!");

            // Load the webConfiguration.
            FileConfiguration webConfig = YamlConfiguration
                    .loadConfiguration(cacheConfigLocation);
            ArcadeWebConfiguration.setBungeeLobbyServerName(webConfig
                    .getString("bungeeLobbyServer"));
            ArcadeWebConfiguration.setLobbySignLines(webConfig
                    .getStringList("lobbysignligns"));

            // Load all of the mini-plugins.
            log("Attempting to load mini-plugins");

            loadedMiniPlugins.add(new mySQLConnector("mySQL-Connector", "0.1-SNAPSHOT", getServer()));
            loadedMiniPlugins.add(new NameDatabase("NameDatabase", "0.1-SNAPSHOT", getServer()));

            for (ArcadeMiniPlugin miniPlugin : this.loadedMiniPlugins) {
                miniPlugin.onEnable();
                miniPlugin.postEnable();
            }
            log("Finished loading mini-plugins");

            // Register events.
            PluginManager pluginManager = getServer().getPluginManager();
            pluginManager.registerEvents(new LobbySignsListener(), this);
            pluginManager.registerEvents(new UserManager(), this);
            pluginManager.registerEvents(new BarUtils(), this);

            // Hook any messaging channels.
            getServer().getMessenger().registerOutgoingPluginChannel(this,
                    "BungeeCord");

            // Register needed tasks.
            getServer().getScheduler().runTaskTimer(this,
                    new TileEntityMemoryLeakFixTask(), 100L, 100L);

            // We have to call this manually.
            postEnable();

            // Needed - ArcadePlugin
            super.postEnable();
        } catch (Exception ex) {
            ex.printStackTrace();
            shutdown("Error caught while starting up.");
            return;
        }
    }

    @Override
    public void onDisable() {
        // Needed - ArcadePlugin
        super.onDisable();

        for (ArcadeMiniPlugin miniPlugin : loadedMiniPlugins) {
            miniPlugin.onDisable();
            miniPlugin.postDisable();
        }

        //Task cleanup
        getServer().getScheduler().cancelTasks(this);
        
        // We have to call this manually.
        postDisable();

        // Needed - ArcadePlugin
        super.postDisable();
    }

    @Override
    public void postEnable() {
        log("Chosen server type: " + ArcadeConfiguration.getServerType().name());
        switch (ArcadeConfiguration.getServerType()) {
            case SINGLE_GAMEMODE:
                log("Finding the first gamemode from your configuration.");

                if (ArcadeConfiguration.getGamemodes().isEmpty()) {
                    shutdown("There are no gamemodes configured.");
                    return;
                }

                MinigameType minigameType = MinigameType
                        .valueOf(ArcadeConfiguration.getGamemodes().get(0)
                                .toUpperCase());

                if (minigameType == null) {
                    shutdown("We could not resolve the minigame from name: "
                            + ArcadeConfiguration.getGamemodes().get(0)
                            .toUpperCase());
                    return;
                }

                log("Starting the found minigame " + minigameType.name() + " ...");

                loadedMiniPlugins.add(minigameType.start(getServer()));
                break;
            default:
                break;
        }

        setServerOnline(true);
    }

    @Override
    public void postDisable() {
    }
}
