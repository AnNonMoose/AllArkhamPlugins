/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.pregame;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.lingala.zip4j.exception.ZipException;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.FileConfiguration;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.YamlConfiguration;
import org.arkhamnetwork.arcade.commons.manager.Manager;
import org.arkhamnetwork.arcade.commons.plugin.ArcadeMiniPlugin;
import org.arkhamnetwork.arcade.commons.podium.Podium;
import org.arkhamnetwork.arcade.commons.utils.SpawnUtils;
import org.arkhamnetwork.arcade.commons.utils.WorldUtils;
import org.arkhamnetwork.arcade.commons.utils.ZipUtils;
import org.arkhamnetwork.arcade.core.Arcade;
import org.arkhamnetwork.arcade.core.configuration.ArcadeConfiguration;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

/**
 *
 * @author devan_000
 */
public class PreGameManager extends Manager {

    @Getter
    private static World preGameWorld;
    private static String WORLD_ZIP_PATH = "Lobby/PreGame.zip";
    private static String WORLD_CONFIG_PATH = "Lobby/PreGame.yml";
    @Getter
    private static Location spawnLocation;
    @Getter
    private static Location dynamicSideSign1;
    @Getter
    private static Location dynamicSideSign2;
    @Getter
    private static final BlockFace dynamicSideSign1Face = BlockFace.NORTH;
    @Getter
    private static final BlockFace dynamicSideSign2Face = BlockFace.SOUTH;
    @Getter
    private static List<Podium> podiums = new ArrayList<>();

    /**
     * Downloads and loads the PreGame world.
     *
     * @param plugin
     */
    public static void setup(ArcadeMiniPlugin plugin) {
        try {
            plugin.log("Attempting to download the pre-game lobby.");
            downloadWorldToCache();
            plugin.log("The pre-game lobby was download successfuly.");

            plugin.log("Attempting to unzip the pre-game lobby.");
            unzipWorld(plugin);
            plugin.log("The pre-game lobby was extracted successfuly.");

            plugin.log("Attempting to load the pre-game world");
            loadWorld();
            plugin.log("The pre-game world loaded successfuly.");

            plugin.log("Attempting to download the pre-game world config");
            downloadConfigToCache();
            plugin.log("The pre-game world config downloaded successfuly.");

            FileConfiguration preGameConfig = YamlConfiguration
                    .loadConfiguration(new File(Arcade.getInstance()
                                    .getDataFolder() + File.separator + "cache",
                                    "PreGame.yml"));
            spawnLocation = SpawnUtils.getLocationFromSection(preGameConfig
                    .getConfigurationSection("spawn"));

            dynamicSideSign1 = new Location(spawnLocation.getWorld(),
                    spawnLocation.getBlockX() - 48,
                    spawnLocation.getBlockY() + 45, spawnLocation.getBlockZ());
            dynamicSideSign2 = new Location(spawnLocation.getWorld(),
                    spawnLocation.getBlockX() + 48,
                    spawnLocation.getBlockY() + 45, spawnLocation.getBlockZ());

            for (String podiumLocation : preGameConfig.getStringList("podiums")) {
                podiums.add(new Podium(SpawnUtils
                        .getLocationFromString(podiumLocation)));
                plugin.log("Loaded a podium at " + podiumLocation);
            }
        } catch (IOException | ZipException ex) {
            plugin.log("An exception was thrown while downloading the pre-game lobby.");
            ex.printStackTrace();
        }
    }

    private static void downloadWorldToCache() throws IOException {
        File cacheZIPLocation = new File(Arcade.getInstance().getDataFolder()
                + File.separator + "cache", "PreGame.zip");

        if (cacheZIPLocation.exists()) {
            cacheZIPLocation.delete();
        }

        FileUtils.copyURLToFile(new URL(ArcadeConfiguration.getWebServerURL()
                + WORLD_ZIP_PATH), cacheZIPLocation);
    }

    private static void unzipWorld(ArcadeMiniPlugin plugin) throws IOException,
            ZipException {
        File worldLocation = new File(plugin.getServer().getWorldContainer()
                + File.separator + "PreGameLobby");

        if (worldLocation.exists()) {
            FileUtils.deleteDirectory(worldLocation);
        }

        ZipUtils.unZip(new File(Arcade.getInstance().getDataFolder()
                + File.separator + "cache", "PreGame.zip"), worldLocation);
    }

    private static void loadWorld() {
        WorldUtils.loadWorld("PreGameLobby", Difficulty.EASY, false, true);
    }

    private static void downloadConfigToCache() throws MalformedURLException,
            IOException {
        File cacheYMLLocation = new File(Arcade.getInstance().getDataFolder()
                + File.separator + "cache", "PreGame.yml");

        if (cacheYMLLocation.exists()) {
            cacheYMLLocation.delete();
        }

        FileUtils.copyURLToFile(new URL(ArcadeConfiguration.getWebServerURL()
                + WORLD_CONFIG_PATH), cacheYMLLocation);
    }

}
