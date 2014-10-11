package org.arkham.cs.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFactory {
		
	public static ItemStack create(Material mat, String displayName, int amount, byte data,  String... lore){
		ItemStack item =  new ItemStack(mat, amount, data);
		ItemMeta meta = item.getItemMeta();
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		meta.setDisplayName(displayName);
		List<String> lore1 = new ArrayList<>();
		if(lore[0].equalsIgnoreCase("noLore")){
			item.setItemMeta(meta);
			return item;
		}
		for(String s : lore){
			lore1.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		meta.setLore(lore1);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack create(Material mat, String displayName){
		return create(mat, displayName, 1, (byte) 0, "noLore");
	}
	
	public static ItemStack create(Material mat, String displayName, String... lore){
		return create(mat, displayName, 1, (byte) 0, lore);
	}
	
	public static ItemStack create(Material mat, String displayName, List<String> lore){
		return create(mat, displayName, 1, (byte) 0, (String[]) lore.toArray()); 
	}
	
	public static String getBaseName(Material item){
		String displayName = item.name();
		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.GOLD + ChatColor.BOLD.toString());
		if(displayName.contains("_")){
			String[] str = displayName.split("_");
			for(int i = 0; i < str.length; i++){
				String name = str[i];
				builder.append(name.substring(0, 1).toUpperCase());
				builder.append(name.substring(1).toLowerCase());
				builder.append(" ");
			}
		} else {
			builder.append(displayName.substring(0, 1).toUpperCase());
			builder.append(displayName.substring(1).toLowerCase());
		}
		return builder.toString();
	}
	
	
}