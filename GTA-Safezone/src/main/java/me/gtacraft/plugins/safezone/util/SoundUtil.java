package me.gtacraft.plugins.safezone.util;

import me.gtacraft.GTAGuns;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Connor on 4/28/14. Designed for the GTA-Guns project.
 */

public class SoundUtil {
    public static void playSound(final Location loc, String load, final Player... players) {
        if (load.equals(""))
            return;

        for (String play : load.split(",")) {
            String[] split = play.split("-");
            final Sound sound = Sound.valueOf(split[0].toUpperCase().replace(" ", "_"));
            final int vol = (int)Double.parseDouble(split[1]);
            final double pitch = Double.parseDouble(split[2]);
            new BukkitRunnable() {
                public void run() {
                    for (Player player : players) {
                        player.playSound(loc, sound, vol, (float) pitch);
                    }
                }
            }.runTaskLater(GTAGuns.getInstnace(), (long)Double.parseDouble(split[3]));
        }
    }

    public static void playSound(final Player player, String load, final Player... players) {
        if (load.equals(""))
            return;

        for (String play : load.split(",")) {
            String[] split = play.split("-");
            final Sound sound = Sound.valueOf(split[0].toUpperCase().replace(" ", "_"));
            final int vol = (int)Double.parseDouble(split[1]);
            final double pitch = Double.parseDouble(split[2]);
            new BukkitRunnable() {
                public void run() {
                    for (Player play : players) {
                        play.playSound(player.getLocation(), sound, vol, (float) pitch);
                    }
                }
            }.runTaskLater(GTAGuns.getInstnace(), (long)Double.parseDouble(split[3]));
        }
    }
}
