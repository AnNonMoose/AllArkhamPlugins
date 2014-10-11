package me.gtacraft.plugins.chests;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;

import java.util.HashMap;

/**
 * Created by Connor on 7/3/14. Designed for the GTA-Chests project.
 */

public class ChestManager {

    //stores a map of all containers that need to be refilled in the given integer value
    private static HashMap<Location, Integer> refill = new HashMap<>();

    public static void tickDown() {
        Runnable async = new Runnable() {
            public void run() {

                HashMap<Location, Integer> clone = new HashMap<>(refill);
                refill.clear();

                for (final Location c : clone.keySet()) {
                    int left = clone.get(c);
                    if (left-- <= 0) {
                        //remove
                        GTAChests.log.debug("Clearing chest at: "+(c.getWorld().getName()+","+c.getBlockX()+","+c.getBlockY()+","+c.getBlockZ()), getClass());
                        Runnable sync = new Runnable() {
                            public void run() {
                                if (!c.getBlock().getType().equals(Material.CHEST))
                                    c.getBlock().setType(Material.CHEST);

                                final Chunk open = c.getChunk();
                                final boolean isLoaded = open.isLoaded();

                                if (!isLoaded)
                                    open.load();

                                if (c.getBlock().getState() instanceof Chest) {
                                    Chest cl = (Chest)c.getBlock().getState();
                                    cl.getInventory().clear();
                                    cl.update();
                                } else if (c.getBlock().getState() instanceof DoubleChest) {
                                    DoubleChest cl = (DoubleChest)c.getBlock().getState();
                                    cl.getInventory().clear();
                                    c.getBlock().getState().update();
                                }

                                if (!isLoaded)
                                    open.unload();
                            }
                        };
                        Bukkit.getScheduler().scheduleSyncDelayedTask(GTAChests.getInstance(), sync);
                        continue;
                    }

                    refill.put(c, left);
                }
                //recurse
                tickDown();
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAChests.getInstance(), async, 20l);
    }

    public static boolean contains(Location loc) {
        return refill.containsKey(loc);
    }

    public static void add(Location build) {
        refill.put(build, GTAChests.getInstance().getResetTime());
    }

    public static void remove(Location destroy) {
        refill.remove(destroy);
    }

    public static void clear() {
        refill.clear();
    }
}
