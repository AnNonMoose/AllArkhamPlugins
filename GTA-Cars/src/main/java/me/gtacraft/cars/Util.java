package me.gtacraft.cars;

import org.bukkit.Bukkit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Connor on 7/14/14. Designed for the GTA-Cars project.
 */

public class Util {
    public static ConcurrentHashMap<String, Integer> countdown = new ConcurrentHashMap<String, Integer>();

    public static void tick() {
        Runnable recall = new Runnable() {
            public void run() {
                for (String key : countdown.keySet()) {
                    int value = countdown.remove(key);
                    if (--value == 0)
                        continue;

                    countdown.put(key, value);
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(CarsPlugin.getInstance(), recall, 1l, 1l);
    }

    public static int getSubscribedTime(String key) {
        return (countdown.containsKey(key) ? countdown.get(key) : -1);
    }

    public static void subscribe(String key, int time) {
        countdown.put(key, time);
    }

    public static void unsubscribe(String key) {
        countdown.remove(key);
    }

}
