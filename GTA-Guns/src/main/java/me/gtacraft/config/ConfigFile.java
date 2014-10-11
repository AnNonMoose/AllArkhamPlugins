package me.gtacraft.config;

import me.gtacraft.GTAGuns;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * Created by Connor on 4/27/14. Designed for the GTA-Guns project.
 */

public class ConfigFile {

    private FileConfiguration conf;
    private File f;

    public ConfigFile(Plugin plugin, File file)
    {
        f = file;
        try
        {
            if (!f.exists()) f.createNewFile();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        reloadFile();
    }

    public FileConfiguration getFile()
    {
        return conf;
    }

    public void saveFile()
    {
        try { conf.save(f); } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void reloadFile()
    {
        conf = YamlConfiguration.loadConfiguration(f);
        saveFile();
    }

    public String getName() {
        return f.getName().substring(0, f.getName().indexOf(".")-1);
    }

    public String toString() {
        return f.getName()+", "+conf.getName();
    }
}
