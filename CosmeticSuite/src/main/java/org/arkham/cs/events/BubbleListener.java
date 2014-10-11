package org.arkham.cs.events;

import java.util.ArrayList;
import java.util.List;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.Hologram;
import org.arkham.cs.handler.PlayerHandler;
import org.arkham.cs.utils.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class BubbleListener implements Listener {

	public BubbleListener() {
		Bukkit.getPluginManager().registerEvents(this, CosmeticSuite.getInstance());
	}

	public static String[] convertStringToBubbleChat(String msg, Player player) {
		List<String> lmsg = new ArrayList<>();
		Rank rank = PlayerHandler.getRank(player);
		ChatColor color = ChatColor.WHITE;
		if (rank == Rank.HERO) {
			color = ChatColor.AQUA;
		}
		
		if (rank == Rank.SUPERHERO) {
			color = ChatColor.DARK_RED;
		}
		lmsg.add(color + player.getName());
		ChatColor pColor = CosmeticSuite.getInstance().getChatColorManager().getColor(player);
		msg = pColor + msg;
		if (msg.length() <= 33) {
			lmsg.add(msg);
			return lmsg.toArray(new String[lmsg.size()]);
		}
		String word_batch = "";
		for (String word : msg.split(" ")) {
			if (word.length() <= 0 || word.length() > 16)
				continue;
			if (word_batch.length() >= 33) {
				if (word_batch.endsWith(" "))
					word_batch = word_batch.substring(0, word_batch.length() - 1);
				lmsg.add(pColor + word_batch);
				word_batch = word + " ";
				continue;
			}
			word_batch += word + " ";
		}
		if (word_batch.length() > 0) {
			lmsg.add(pColor + word_batch);
		}
		return lmsg.toArray(new String[lmsg.size()]);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onChat(AsyncPlayerChatEvent event) {
		Player pl = event.getPlayer();

		
		String msg = event.getMessage();
		CosmeticSuite.getInstance().getChatColorManager().sync(pl);
		ChatColor pColor = CosmeticSuite.getInstance().getChatColorManager().getColor(pl);
		event.setMessage(pColor + msg);
		event.setFormat(event.getFormat());
		 
        if(!PlayerHandler.isSuperHero(pl)){
            // No bubles.
            return;
        }
		
		final Location loc = pl.getLocation();
		final Player fpl = pl;
		final String fmsg = msg;
		final String[] bubble_msg = convertStringToBubbleChat(fmsg, pl);
		final double y_boost = bubble_msg.length * 0.20D;
		final int length = fmsg.length();
		if (pl.hasMetadata("hologram")) {
			CosmeticSuite.getInstance().getServer().getScheduler().runTask(CosmeticSuite.getInstance(), new Runnable() {
				@Override
				public void run() {
					try {
						Hologram h = (Hologram) fpl.getMetadata("hologram").get(0).value();
						h.destroy();
						fpl.removeMetadata("hologram", CosmeticSuite.getInstance());
					} catch (ClassCastException e) {
						fpl.removeMetadata("hologram", CosmeticSuite.getInstance());
					}
				}
			});
		}
		CosmeticSuite.getInstance().getServer().getScheduler().runTaskAsynchronously(CosmeticSuite.getInstance(), new Runnable() {
			@Override
			public void run() {
				new Hologram(CosmeticSuite.getInstance(), fpl, bubble_msg).show(loc.add(0, (0.8 + y_boost), 0), ((4 * 20L) + length));
			}
		});
	}
}
