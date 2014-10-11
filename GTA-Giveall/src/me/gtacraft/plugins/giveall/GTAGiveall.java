package me.gtacraft.plugins.giveall;

import me.gtacraft.api.GTAGunsAPI;
import me.gtacraft.gun.Gun;
import me.gtacraft.gun.GunData;
import me.gtacraft.player.GunHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Connor on 7/17/14. Designed for the GTA-Giveall project.
 */

public class GTAGiveall extends JavaPlugin implements Listener {

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPreProcessCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equalsIgnoreCase("/give all") || event.getMessage().equalsIgnoreCase("/give all")) {
            if (!(player.isOp())) {
                return;
            }
            event.setCancelled(true);
            ItemStack inHand = event.getPlayer().getItemInHand();
            if (inHand == null || inHand.getType().equals(Material.AIR))
                return;

            Gun gun = GTAGunsAPI.getGunFromItem(inHand);
            if (gun != null) {
                GunHolder holder = GunHolder.getHolder(player);
                if (holder.getCurrentWeapon() == null) {
                    player.sendMessage(ChatColor.RED+"Please hold the gun you wish to give to everyone!");
                    return;
                }

                player.getInventory().remove(player.getItemInHand());

                GunData give = holder.getCurrentWeapon();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.equals(player))
                        continue;

                    GTAGunsAPI.giveGun(online, give.getModel());
                    online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou were given a &e"+give.getModel().getAttribute("name").getStringValue()+"&c&l!"));
                }

                player.sendMessage(ChatColor.GREEN+"You gave "+ChatColor.YELLOW+ (Bukkit.getOnlinePlayers().length-1)+ChatColor.GREEN+" people the gun in your hand!");
                return;
            } else {
                player.getInventory().remove(player.getItemInHand());

                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.equals(player))
                        continue;

                    online.getInventory().addItem(inHand);
                    online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou were given a &e"+getItemName(inHand)+"&c&l!"));
                }

                player.sendMessage(ChatColor.GREEN+"You gave "+ChatColor.YELLOW+ (Bukkit.getOnlinePlayers().length-1)+ChatColor.GREEN+" people the item in your hand!");
                return;
            }
        }
    }

    public static String getItemName(ItemStack is){
        if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()) return is.getItemMeta().getDisplayName();
        String sys_name = is.getType().name();
        String epic_name = "";
        if(sys_name.contains("_")){
            for(String word : sys_name.split("_")){
                epic_name = epic_name + word.substring(0,1).toUpperCase() + word.substring(1, word.length()).toLowerCase() + " ";
            }
            if(epic_name.endsWith(" ")) epic_name = epic_name.substring(0, epic_name.length() - 1);
            return epic_name;
        } else {
            return is.getType().name().substring(0, 1).toUpperCase() + is.getType().name().substring(1, is.getType().name().length()).toLowerCase();
        }
    }
}
