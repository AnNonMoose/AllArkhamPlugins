package org.arkham.cs.cosmetics;

import java.util.ArrayList;
import java.util.List;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.handler.PlayerHandler;
import org.arkham.cs.utils.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Hero - Rename / Color of armor and blocks SuperHero Rename / Color weapons,
 * armor, blocks, tools /lore /itemcolor
 * 
 * atauthor calebbfmv Aug 13, 2014
 * 
 */
public class ItemColoring {

	private static ArrayList<Material> heroItems = new ArrayList<>();
	private static ArrayList<Material> superHeroItems = new ArrayList<>();

	static {
		for (Material mat : Material.values()) {
			if (mat.name().toLowerCase().contains("_helmet") || mat.name().toLowerCase().contains("_chestplate") || mat.name().toLowerCase().contains("_leggings")
					|| mat.name().toLowerCase().contains("_boots")) {
				heroItems.add(mat);
			}
			String name = mat.name().toLowerCase();
			if (name.contains("_pickaxe") || name.contains("_axe") || name.contains("_spade") || name.contains("_hoe") || name.contains("_sword")) {
				superHeroItems.add(mat);
				continue;
			}
			heroItems.add(mat);
		}
		superHeroItems.addAll(heroItems);
	}

	public static void rename(Player player, String name) {
		if (name == null || name.equalsIgnoreCase("")) {
			return;
		}
		ItemStack item = player.getItemInHand();
		if (PlayerHandler.isNothingSpecial(player)) {
			CosmeticSuite cs = CosmeticSuite.getInstance();
			FileConfiguration config = cs.getConfig();
			String link = config.getString("buy-link", CosmeticSuite.PREFIX + "Purchase this rank at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org");
			link = ChatColor.translateAlternateColorCodes('&', link);
			player.sendMessage(link);
			return;
		}
		if (item == null || item.getType() == Material.AIR) {
			player.sendMessage(CosmeticSuite.PREFIX + "Pleas have the item, that you wish to change, in your hand.");
			return;
		}
		if (name.length() > 64) {
			player.sendMessage(CosmeticSuite.PREFIX + "The given name was longer than the max allowed (Your Length: " + name.length() + " Max Allowed: 64");
			return;
		}
		Rank rank = PlayerHandler.getRank(player);
		if (rank == Rank.HERO) {
			if (!heroItems.contains(item.getType())) {
				player.sendMessage(CosmeticSuite.PREFIX + "You cannot edit this item!");
				return;
			}
		}
		if (rank == Rank.SUPERHERO) {
			if (!superHeroItems.contains(item.getType())) {
				player.sendMessage(CosmeticSuite.PREFIX + "You cannot edit this item!");
				return;
			}
		}
		ItemMeta meta = item.getItemMeta();
		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.translateAlternateColorCodes('&', name));
		meta.setDisplayName(builder.toString());
		item.setItemMeta(meta);
		player.setItemInHand(item);
		player.sendMessage(CosmeticSuite.PREFIX + "Changed items name to " + builder.toString());
	}

	public static void lore(Player player, String loreLine) {
		nameAndLore(player, null, loreLine);
	}

	public static void nameAndLore(Player player, String name, String loreLine) {
		rename(player, name);
		ItemStack item = player.getItemInHand();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		if (PlayerHandler.isNothingSpecial(player)) {
			CosmeticSuite cs = CosmeticSuite.getInstance();
			FileConfiguration config = cs.getConfig();
			String link = config.getString("buy-link", CosmeticSuite.PREFIX + "Purchase this rank at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org");
			link = ChatColor.translateAlternateColorCodes('&', link);
			player.sendMessage(link);
			return;
		}
		if (item == null || item.getType() == Material.AIR) {
			player.sendMessage(CosmeticSuite.PREFIX + "Pleas have the item, that you wish to change, in your hand.");
			return;
		}
		if (loreLine.length() > 48) {
			player.sendMessage(CosmeticSuite.PREFIX + "The given lore was longer than the max allowed (Your Length: " + name.length() + " Max Allowed: 48");
			return;
		}
		Rank rank = PlayerHandler.getRank(player);
		if (rank == Rank.HERO) {
			if (!heroItems.contains(item.getType())) {
				player.sendMessage(CosmeticSuite.PREFIX + "You cannot edit this item!");
				return;
			}
		}
		if (rank == Rank.SUPERHERO) {
			if (!superHeroItems.contains(item.getType())) {
				player.sendMessage(CosmeticSuite.PREFIX + "You cannot edit this item!");
				return;
			}
		}
		String color = (loreLine.contains("&") ? ChatColor.translateAlternateColorCodes('&', loreLine) : ChatColor.WHITE.toString() + loreLine);
		loreLine = ChatColor.AQUA + player.getName() + " : " + color;
		lore.add(loreLine);
		meta.setLore(lore);
		item.setItemMeta(meta);
		player.setItemInHand(item);
		player.sendMessage(CosmeticSuite.PREFIX + "Set the items lore to " + loreLine);
	}
	
	public static void color(Player player, DyeColor dyeColor){
		Color color = dyeColor.getColor();
		ItemStack item = player.getItemInHand();
		if (PlayerHandler.isNothingSpecial(player)) {
			CosmeticSuite cs = CosmeticSuite.getInstance();
			FileConfiguration config = cs.getConfig();
			String link = config.getString("buy-link", CosmeticSuite.PREFIX + "Purchase this rank at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org");
			link = ChatColor.translateAlternateColorCodes('&', link);
			player.sendMessage(link);
			return;
		}
		if (item == null || item.getType() == Material.AIR) {
			player.sendMessage(CosmeticSuite.PREFIX + "Pleas have the item, that you wish to change, in your hand.");
			return;
		}
		Rank rank = PlayerHandler.getRank(player);
		if (rank == Rank.HERO) {
			if (!heroItems.contains(item.getType())) {
				player.sendMessage(CosmeticSuite.PREFIX + "You cannot edit this item!");
				return;
			}
		}
		if (rank == Rank.SUPERHERO) {
			if (!superHeroItems.contains(item.getType())) {
				player.sendMessage(CosmeticSuite.PREFIX + "You cannot edit this item!");
				return;
			}
		}
		ItemMeta meta = item.getItemMeta();
		try {
			LeatherArmorMeta lmeta = (LeatherArmorMeta) meta;
			lmeta.setColor(color);
			item.setItemMeta(lmeta);
			player.setItemInHand(item);
			player.sendMessage(CosmeticSuite.PREFIX + "Changed the items color to " + ChatColor.UNDERLINE + dyeColor.name());
		} catch (ClassCastException e){
			player.sendMessage(CosmeticSuite.PREFIX + "Make sure the item in your hand is leather armor!");
		}
	}
}