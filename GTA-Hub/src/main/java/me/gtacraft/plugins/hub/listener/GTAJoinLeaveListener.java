package me.gtacraft.plugins.hub.listener;

import me.gtacraft.plugins.hub.GTAHub;
import me.gtacraft.plugins.hub.util.GTAItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Connor on 7/8/14. Designed for the GTA-Hub project.
 */

public class GTAJoinLeaveListener implements Listener {

    public GTAJoinLeaveListener() {
        Bukkit.getPluginManager().registerEvents(this, GTAHub.get());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        giveItems(player);
        player.teleport(GTAHub.get().spawn);

        GTAHub.get().async_player_map.put(player.getName(), player);
    }

    private void giveItems(Player pl) {
        pl.getInventory().clear();

        pl.getInventory().setItem(0, GTAItems.SERVER_SELECTOR);
        pl.getInventory().setItem(1, GTAItems.HIDE_PLAYERS);
        //pl.getInventory().setItem(8, GTAItems.GROUND_SMASHER);
        pl.updateInventory();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GTAHub.get().async_player_map.remove(event.getPlayer().getName());
    }
}
