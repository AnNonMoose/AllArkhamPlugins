package me.gtacraft.plugins.gangs.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class LocationUtil {

    public static String fromLocation(Location location) {
        String wd_ = location.getWorld().getName();
        double x_ = location.getX();
        double y_ = location.getY();
        double z_ = location.getZ();
        float yw_ = location.getYaw();
        float pw_ = location.getPitch();

        return wd_+","+x_+","+y_+","+z_+","+yw_+","+pw_;
    }

    public static Location fromString(String string) {
        if (string.equals("NULL"))
            return null;

        String[] split = string.split(",");
        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[4]);
        float pitch = Float.parseFloat(split[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }
}
