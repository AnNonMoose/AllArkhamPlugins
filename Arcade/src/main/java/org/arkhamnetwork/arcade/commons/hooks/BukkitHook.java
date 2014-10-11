/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.hooks;

import org.arkhamnetwork.arcade.commons.plugin.ArcadePlugin;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;

/**
 *
 * @author devan_000
 */
public class BukkitHook {

    public static void disableChunkGC(ArcadePlugin plugin) {
        ((CraftServer) plugin.getServer()).chunkGCPeriod = 0;
        ((CraftServer) plugin.getServer()).chunkGCLoadThresh = 0;
    }

}
