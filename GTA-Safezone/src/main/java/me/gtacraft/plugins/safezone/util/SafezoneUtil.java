package me.gtacraft.plugins.safezone.util;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gtacraft.plugins.safezone.Safezone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Connor on 6/27/14. Designed for the GTA-Safezone project.
 */

public class SafezoneUtil {

    public static boolean isInSafeZone(Location location) {
        if (!(Safezone.getInstance().getWorld().equals(location.getWorld())))
            return false;

        for (ProtectedRegion pr : Safezone.getInstance().getSafeZoneRegions()) {
            if (pr.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                return true;
        }

        return false;
    }

    public static volatile ConcurrentHashMap<String, Integer> countdown = new ConcurrentHashMap<String, Integer>();

    public static void tick() {
        Runnable recall = new Runnable() {
            public void run() {
                for (String key : countdown.keySet()) {
                    int value = countdown.remove(key);
                    if (--value == 0) {
                        Player find = Bukkit.getPlayer(key);
                        if (find != null && !(SafezoneUtil.isInSafeZone(find.getLocation())))
                            find.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &eYou will now deal and receive damage!"));
                        continue;
                    }

                    countdown.put(key, value);
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Safezone.getInstance(), recall, 20l, 20l);
    }

    public static int getSubscribedTime(String key) {
        try {
            if (key == null)
                return -1;
            return countdown.containsKey(key) && countdown.get(key) != null ? ((Integer)countdown.get(key)).intValue() : -1;
        } catch (NullPointerException err) {
            return -1;
        }
    }

    public static void subscribe(String key, int time) {
        countdown.put(key, time);
    }

    public static void unsubscribe(String key) {
        countdown.remove(key);
    }
}
