package me.gtacraft.npccore;

import me.gtacraft.npccore.controllers.ConfigController;
import me.gtacraft.npccore.controllers.EntityController;
import me.gtacraft.npccore.controllers.ThreadController;
import me.gtacraft.npccore.listeners.EntityListener;
import me.gtacraft.npccore.listeners.ServerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class GTANPCCore extends JavaPlugin {

    private static GTANPCCore plugin;

    public static GTANPCCore get() {
        return plugin;
    }

    private PluginDescriptionFile pdfFile = null;

    public ConfigController configController = null;
    public ThreadController threadController = null;
    public EntityController entityController = null;

    public BukkitTask aSyncCalculationThread = null;
    public BukkitTask mobSpawnThread = null;

    @Override
    public void onEnable() {
        plugin = this;
        pdfFile = getDescription();

        log("Plugin version " + pdfFile.getVersion() + " starting");

        loadControllers();

        this.configController.onEnable();
        this.threadController.onEnable();

        registerEvents();

        log("Plugin version " + pdfFile.getVersion() + " started");
    }

    @Override
    public void onDisable() {
        log("Plugin version " + pdfFile.getVersion() + " stopping");


        log("Plugin version " + pdfFile.getVersion() + " shutdown");
    }

    private void log(String message) {
        getLogger().info(message);
    }

    private void loadControllers() {
        this.configController = new ConfigController();
        this.threadController = new ThreadController();
        this.entityController = new EntityController();
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new ServerListener(), this);
        pm.registerEvents(new EntityListener(), this);
    }
}


