package me.gtacraft.plugins.chestscanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Connor on 7/5/14. Designed for the GTA-Chest-Scanner project.
 */

public class ChestScanner extends JavaPlugin {

    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED+"No permission!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Must be a player!");
            return true;
        }

        Runnable async = new Runnable() {
            public void run() {
                Player player = (Player)sender;
                Location at = player.getLocation();
                int xMin = at.getBlockX()-100;
                int yMin = at.getBlockY()-100;
                int zMin = at.getBlockZ()-100;

                int xMax = at.getBlockX()+100;
                int yMax = at.getBlockY()+100;
                int zMax = at.getBlockZ()+100;

                player.sendMessage("Chests in range...");
                for (int x = xMin; x < xMax; x++) {
                    for (int y = yMin; y < yMax; y++) {
                        for (int z = zMin; z < zMax; z++) {
                            Location hit = new Location(at.getWorld(), x, y, z);
                            if (hit.getBlock().getType().equals(Material.CHEST) || hit.getBlock().getType().equals(Material.TRAPPED_CHEST)) {
                                player.sendMessage("("+hit.getBlockX()+", "+hit.getBlockY()+", "+hit.getBlockZ()+")");
                            }
                        }
                    }
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, async);

        return true;
    }
}
