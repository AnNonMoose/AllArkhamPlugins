package org.arkham.cs.commands;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.ItemColoring;
import org.arkham.cs.handler.PlayerHandler;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ItemCommand implements CommandExecutor {

	public ItemCommand(CosmeticSuite cs) {
		cs.getCommand("itemedit").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (PlayerHandler.isNothingSpecial(player)) {
			CosmeticSuite cs = CosmeticSuite.getInstance();
			FileConfiguration config = cs.getConfig();
			String link = config.getString("buy-link", CosmeticSuite.PREFIX + "Purchase this rank at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org");
			link = ChatColor.translateAlternateColorCodes('&', link);
			player.sendMessage(link);
			player.closeInventory();
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(help());
			player.closeInventory();
			return true;
		}
		String option = args[0].toLowerCase();
		StringBuilder builder = new StringBuilder();
		if (args[1].contains("_")) {
			for (String s : args[1].split("_")) {
				builder.append(s + " ");
			}
		}
		if (args.length > 2) {
			for (int i = 1; i < args.length; i++) {
				String s = args[i];
				builder.append(s + " ");
			}
		}
		switch (option) {
		case "name":
			ItemColoring.rename(player, builder.toString());
			break;
		case "lore":
			ItemColoring.lore(player, builder.toString());
			break;
		case "color":
			DyeColor color = DyeColor.valueOf(args[1].toUpperCase());
			ItemColoring.color(player, color);
			break;
		}
		return false;
	}

	public String[] help() {
		String prefix = CosmeticSuite.PREFIX;
		String[] help = { ChatColor.YELLOW + "====================" + prefix + "==========================", prefix + "/itemedit - Shows the help menu.", prefix + "Arguments: ",
				"  " + ChatColor.RED + "name <ItemName> | Name the item in your hand! \n" + ChatColor.GRAY + "(Supports ChatColors, Max length: 64)",
				"  " + ChatColor.RED + "lore <lore> | Set the item in your hands lore! \n" + ChatColor.GRAY + "(Supports ChatColors, Max length: 48)",
				"  " + ChatColor.RED + "color <ItemColor> | Recolor the item in your hand! \n" + ChatColor.GRAY + "(Only works with Leather Armor)",
				ChatColor.YELLOW + "====================" + prefix + "==========================", };
		return help;
	}

}
