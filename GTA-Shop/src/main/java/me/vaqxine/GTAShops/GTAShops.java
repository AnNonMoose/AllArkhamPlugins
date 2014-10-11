package me.vaqxine.GTAShops;

import java.io.File;
import java.util.HashMap;

import me.vaqxine.GTAShops.listeners.ShopListener;
import me.vaqxine.GTAShops.utils.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class GTAShops extends JavaPlugin {
    
    public static HashMap<String, Inventory> shop_inventories = new HashMap<String, Inventory>();
    // List of NPC Names and inventory objects.
    
    public static File shop_config_folder = new File("plugins/GTAShops/shops/");
    public static Logger log = new Logger();
    private static GTAShops plugin;
    
    public void onEnable(){
        plugin = this;
        shop_config_folder.mkdirs();
        FileUtils.loadShopConfig();
        this.getServer().getPluginManager().registerEvents(new ShopListener(), this);
    }
    
    public void onDisable(){}
    
    public static GTAShops getPlugin(){
        return plugin;
    }

    public static boolean isShopNearby(Location location) {
        for (Entity player : location.getWorld().getEntities()) {
            if (!(player instanceof Player))
                continue;

            Player ent = (Player)player;
            String sysName = ShopListener.systemName(ent.getName());
            if (shop_inventories.containsKey(sysName)) {
                if (ent.getLocation().distance(location) < 8)
                    return true;
            }
        }
        return false;
    }
}
