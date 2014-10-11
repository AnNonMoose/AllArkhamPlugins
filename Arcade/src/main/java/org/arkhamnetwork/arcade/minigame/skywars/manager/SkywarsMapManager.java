/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.FileConfiguration;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.YamlConfiguration;
import org.arkhamnetwork.arcade.commons.manager.Manager;
import org.arkhamnetwork.arcade.commons.plugin.ArcadeMiniPlugin;
import org.arkhamnetwork.arcade.commons.utils.WorldUtils;
import org.arkhamnetwork.arcade.commons.utils.ZipUtils;
import org.arkhamnetwork.arcade.core.Arcade;
import org.arkhamnetwork.arcade.core.configuration.ArcadeConfiguration;
import org.arkhamnetwork.arcade.minigame.skywars.struct.SkywarsMapData;
import org.bukkit.Difficulty;

/**
 *
 * @author devan_000
 */
public class SkywarsMapManager extends Manager {

    public static SkywarsMapData downloadAndLoadMapWithName(
            ArcadeMiniPlugin plugin, String mapName) {
        try {
            plugin.log("Attempting to download the " + mapName + ".zip");
            downloadMapZIP(mapName);
            plugin.log(mapName + ".zip downloaded successfuly.");

            plugin.log("Attempting to download the " + mapName + ".yml");
            downloadMapYML(mapName);
            plugin.log(mapName + ".yml downloaded successfuly.");

            plugin.log("Attempting to un-zip the " + mapName + ".zip to the "
                    + mapName + " world folder.");
            unZIPWorld(plugin, mapName);
            plugin.log("Map unzipped successfuly.");

            plugin.log("Attempting to load the " + mapName + " world.");
            loadWorld(mapName);
            plugin.log("World loaded successfuly.");

            FileConfiguration mapConfiguration = YamlConfiguration
                    .loadConfiguration(new File(Arcade.getInstance()
                                    .getDataFolder() + File.separator + "cache",
                                    mapName + ".yml"));

            return new SkywarsMapData(mapName,
                    mapConfiguration.getInt("minPlayers"),
                    mapConfiguration.getInt("maxPlayers"),
                    mapConfiguration.getInt("timerSeconds"),
                    mapConfiguration.getConfigurationSection("authors"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static void downloadMapZIP(String mapName) throws IOException {
        File cacheZIPLocation = new File(Arcade.getInstance().getDataFolder()
                + File.separator + "cache", mapName + ".zip");

        if (cacheZIPLocation.exists()) {
            cacheZIPLocation.delete();
        }

        FileUtils.copyURLToFile(new URL(ArcadeConfiguration.getWebServerURL()
                + "/Skywars/Maps/" + mapName + "/" + mapName + ".zip"),
                cacheZIPLocation);
    }

    private static void downloadMapYML(String mapName) throws IOException {
        File cacheYMLLocation = new File(Arcade.getInstance().getDataFolder()
                + File.separator + "cache", mapName + ".yml");

        if (cacheYMLLocation.exists()) {
            cacheYMLLocation.delete();
        }

        FileUtils.copyURLToFile(new URL(ArcadeConfiguration.getWebServerURL()
                + "/Skywars/Maps/" + mapName + "/" + mapName + ".yml"),
                cacheYMLLocation);
    }

    private static void unZIPWorld(ArcadeMiniPlugin plugin, String mapName)
            throws Exception {
        File worldLocation = new File(plugin.getServer().getWorldContainer()
                + File.separator + mapName);

        if (worldLocation.exists()) {
            FileUtils.deleteDirectory(worldLocation);
        }

        ZipUtils.unZip(new File(Arcade.getInstance().getDataFolder()
                + File.separator + "cache", mapName + ".zip"), worldLocation);
    }

    private static void loadWorld(String mapName) {
        WorldUtils.loadWorld(mapName, Difficulty.NORMAL, true, false);
    }
}
