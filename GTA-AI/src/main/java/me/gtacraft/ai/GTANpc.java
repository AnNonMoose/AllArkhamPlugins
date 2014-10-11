package me.gtacraft.ai;

import me.gtacraft.ai.util.Util;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Connor on 6/14/14. Designed for the GTA-AI project.
 */

public class GTANpc extends JavaPlugin {

    private static GTANpc instance;

    public void onEnable() {
        instance = this;

        CitizensHook.init();

        createNamesFile();
    }
    private void createNamesFile() {
        try {
            File names = new File(getDataFolder(), "names.txt");
            if (!names.exists())
                names.createNewFile();

            Util.fillNames();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static GTANpc getInstance() {
        return instance;
    }

    public void onDisable() {

    }
}
