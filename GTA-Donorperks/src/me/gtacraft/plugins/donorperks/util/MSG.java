package me.gtacraft.plugins.donorperks.util;

import me.gtacraft.plugins.donorperks.GTADonorperks;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Connor on 7/16/14. Designed for the GTA-Donorperks project.
 */

public class MSG {

    public static String f(String previous) {
        return ChatColor.translateAlternateColorCodes('&', previous);
    }

    public static void config(Player player, String section) {
        FileConfiguration fc = GTADonorperks.getInstance().getConfig();
        Object msg = fc.get(section);
        if (msg instanceof List) {
            List<String> send = (List<String>)msg;
            for (String s : send)
                player.sendMessage(f(s));
        } else {
            String s = (String)msg;
            player.sendMessage(f(s));
        }
    }
}
