package me.gtacraft.config;

import me.gtacraft.GTAGuns;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 4/27/14. Designed for the GTA-Guns project.
 */

public class ConfigFolder {

    private List<ConfigFile> files = new ArrayList<ConfigFile>();
    private File folder;

    public ConfigFolder(Plugin plugin, File folder) {
        this.folder = folder;
        try {
            if (!this.folder.exists()) this.folder.mkdir();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        discoverFiles();
    }

    public void discoverFiles() {
        files.clear();
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                ConfigFile cf = new ConfigFile(GTAGuns.getInstnace(), file);
                files.add(cf);
            }
        }
    }

    public List<ConfigFile> getConfigFiles() {
        return files;
    }
}
