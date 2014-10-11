package me.gtacraft.plugins.gangs.listeners;

import me.gtacraft.plugins.gangs.GTAGangs;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public abstract class IListener implements Listener {

    protected GTAGangs plugin;

    public IListener() {
        plugin = GTAGangs.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
