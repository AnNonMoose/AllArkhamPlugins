/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.generator;

import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author devan_000
 */
public class EmptyWorldGenerator extends ChunkGenerator {

    @Override
    public byte[] generate(World world, Random random, int cx, int cz) {
        return new byte[65536];
    }

}
