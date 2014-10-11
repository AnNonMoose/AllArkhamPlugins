/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import java.util.Collection;
import java.util.HashSet;
import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_7_R3.ChunkSection;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import org.arkhamnetwork.arcade.core.Arcade;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_7_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author devan_000
 */
public class MapUtils {

    public static Chunk changeBlockAt(org.bukkit.World world, int x, int y, int z, int id, int data) {
        Chunk bukkitChunk = world.getChunkAt(x >> 4, z >> 4);
        if (!bukkitChunk.isLoaded()) {
            bukkitChunk.load(false);
        }
        net.minecraft.server.v1_7_R3.Chunk c = ((CraftChunk) bukkitChunk).getHandle();

        c.a(x & 0x0f, y, z & 0x0f, net.minecraft.server.v1_7_R3.Block.e(id), data);
        return bukkitChunk;
    }

    public static void resendChunksForPlayers(final HashSet<Chunk> chunks) {
        Arcade.getInstance().getServer().getScheduler()
                .runTask(Arcade.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        for (Chunk chunk : chunks) {
                            final Player[] onlinePlayers = Bukkit
                            .getOnlinePlayers();
                            for (Player player : onlinePlayers) {
                                // Lets check if the player is close.
                                int viewDistance = 24;

                                Vector pV = player.getLocation().toVector();
                                int xDist = Math.abs((pV.getBlockX() >> 4)
                                        - chunk.getX());
                                int zDist = Math.abs((pV.getBlockZ() >> 4)
                                        - chunk.getZ());
                                int distanceFromChunkInChunks = (xDist + zDist);

                                if (distanceFromChunkInChunks <= viewDistance) {
                                    sendChunkForPlayer(chunk.getX(),
                                            chunk.getZ(), player);
                                }
                            }
                        }
                    }
                });
    }

    private static void sendChunkForPlayer(int x, int z, Player player) {
        EntityPlayer pl = ((CraftPlayer) player).getHandle();
        ChunkCoordIntPair pair = new ChunkCoordIntPair(x, z);
        if (!pl.chunkCoordIntPairQueue.contains(pair)) {
            pl.chunkCoordIntPairQueue.add(pair);
        }
    }

}
