package org.arkham.cs.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.arkham.cs.db.SQLConnectionThread;
import org.arkham.cs.db.SQLQueryThread;
import org.arkham.cs.interfaces.Button;
import org.bukkit.entity.Player;

public class PurchaseHandler {

	private static HashMap<UUID, List<Button>> purchases = new HashMap<>();

	public static void addPurchase(Player player, Button button){
		if(hasPurchased(player, button)){
			return;
		}
		String name = "'" + player.getUniqueId().toString() + "'";
		List<Button> buttons = purchases.get(player.getUniqueId());
		if(buttons == null){
			buttons = purchased(player);
		}
		buttons.add(button);
		purchases.put(player.getUniqueId(), buttons);
		String query1 = "SELECT `buttons` FROM `purchases` WHERE `player` =" + name + "";
		String query = "UPDATE `purchases` SET `buttons`='" + Button.serialze(buttons)+ "' WHERE `player`=" + name;
		ResultSet res = SQLConnectionThread.getResultSet(query1);
		try {
			if(!res.next()){
				query = "INSERT INTO `purchases` VALUES(" + name + ", '" + button.getPermission() + "')"; 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		SQLQueryThread.addQuery(query);
	}

	public static boolean hasPurchased(Player player, Button button){
		if(player.isOp()){
			return true;
		}
		if(player.hasPermission("cosmetics.*")){
			return true;
		}
		if(purchases.get(player.getUniqueId()) == null){
			return false;
		}
		return purchases.get(player.getUniqueId()).contains(button);
	}

	public static List<Button> purchased(Player player){
		List<Button> buttons = purchases.get(player.getUniqueId());
		if(buttons == null){
			buttons = new ArrayList<>();
			String uuid = "'" + player.getUniqueId().toString() + "'";
			String query = "SELECT * FROM `purchases` WHERE `player` =" + uuid + "";
			ResultSet res = SQLConnectionThread.getResultSet(query);
			try {
				if(res.next()){
					buttons.addAll(Button.deserialize(res.getString("buttons")));	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return buttons;
	}

	public static void setUpPurchases(Player player){
		List<Button> buttons = new ArrayList<>();
		String uuid = "'" + player.getUniqueId().toString() + "'";
		String query = "SELECT * FROM `purchases` WHERE `player` =" + uuid + "";
		ResultSet res = SQLConnectionThread.getResultSet(query);
		try {
			if(res.next()){
				buttons.addAll(Button.deserialize(res.getString("buttons")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		purchases.put(player.getUniqueId(), buttons);
	}
}


