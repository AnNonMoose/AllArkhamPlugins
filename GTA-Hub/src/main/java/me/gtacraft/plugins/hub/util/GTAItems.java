package me.gtacraft.plugins.hub.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Connor on 7/8/14. Designed for the GTA-Hub project.
 */

public class GTAItems {

    public static final ItemStack SERVER_SELECTOR;
    public static final ItemStack HIDE_PLAYERS;
    public static final ItemStack SHOW_PLAYERS;
    public static final ItemStack GROUND_SMASHER;

    static {
        SERVER_SELECTOR = new ItemStack(Material.COMPASS);
        ItemMeta ssMeta = SERVER_SELECTOR.getItemMeta();
        ssMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e« &f&lServer Selector &e»"));
        ssMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&a&oRight click to open the server selector menu")));
        SERVER_SELECTOR.setItemMeta(ssMeta);

        HIDE_PLAYERS = new ItemStack(Material.REDSTONE_TORCH_ON);
        ItemMeta hpMeta = HIDE_PLAYERS.getItemMeta();
        hpMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e« &c&lHide Players &e»"));
        hpMeta.setLore(Arrays.asList(ChatColor.GREEN+""+ChatColor.ITALIC+"Right click to hide online players"));
        HIDE_PLAYERS.setItemMeta(hpMeta);

        SHOW_PLAYERS = new ItemStack(Material.LEVER);
        ItemMeta spMeta = HIDE_PLAYERS.getItemMeta();
        spMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e« &a&lShow Players &e»"));
        spMeta.setLore(Arrays.asList(ChatColor.GREEN + "" + ChatColor.ITALIC + "Right click to show online players"));
        SHOW_PLAYERS.setItemMeta(spMeta);

        GROUND_SMASHER = new ItemStack(Material.IRON_AXE);
        ItemMeta gsMeta = GROUND_SMASHER.getItemMeta();
        gsMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e« &5&lGround Smasher &e»"));
        spMeta.setLore(Arrays.asList(ChatColor.GREEN+""+ ChatColor.ITALIC+"Hit the ground to teleport into another dimension!"));
        GROUND_SMASHER.setItemMeta(gsMeta);
    }
}
