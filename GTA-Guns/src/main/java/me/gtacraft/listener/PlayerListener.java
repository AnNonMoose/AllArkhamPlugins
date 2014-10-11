package me.gtacraft.listener;

import me.gtacraft.player.GunHolder;
import me.gtacraft.util.ExplosionUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

/**
 * Created by Connor on 4/28/14. Designed for the GTA-Guns project.
 */

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        GunHolder.getHolder(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        GunHolder.handleLeave(e.getPlayer());
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent e) {
        GunHolder.handleLeave(e.getPlayer());
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        if (ExplosionUtil.getDeniedItems().contains(e.getItem()))
            e.setCancelled(true);
    }
}
