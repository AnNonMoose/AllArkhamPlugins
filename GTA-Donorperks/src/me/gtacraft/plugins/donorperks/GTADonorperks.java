package me.gtacraft.plugins.donorperks;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Connor on 7/16/14. Designed for the GTA-Donorperks project.
 */

public class GTADonorperks extends JavaPlugin {

    private static GTADonorperks instance;

    public static GTADonorperks getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
    }

    public void onDisable() {
        saveDefaultConfig();
    }
}
