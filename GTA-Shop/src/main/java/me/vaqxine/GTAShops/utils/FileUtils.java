package me.vaqxine.GTAShops.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.vaqxine.GTAShops.GTAShops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FileUtils {
    public static void loadShopConfig(){
        
        int count = 0;
        for(File f : GTAShops.shop_config_folder.listFiles()){
            if(f.getName().endsWith(".shop")){
                String npc_name = ChatColor.stripColor(f.getName().replace(".shop", ""));
                
                // Construct inventory object based on config.
                // Line format:
                    // 1xid:durability@400
                    // 50x3:0@50 -> This would sell 50 dirt for $50.
 
                List<ItemStack> shop_stock = new ArrayList<ItemStack>();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        if (line.length() > 0 && !line.startsWith("#")) {
                            ItemStack is = ItemUtils.convertShopStringToItemStack(line);
                            if(is != null) shop_stock.add(is);
                        }
                    }
                    reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                
                int size = 9;
                while(size < shop_stock.size() && size < 54){
                    size+=9;
                }
                
                if(shop_stock.size() > size){
                    // Too many items.
                    shop_stock = shop_stock.subList(0, 53);
                    GTAShops.log.error("Had to trim shop inventory of " + npc_name + "!", FileUtils.class);
                }
                
                Inventory inv = Bukkit.createInventory(null, size, npc_name.replace("_", " "));
                inv.setContents(shop_stock.toArray(new ItemStack[shop_stock.size()]));
                GTAShops.shop_inventories.put(npc_name, inv);
                
                GTAShops.getPlugin().log.debug("Loaded " + f.getName() + " shop template!", FileUtils.class);
                count++;
            }
        }
        
        GTAShops.getPlugin().log.debug("Loaded " + count + " total shop templates!", FileUtils.class);
    }
}
