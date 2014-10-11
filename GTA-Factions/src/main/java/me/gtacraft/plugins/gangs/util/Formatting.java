package me.gtacraft.plugins.gangs.util;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class Formatting {

    public static String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
