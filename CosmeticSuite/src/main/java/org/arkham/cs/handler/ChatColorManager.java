package org.arkham.cs.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.db.SQLConnectionThread;
import org.arkham.cs.db.SQLQueryThread;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatColorManager {

	private static HashMap<UUID, ChatColor> colors = new HashMap<>();

	public ChatColorManager(){

	}

	public void sync(final Player player){
		if(colors.get(player.getUniqueId()) != null){
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				String uuid = "'" + player.getUniqueId() + "'";
				String resultSet = "SELECT `code` FROM `colors` WHERE `player`=" + uuid;
				ResultSet res = SQLConnectionThread.getResultSet(resultSet);
				try {
					if(res.next()){
						String colorCode = res.getString("code");
						ChatColor color = ChatColor.getByChar(colorCode);
						setColor(player, color);
						res.close();
					} else {
						setColor(player, ChatColor.WHITE);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(CosmeticSuite.getInstance());
	}

	public void setColor(Player player, ChatColor color){
		colors.put(player.getUniqueId(), color);
		SQLQueryThread.addQuery("INSERT INTO `colors` VALUES('" + player.getUniqueId().toString() + "', '" + color.getChar() + "') ON DUPLICATE KEY UPDATE `code`='" + color.getChar() + "'");
	}

	public boolean hasColor(Player player){
		return colors.get(player.getUniqueId()) != null;
	}

	public ChatColor getColor(Player player){
		ChatColor color = colors.get(player.getUniqueId());
		if(color == null){
			sync(player);
		}
		return color == null ? ChatColor.GRAY : color;
	}

}
