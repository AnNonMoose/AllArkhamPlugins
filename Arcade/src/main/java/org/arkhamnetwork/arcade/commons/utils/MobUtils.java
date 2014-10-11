/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import org.arkhamnetwork.arcade.core.Arcade;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftChunk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

/**
 *
 * @author devan_000
 */
public class MobUtils {

    private static Arcade plugin = Arcade.getInstance();

    public static Villager spawnVillager(final Location location, boolean frozen) {
        if (!location.getChunk().isLoaded()) {
            location.getChunk().load(true);
        }

        final Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        if (frozen) {
            NMSUtil.overwriteVillagerAI(villager);
            plugin.getServer().getScheduler()
                    .runTaskTimerAsynchronously(plugin, new Runnable() {
                        public void run() {
                            villager.teleport(location);
                        }
                    }, 1L, 1L);
        }
        return villager;
    }

}
