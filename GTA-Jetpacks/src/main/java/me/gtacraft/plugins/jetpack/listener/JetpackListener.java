package me.gtacraft.plugins.jetpack.listener;

import me.gtacraft.plugins.jetpack.GTAJetpack;
import me.gtacraft.plugins.jetpack.task.JetTask;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Created by Connor on 7/11/14. Designed for the GTA-Jetpacks project.
 */

public class JetpackListener implements Listener {

    public JetpackListener() {
        Bukkit.getPluginManager().registerEvents(this, GTAJetpack.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        JetTask.async_player_map.put(event.getPlayer().getName(), event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        JetTask.async_player_map.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (!(event.getPlayer().hasPermission("jetpack.bypass")) && event.isFlying())
            event.setCancelled(true);
    }
}


