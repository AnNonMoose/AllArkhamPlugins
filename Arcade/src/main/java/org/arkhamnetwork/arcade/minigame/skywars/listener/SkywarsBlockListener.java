/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.listener;

import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author devan_000
 */
public class SkywarsBlockListener implements Listener {

    private SkyWars plugin = SkyWars.getSkywars();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(BlockDamageEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);
        }
    }

}
