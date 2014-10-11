package me.gtacraft.plugins.donorperks.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Connor on 7/16/14. Designed for the GTA-Donorperks project.
 */

public class BoomCommand implements SubCommand {

    public void run(Player player, String[] args) {
        if (!(player.hasPermission("gtadonors.boom"))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ));
        }
    }
}
