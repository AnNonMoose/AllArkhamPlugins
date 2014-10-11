package me.vaqxine.GTAShops.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import me.gtacraft.api.GTAGunsAPI;
import me.gtacraft.gun.Gun;
import me.gtacraft.util.GunUtil;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
    public static ItemStack convertShopStringToItemStack(String s){
        try{
            if(s.contains("gun[")){
                // gun(mini_gun)@50
                String gun_name = s.split(Pattern.quote("gun["))[1].split(Pattern.quote("]"))[0];
                Gun g = GTAGunsAPI.getGunFromName(gun_name.replace("_", " "));
                System.out.println("gun_name = " + gun_name.replace("_", " ") + " | gun_object = " + g);
                ItemStack gun_is = GunUtil.getGunStack(g);
                ItemMeta im = gun_is.getItemMeta();
                double price = Double.parseDouble(s.split("@")[1]);
                im.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Price " + ChatColor.GRAY + "$" + price)));
                gun_is.setItemMeta(im);
                return gun_is;
            } else {
                // Line format:
                // 1xid:durability(custom_name)@400
                // 50x3:0@50 -> This would sell 50 dirt for $50.
                String custom_name = null;

                if(s.contains("[") && s.contains("]")){
                    // Custom name.
                    custom_name = s.split(Pattern.quote("["))[1].split(Pattern.quote("]"))[0];
                    s = s.replace("[" + custom_name + "]", "");
                    custom_name = ChatColor.translateAlternateColorCodes('&', custom_name.replace("_", " "));
                }

                int amount = Integer.parseInt(s.split("x")[0]);
                int id = Integer.parseInt(s.split("x")[1].split(":")[0]);
                short durability = Short.parseShort(s.split(":")[1].split("@")[0]);
                double price = Double.parseDouble(s.split("@")[1]);

                ItemStack is = new ItemStack(id, 1, durability);
                ItemMeta im = is.getItemMeta();
                if(custom_name != null){
                    im.setDisplayName(custom_name);
                }

                im.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Price " + ChatColor.GRAY + "$" + price)));
                is.setItemMeta(im);
                is.setAmount(amount);
                return is;
            }
        } catch(Exception err){
            err.printStackTrace();
            return null;
        }
    }

    public static double getPrice(ItemStack is){
        double price = 0.0D;
        if(is.hasItemMeta() && is.getItemMeta().hasLore()){
            for(String s : is.getItemMeta().getLore()){
                if(s.startsWith(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Price")){
                    // Grab that ass.
                    price = Double.parseDouble(ChatColor.stripColor(s.split(" ")[1].replace("$", "")));
                }
            }
        }

        return price;
    }

    public static ItemStack removePrice(ItemStack is){
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore()){
            List<String> new_lore = new ArrayList<String>();
            for(String s : is.getItemMeta().getLore()){
                if(s.startsWith(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Price")){
                    continue;
                }

                new_lore.add(s);
            }


            ItemMeta im = is.getItemMeta();
            im.setLore(new_lore);
            is.setItemMeta(im);
            return is;
        }
        
        return null;
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
