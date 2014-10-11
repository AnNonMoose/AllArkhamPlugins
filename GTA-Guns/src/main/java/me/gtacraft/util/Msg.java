package me.gtacraft.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Msg 
{
	public static void sendMessage(Player player, String... messages)
	{
		for (String msg : messages)
		{
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7<< &a&oGTA-Guns &7>> &a"+msg));
		}
	}
	
	public static void sendXConsoleMessage(CommandSender sender, String... messages)
	{
		for (String msg : messages)
		{
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7<< &a&oGTA-Guns &7>> &a"+msg));
		}
	}
}
