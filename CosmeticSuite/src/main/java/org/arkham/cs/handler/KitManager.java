package org.arkham.cs.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.GlobalKit;
import org.arkham.cs.cosmetics.HeroKit;
import org.arkham.cs.db.SQLConnectionThread;
import org.arkham.cs.db.SQLQueryThread;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class KitManager {

	public static void giveKit(Player player, GlobalKit kit) {
		if (!canUse(player, kit)) {
			return;
		}
		kit.giveItems(player);
		long current = System.currentTimeMillis() / 1000;
		int id = (kit instanceof HeroKit) ? 1 : 2;
		player.sendMessage(CosmeticSuite.PREFIX + "You have redeemed the " + (id == 1 ? "Hero": "SuperHero") + " Kit! You'll have to wait another 24 hours before using it again!");
		SQLQueryThread.addQuery("INSERT INTO `globalkits` VALUES('" + player.getUniqueId().toString() + "', " + id + ", " + current + ") ");
	}

	public static boolean canUse(Player player, GlobalKit kit) {
		int id = (kit instanceof HeroKit) ? 1 : 2;
		if(PlayerHandler.isNothingSpecial(player)){
			CosmeticSuite cs = CosmeticSuite.getInstance();
			FileConfiguration config = cs.getConfig();
			String link = config.getString("buy-link", CosmeticSuite.PREFIX + "Purchase this Kit at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org");
			link = ChatColor.translateAlternateColorCodes('&', link);
			player.sendMessage(link);
			return false;
		}
		String uuid = "'" + player.getUniqueId() + "'";
		String query = "SELECT `time` FROM `globalkits` WHERE `player`=" + uuid + " AND `id`=" + id;
		ResultSet res = SQLConnectionThread.getResultSet(query);
		try {
			if (res.next()) {
				String msg = getTimeRemaining(player, kit);
				if (msg.equalsIgnoreCase("hecanuse")) {
					SQLQueryThread.addQuery("DELETE FROM `globalkits` WHERE `player` =" + uuid + " AND `id`=" + id);
					return true;
				}
				player.sendMessage(msg);
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getTimeRemaining(Player player, GlobalKit kit) {
		int id = (kit instanceof HeroKit) ? 1 : 2;
		long time = -69;
		StringBuilder builder = new StringBuilder();
		String uuid = "'" + player.getUniqueId() + "'";
		String query = "SELECT `time` FROM `globalkits` WHERE `player`=" + uuid + " AND `id`=" + id;
		ResultSet res = SQLConnectionThread.getResultSet(query);
		try {
			if (res.next()) {
				time = res.getLong("time");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int minutes = 0;
		int seconds = 0;
		int hours = 0;
		long t = (TimeUnit.HOURS.toSeconds(24)) - ((System.currentTimeMillis() / 1000) - time);
		if (t <= 0) {
			builder.append("HeCanUse");
			return builder.toString();
		}
		builder.append(CosmeticSuite.PREFIX + "Please wait another ");
		seconds = (int) (t % 60);
		minutes = (int) ((t % 3600) / 60);
		hours = (int) (t / 3600);
		if (hours > 0) {
			builder.append(hours + "h, ");
		}
		if (minutes > 0) {
			builder.append(minutes + "m, ");
		}
		if (seconds > 0) {
			builder.append(seconds + "s");
		}
		builder.append(ChatColor.YELLOW + " before using this kit again");
		return builder.toString();
	}
}