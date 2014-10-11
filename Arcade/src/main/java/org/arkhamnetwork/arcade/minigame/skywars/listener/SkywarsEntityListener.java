/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.listener;

import org.arkhamnetwork.arcade.commons.pregame.PreGameManager;
import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

/**
 *
 * @author devan_000
 */
public class SkywarsEntityListener implements Listener {

    private SkyWars plugin = SkyWars.getSkywars();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShootBow(EntityShootBowEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.getEntity().teleport(PreGameManager.getSpawnLocation());
            }
        }
    }
}
