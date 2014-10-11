/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import org.arkhamnetwork.arcade.commons.generator.EmptyWorldGenerator;
import org.arkhamnetwork.arcade.core.Arcade;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

/**
 *
 * @author devan_000
 */
public class WorldUtils {

    public static World loadWorld(String name, Difficulty difficulty,
            boolean pvpEnabled, boolean lockDay) {
        World world = Bukkit.getWorld(name);

        if (world != null) {
            Bukkit.unloadWorld(world, false);
            world = null;
        }

        WorldCreator creator = new WorldCreator(name);
        creator.environment(Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator(new EmptyWorldGenerator());
        world = creator.createWorld();
        world.setDifficulty(difficulty);
        world.setSpawnFlags(false, false);
        world.setPVP(pvpEnabled);
        world.setStorm(false);
        world.setThundering(false);
        world.setKeepSpawnInMemory(false);
        world.setTicksPerAnimalSpawns(0);
        world.setTicksPerMonsterSpawns(0);
        world.setWeatherDuration(0);

        world.setAutoSave(false);
        world.setGameRuleValue("doFireTick", "false");
        world.setTime(6000L);

        if (lockDay) {
            final World lockWorld = world;

            Arcade.getInstance()
                    .getServer()
                    .getScheduler()
                    .runTaskTimerAsynchronously(Arcade.getInstance(),
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (lockWorld != null) {
                                        lockWorld.setTime(6000L);
                                    }
                                }
                            }, 100L, 100L);
        }

        return world;
    }

}
