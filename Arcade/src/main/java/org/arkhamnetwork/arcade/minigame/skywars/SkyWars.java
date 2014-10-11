/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.FileConfiguration;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.YamlConfiguration;
import org.arkhamnetwork.arcade.commons.kit.Kit;
import org.arkhamnetwork.arcade.commons.kit.KitShop;
import org.arkhamnetwork.arcade.commons.plugin.ArcadeMiniPlugin;
import org.arkhamnetwork.arcade.commons.podium.Podium;
import org.arkhamnetwork.arcade.commons.pregame.PreGameManager;
import org.arkhamnetwork.arcade.commons.storage.ArcadeHashMap;
import org.arkhamnetwork.arcade.commons.utils.DescriptionUtils;
import org.arkhamnetwork.arcade.commons.utils.KitUtils;
import org.arkhamnetwork.arcade.commons.utils.MobUtils;
import org.arkhamnetwork.arcade.core.Arcade;
import org.arkhamnetwork.arcade.core.configuration.ArcadeConfiguration;
import org.arkhamnetwork.arcade.minigame.MinigameStage;
import org.arkhamnetwork.arcade.minigame.skywars.listener.SkywarsBlockListener;
import org.arkhamnetwork.arcade.minigame.skywars.listener.SkywarsEntityListener;
import org.arkhamnetwork.arcade.minigame.skywars.listener.SkywarsInventoryListener;
import org.arkhamnetwork.arcade.minigame.skywars.listener.SkywarsMiscListener;
import org.arkhamnetwork.arcade.minigame.skywars.listener.SkywarsPlayerListener;
import org.arkhamnetwork.arcade.minigame.skywars.manager.SkywarsMapManager;
import org.arkhamnetwork.arcade.minigame.skywars.manager.SkywarsRotationManager;
import org.arkhamnetwork.arcade.minigame.skywars.manager.SkywarsScoreboardManager;
import org.arkhamnetwork.arcade.minigame.skywars.manager.SkywarsSignsManager;
import org.arkhamnetwork.arcade.minigame.skywars.struct.SkywarsMapData;
import org.arkhamnetwork.arcade.minigame.skywars.struct.SkywarsPlayer;
import org.arkhamnetwork.arcade.minigame.skywars.tasks.SkywarsGameTask;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author devan_000
 */
public class SkyWars extends ArcadeMiniPlugin {

    @Getter
    private static SkyWars skywars;
    @Getter
    private ArcadeHashMap<Integer, MinigameStage> stages = new ArcadeHashMap<>();
    @Getter
    @Setter
    private MinigameStage currentStage = null;
    @Getter
    @Setter
    private ArcadeHashMap<UUID, SkywarsPlayer> players = new ArcadeHashMap<>();
    @Getter
    @Setter
    private SkywarsMapData currentMapData = null;
    @Getter
    private String WEB_CONFIG_LOCATION = "Skywars/Skywars.yml";
    @Getter
    private String chatFormat = null;
    @Getter
    private ArcadeHashMap<Integer, String> configuredMaps = new ArcadeHashMap<>();
    @Getter
    private SkywarsScoreboardManager scoreboardManager = null;
    @Getter
    private SkywarsSignsManager signsManager = null;
    @Getter
    private SkywarsGameTask gameTask;
    @Getter
    private int lastPlayedMapID = 0;
    @Getter
    private List<String> gameDescriptionMessage;
    @Getter
    private ArcadeHashMap<String, Kit> kits = new ArcadeHashMap<>();
    @Getter
    private ArcadeHashMap<UUID, KitShop> kitShops = new ArcadeHashMap<>();

    public SkyWars(String name, String version, Server server,
            List<String> pluginGameDescription) {
        super(name, version, server, pluginGameDescription);
        skywars = this;

        stages.put(0, new MinigameStage(0, "Pre-Game"));
        stages.put(1, new MinigameStage(1, "Game"));
        stages.put(2, new MinigameStage(2, "End-Game"));
    }

    @Override
    public void onEnable() {
        try {
            // Needed - ArcadeMiniPlugin
            super.onEnable();

            // Generate and load the PreGame lobby's and worlds.
            PreGameManager.setup(this);
            
            // Download the web Skywars.yml
            log("Attempting to download the web Skywars.yml to the cache");
            File cacheConfigLocation = new File(Arcade.getInstance()
                    .getDataFolder() + File.separator + "cache", "Skywars.yml");
            if (cacheConfigLocation.exists()) {
                cacheConfigLocation.delete();
            }
            FileUtils.copyURLToFile(
                    new URL(ArcadeConfiguration.getWebServerURL()
                            + WEB_CONFIG_LOCATION), cacheConfigLocation);
            log("Skywars.yml download completed succuessfuly.");

            // Load the skywars web configuration.
            FileConfiguration skywarsWebConfiguration = YamlConfiguration
                    .loadConfiguration(cacheConfigLocation);
            chatFormat = skywarsWebConfiguration.getString("chatFormat");
            for (String map : skywarsWebConfiguration.getStringList("maps")) {
                String[] split = map.split(":");
                if (split.length == 2) {
                    int mapID = Integer.valueOf(split[0]);
                    String mapName = split[1];
                    log("* Loaded map " + mapName + " with id " + mapID + ".");
                    configuredMaps.put(mapID, mapName);
                }
            }
            for (String kitName : skywarsWebConfiguration.getConfigurationSection("kits").getKeys(false)) {
                kits.put(kitName, KitUtils.getKitFromSection(skywarsWebConfiguration.getConfigurationSection("kits." + kitName)));
                log("Loaded kit " + ChatColor.GREEN + kitName);
            }
            for (Kit kit : kits.values()) {
                for (Podium podium : PreGameManager.getPodiums()) {
                    if (!podium.isOccupied()) {
                        KitShop shop = new KitShop(podium, kit);
                        kitShops.put(shop.getVillager().getUniqueId(), shop);
                        podium.setOccupied(true);
                        break;
                    }
                }
            }

            // Load the internalDataConfig and set the last map played ID.
            FileConfiguration internalDataConfig = YamlConfiguration
                    .loadConfiguration(new File(Arcade.getInstance()
                                    .getDataFolder(), "internalData.yml"));
            lastPlayedMapID = internalDataConfig
                    .getInt("skywars.lastPlayedMapID");

            // Find the next map.
            log("The last played map was "
                    + this.getConfiguredMaps().get(lastPlayedMapID)
                    + " attempting to find the next map...");
            final int nextMapID = SkywarsRotationManager.getNextMap();
            final String nextMapName = this.getConfiguredMaps().get(nextMapID);
            log("The next map playing is " + nextMapName + ".");

            // Set the lastPlayedMap as the found nextMap.
            log("Saving " + nextMapName + " as the new last played map.");
            internalDataConfig.set("skywars.lastPlayedMapID", nextMapID);
            internalDataConfig.save(new File(Arcade.getInstance()
                    .getDataFolder(), "internalData.yml"));

            // Download and load the next map.
            log("Attempting to load the map with name " + nextMapName);
            currentMapData = SkywarsMapManager.downloadAndLoadMapWithName(this,
                    nextMapName);
            log("Map downloaded and loaded successfuly.");

            // Print out the currentMap information.
            log("Current map minimum players: "
                    + currentMapData.getMinPlayers());
            log("Current map maximum players: "
                    + currentMapData.getMaxPlayers());
            log("Current map timer length: "
                    + currentMapData.getTimerLengthSeconds());
            log("Current map authors: "
                    + currentMapData.getAuthors().keySet().toString());

            // Load the current gameDescription message.
            this.gameDescriptionMessage = DescriptionUtils
                    .buildGameDescriptionMessage(this.getCustomName(), this
                            .getPluginDescription(), nextMapName, this
                            .getCurrentMapData().getAuthors(), this
                            .getCurrentMapData().getMinPlayers(), this
                            .getCurrentMapData().getMaxPlayers());

            // Craft the ScoreboardManager.
            this.scoreboardManager = new SkywarsScoreboardManager(this);

            // Craft the signs manager.
            this.signsManager = new SkywarsSignsManager(this);

            // Register all events.
            PluginManager pm = getServer().getPluginManager();
            pm.registerEvents(new SkywarsPlayerListener(), Arcade.getInstance());
            pm.registerEvents(new SkywarsBlockListener(), Arcade.getInstance());
            pm.registerEvents(new SkywarsEntityListener(), Arcade.getInstance());
            pm.registerEvents(new SkywarsMiscListener(), Arcade.getInstance());
            pm.registerEvents(new SkywarsInventoryListener(), Arcade.getInstance());

            // Set the current game stage.
            currentStage = stages.get(0);

            // Start the gameTask.
            gameTask = new SkywarsGameTask();
            gameTask.runTaskTimerAsynchronously(Arcade.getInstance(), 0L, 20L);

            // Needed - ArcadeMiniPlugin
            super.postEnable();
        } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Needed - ArcadeMiniPlugin
        super.onDisable();

        // Needed - ArcadeMiniPlugin
        super.postDisable();
    }

    @Override
    public void postEnable() {
    }

    @Override
    public void postDisable() {
    }

    public boolean hasEnoughPlayersToStart() {
        return this.players.size() >= this.currentMapData.getMinPlayers();
    }

    public boolean isFull() {
        return getPlayers().size() >= getCurrentMapData().getMaxPlayers();
    }

}
