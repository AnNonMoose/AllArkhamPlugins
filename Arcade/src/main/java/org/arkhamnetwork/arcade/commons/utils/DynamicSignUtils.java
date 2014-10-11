/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.CraftChunk;

/**
 *
 * @author devan_000
 */
public class DynamicSignUtils {

    public static enum TextAlign {

        LEFT, RIGHT, CENTER;
    }

    public static HashMap<Character, int[][]> alphabet = new HashMap();

    public static void makeText(String string, Location loc, BlockFace face,
            int id, byte data, TextAlign align, boolean setAir) {
        if (alphabet.isEmpty()) {
            populateAlphabet();
        }
        Block block = loc.getBlock();

        int width = 0;
        for (char c : string.toLowerCase().toCharArray()) {
            int[][] letter = (int[][]) alphabet.get(Character.valueOf(c));
            if (letter != null) {
                width += letter[0].length + 1;
            }
        }
        if ((align == TextAlign.CENTER) || (align == TextAlign.RIGHT)) {
            int divisor = 1;
            if (align == TextAlign.CENTER) {
                divisor = 2;
            }
            block = block.getRelative(face, -1 * width / divisor + 1);
        }

        HashSet<Chunk> chunksToSend = new HashSet<>();

        if (setAir) {
            final World world = loc.getWorld();
            final int bX = loc.getBlockX();
            int bY = loc.getBlockY();
            final int bZ = loc.getBlockZ();
            for (int y = 0; y < 5; ++y) {
                if (align == TextAlign.CENTER) {
                    for (int i = -64; i <= 64; ++i) {
                        chunksToSend.add(MapUtils.changeBlockAt(world, bX + i
                                * face.getModX(), bY + i * face.getModY(), bZ
                                + i * face.getModZ(), 0, (byte) 0));
                    }
                }
                if (align == TextAlign.LEFT) {
                    for (int i = 0; i <= 128; ++i) {
                        chunksToSend.add(MapUtils.changeBlockAt(world, bX + i
                                * face.getModX(), bY + i * face.getModY(), bZ
                                + i * face.getModZ(), 0, (byte) 0));
                    }
                }
                if (align == TextAlign.RIGHT) {
                    for (int i = -128; i <= 0; ++i) {
                        chunksToSend.add(MapUtils.changeBlockAt(world, bX + i
                                * face.getModX(), bY + i * face.getModY(), bZ
                                + i * face.getModZ(), 0, (byte) 0));
                    }
                }
                --bY;
            }
        }

        final World world = block.getWorld();
        int bX = block.getX();
        int bY = block.getY();
        int bZ = block.getZ();
        char[] charArray2;
        for (int length2 = (charArray2 = string.toLowerCase().toCharArray()).length, k = 0; k < length2; ++k) {
            final char c2 = charArray2[k];
            final int[][] letter2 = alphabet.get(c2);
            if (letter2 != null) {
                for (int x = 0; x < letter2.length; ++x) {
                    for (int y2 = 0; y2 < letter2[x].length; ++y2) {
                        if (letter2[x][y2] == 1) {
                            chunksToSend.add(MapUtils.changeBlockAt(world, bX,
                                    bY, bZ, id, data));
                        }
                        bX += face.getModX();
                        bY += face.getModY();
                        bZ += face.getModZ();
                    }
                    bX += face.getModX() * -1 * letter2[x].length;
                    bY += face.getModY() * -1 * letter2[x].length;
                    bZ += face.getModZ() * -1 * letter2[x].length;
                    --bY;
                }
                bY += 5;
                bX += face.getModX() * (letter2[0].length + 1);
                bY += face.getModY() * (letter2[0].length + 1);
                bZ += face.getModZ() * (letter2[0].length + 1);
            }
        }

        for (Chunk chunk : chunksToSend) {
            ((net.minecraft.server.v1_7_R3.Chunk) ((CraftChunk) chunk).getHandle()).initLighting();
        }

        MapUtils.resendChunksForPlayers(chunksToSend);
    }

    private static void populateAlphabet() {
        alphabet.put('0', new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 0, 1},
        {1, 0, 1}, {1, 1, 1}});

        alphabet.put('1', new int[][]{{1, 1}, {0, 1}, {0, 1}, {0, 1},
        {1, 1, 1}});

        alphabet.put('2', new int[][]{{1, 1, 1}, {0, 0, 1}, {1, 1, 1},
        {1}, {1, 1, 1}});

        alphabet.put('3', new int[][]{{1, 1, 1}, {0, 0, 1}, {1, 1, 1},
        {0, 0, 1}, {1, 1, 1}});

        alphabet.put('4', new int[][]{{1, 0, 1}, {1, 0, 1}, {1, 1, 1},
        {0, 0, 1}, {0, 0, 1}});

        alphabet.put('5', new int[][]{{1, 1, 1}, {1}, {1, 1, 1},
        {0, 0, 1}, {1, 1, 1}});

        alphabet.put('6', new int[][]{{1, 1, 1}, {1}, {1, 1, 1},
        {1, 0, 1}, {1, 1, 1}});

        alphabet.put('7', new int[][]{{1, 1, 1}, {0, 0, 1}, {0, 0, 1},
        {0, 0, 1}, {0, 0, 1}});

        alphabet.put('8', new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1},
        {1, 0, 1}, {1, 1, 1}});

        alphabet.put('9', new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1},
        {0, 0, 1}, {1, 1, 1}});

        alphabet.put('.', new int[][]{new int[1], new int[1], new int[1],
            new int[1], {1}});

        alphabet.put('!',
                new int[][]{{1}, {1}, {1}, new int[1], {1}});

        alphabet.put(' ', new int[][]{new int[2], new int[2], new int[2],
            new int[2], new int[2]});

        alphabet.put('a', new int[][]{{1, 1, 1, 1}, {1, 0, 0, 1},
        {1, 1, 1, 1}, {1, 0, 0, 1}, {1, 0, 0, 1}});

        alphabet.put('b', new int[][]{{1, 1, 1, 0}, {1, 0, 0, 1},
        {1, 1, 1, 0}, {1, 0, 0, 1}, {1, 1, 1, 0}});

        alphabet.put('c', new int[][]{{1, 1, 1, 1}, {1}, {1}, {1},
        {1, 1, 1, 1}});

        alphabet.put('d', new int[][]{{1, 1, 1, 0}, {1, 0, 0, 1},
        {1, 0, 0, 1}, {1, 0, 0, 1}, {1, 1, 1, 0}});

        alphabet.put('e', new int[][]{{1, 1, 1, 1}, {1}, {1, 1, 1},
        {1}, {1, 1, 1, 1}});

        alphabet.put('f', new int[][]{{1, 1, 1, 1}, {1}, {1, 1, 1},
        {1}, {1}});

        alphabet.put('g', new int[][]{{1, 1, 1, 1}, {1}, {1, 0, 1, 1},
        {1, 0, 0, 1}, {1, 1, 1, 1}});

        alphabet.put('h', new int[][]{{1, 0, 0, 1}, {1, 0, 0, 1},
        {1, 1, 1, 1}, {1, 0, 0, 1}, {1, 0, 0, 1}});

        alphabet.put('i', new int[][]{{1, 1, 1}, {0, 1}, {0, 1},
        {0, 1}, {1, 1, 1}});

        alphabet.put('j', new int[][]{{1, 1, 1, 1}, {0, 0, 1},
        {0, 0, 1}, {1, 0, 1}, {1, 1, 1}});

        alphabet.put('k', new int[][]{{1, 0, 0, 1}, {1, 0, 1}, {1, 1},
        {1, 0, 1}, {1, 0, 0, 1}});

        alphabet.put('l', new int[][]{{1, 0, 0, 0}, {1, 0, 0, 0},
        {1, 0, 0, 0}, {1, 0, 0, 0}, {1, 1, 1, 1}});

        alphabet.put('m', new int[][]{{1, 1, 1, 1, 1}, {1, 0, 1, 0, 1},
        {1, 0, 1, 0, 1}, {1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}});

        alphabet.put('n', new int[][]{{1, 0, 0, 1}, {1, 1, 0, 1},
        {1, 0, 1, 1}, {1, 0, 0, 1}, {1, 0, 0, 1}});

        alphabet.put('o', new int[][]{{1, 1, 1, 1}, {1, 0, 0, 1},
        {1, 0, 0, 1}, {1, 0, 0, 1}, {1, 1, 1, 1}});

        alphabet.put('p', new int[][]{{1, 1, 1, 1}, {1, 0, 0, 1},
        {1, 1, 1, 1}, {1}, {1}});

        alphabet.put('q', new int[][]{{1, 1, 1, 1}, {1, 0, 0, 1},
        {1, 0, 0, 1}, {1, 0, 1}, {1, 1, 0, 1}});

        alphabet.put('r', new int[][]{{1, 1, 1, 1}, {1, 0, 0, 1},
        {1, 1, 1}, {1, 0, 0, 1}, {1, 0, 0, 1}});

        alphabet.put('s', new int[][]{{1, 1, 1, 1}, {1}, {1, 1, 1, 1},
        {0, 0, 0, 1}, {1, 1, 1, 1}});

        alphabet.put('t', new int[][]{{1, 1, 1, 1, 1}, {0, 0, 1},
        {0, 0, 1}, {0, 0, 1}, {0, 0, 1}});

        alphabet.put('u', new int[][]{{1, 0, 0, 1}, {1, 0, 0, 1},
        {1, 0, 0, 1}, {1, 0, 0, 1}, {1, 1, 1, 1}});

        alphabet.put('v', new int[][]{{1, 0, 0, 1}, {1, 0, 0, 1},
        {1, 0, 0, 1}, {1, 0, 0, 1}, {0, 1, 1}});

        alphabet.put('w', new int[][]{{1, 0, 0, 0, 1}, {1, 0, 0, 0, 1},
        {1, 0, 1, 0, 1}, {1, 0, 1, 0, 1}, {1, 1, 1, 1, 1}});

        alphabet.put('x', new int[][]{{1, 0, 0, 1}, {1, 0, 0, 1},
        {0, 1, 1}, {1, 0, 0, 1}, {1, 0, 0, 1}});

        alphabet.put('y', new int[][]{{1, 0, 0, 1}, {1, 0, 0, 1},
        {1, 1, 1, 1}, {0, 0, 0, 1}, {1, 1, 1, 1}});

        alphabet.put('z', new int[][]{{1, 1, 1, 1}, {0, 0, 0, 1},
        {0, 0, 1}, {0, 1}, {1, 1, 1, 1}});

        alphabet.put(Character.valueOf('!'), new int[][]{{1}, {1}, {1},
        {0}, {1}});
    }

}
