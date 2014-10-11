package org.arkham.cs.events;

import org.arkham.cs.CosmeticSuite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreEvent implements Listener {
	
	public CommandPreEvent(CosmeticSuite cs){
		cs.getServer().getPluginManager().registerEvents(this, cs);
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event){
		String message = event.getMessage().replace("/", "");
		if(message.equalsIgnoreCase("hat")){
			Player player = event.getPlayer();
			CosmeticSuite.getInstance().getCommand().openHats(player);
			event.setCancelled(true);
		}
	}
}