package me.gtacraft.plugins.hub.util;

import me.gtacraft.plugins.hub.GTAHub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Connor on 7/8/14. Designed for the GTA-Hub project.
 */

public class GTAUtil {
    private static Map<String, Integer> countdowns = new HashMap<>();

    public static void initCountdowns() {
        Runnable run = new Runnable() {
            public void run() {
                Map<String, Integer> clone = new HashMap<>();
                for (String key : countdowns.keySet())
                    clone.put(key, countdowns.get(key));

                countdowns.clear();

                for (String key : clone.keySet()) {
                    int time = clone.get(key);
                    if (--time > 0)
                        countdowns.put(key, time);
                }

                clone.clear();
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(GTAHub.get(), run, 20l, 20l);
    }

    public static void addCountdownTask(String key, int time) {
        countdowns.put(key, time);
    }

    public static int getTimeLeft(String key) {
        return countdowns.containsKey(key) ? countdowns.get(key) : 0;
    }

    public static String getRegionName(Location l) {
        if(!(Bukkit.getPluginManager().isPluginEnabled("WorldGuard"))){
            // Ew.
            return "";
        }

        try {
            Class<?> bukkitUtil = GTAHub.wg.getClass().getClassLoader().loadClass("com.sk89q.worldguard.bukkit.BukkitUtil");
            Method toVector = bukkitUtil.getMethod("toVector", Block.class);
            com.sk89q.worldedit.Vector blockVector = (com.sk89q.worldedit.Vector) toVector.invoke(null, l.getBlock());

            List<String> regionSet = GTAHub.wg.getGlobalRegionManager().get(l.getWorld()).getApplicableRegionsIDs(blockVector);
            if (regionSet.size() < 1) {
                return "";
            }

            String return_region = "";
            int return_priority = -1;

            for (String region : regionSet) {
                int region_priority = GTAHub.wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getPriority();

                if(return_region.equalsIgnoreCase("")){
                    // We need to set SOMETHING.
                    return_region = GTAHub.wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getId();
                    return_priority = region_priority;
                }

                if(region_priority > return_priority){
                    return_region = GTAHub.wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getId();
                    return_priority = region_priority;
                }
            }

            if(return_region.contains("global")){
                return_region = "";
            }

            return return_region;

        } catch (Exception e) {

        }
        return "";
    }
}
