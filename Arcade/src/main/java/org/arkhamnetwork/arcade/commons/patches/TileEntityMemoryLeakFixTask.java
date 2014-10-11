/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.patches;

import java.util.Iterator;

import net.minecraft.server.v1_7_R3.IInventory;

import org.arkhamnetwork.arcade.core.Arcade;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author devan_000
 */
public class TileEntityMemoryLeakFixTask extends BukkitRunnable {

    private final Arcade plugin = Arcade.getInstance();

    @Override
    public synchronized void run() {
        for (World world : plugin.getServer().getWorlds()) {
            for (final Object tileEntity : ((CraftWorld) world).getHandle().tileEntityList) {
                if (tileEntity instanceof IInventory) {
                    final Iterator<HumanEntity> entityIterator = ((IInventory) tileEntity)
                            .getViewers().iterator();
                    while (entityIterator.hasNext()) {
                        final HumanEntity entity = entityIterator.next();
                        if (entity instanceof CraftPlayer
                                && !((CraftPlayer) entity).isOnline()) {
                            entityIterator.remove();
                        }
                    }
                }
            }
        }
    }

}
