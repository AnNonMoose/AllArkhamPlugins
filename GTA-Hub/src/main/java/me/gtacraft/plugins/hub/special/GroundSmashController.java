package me.gtacraft.plugins.hub.special;

import com.google.common.collect.Lists;
import me.gtacraft.plugins.hub.GTAHub;
import me.gtacraft.plugins.hub.util.GTAUtil;
import me.vaqxine.WorldRegeneration.RegenerationAPI;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Connor on 7/8/14. Designed for the GTA-Hub project.
 */

public class GroundSmashController {

    /*public static List<Block> port_locations = Lists.newArrayList();

    public static void handleSmash(Player player) {
        //validate cooldown
        if (GTAUtil.getTimeLeft(player.getName()+"_smash") != 0) {
            //cooling down
            player.sendMessage(ChatColor.RED+"You cannot use this feature for another "+ ChatColor.YELLOW+GTAUtil.getTimeLeft(player.getName()+"_smash")+" seconds"+ ChatColor.RED+"!");
            return;
        }

        final List<Block> gen = inLOS(player);
        port_locations.addAll(gen);

        Runnable syncIt = new Runnable() {
            public void run() {
                int i = 1;
                for (final Block l : gen) {
                    Runnable delay = new Runnable() {
                        @Override
                        public void run() {
                            RegenerationAPI.queueBlockForRegeneration(l, 10);

                            //crack
                            sendCrackPacket(l, l.getTypeId());
                            l.setType(Material.LAVA);
                        }
                    };
                    Bukkit.getScheduler().scheduleSyncDelayedTask(GTAHub.get(), delay, i);
                    ++i;
                }
            }
        };
        Bukkit.getScheduler().scheduleSyncDelayedTask(GTAHub.get(), syncIt);

        Runnable later = new Runnable() {
            public void run() {
                port_locations.removeAll(gen);
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAHub.get(), later, 10*20l);

        GTAUtil.addCountdownTask(player.getName()+"_smash", 20);
    }

    private static List<Block> inLOS(Player player) {
        Vector pVec = player.getEyeLocation().getDirection();
        pVec = pVec.setY(0);

        BlockIterator bi = new BlockIterator(player.getWorld(), player.getLocation().subtract(0, 1, 0).toVector(), pVec.normalize(), 0, 7);
        List<Block> values = Lists.newArrayList();
        int skipFirst = -1;
        while (bi.hasNext()) {
            ++skipFirst;
            Block change = bi.next();
            if (skipFirst == 0)
                continue;
            values.add(change);
        }

        return values;
    }

    private static void sendCrackPacket(Block b, int typeID) {
        Packet particles = new PacketPlayOutWorldEvent(2001, Math.round(b.getX()), Math.round(b.getY()), Math.round(b.getZ()), typeID, false);
        ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().sendPacketNearby(b.getX(), b.getY(), b.getZ(), 16, ((CraftWorld) b.getWorld()).getHandle().dimension, particles);
    }*/
}
