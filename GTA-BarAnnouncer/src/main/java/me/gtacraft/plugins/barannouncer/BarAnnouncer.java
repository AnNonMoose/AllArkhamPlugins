package me.gtacraft.plugins.barannouncer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 7/9/14. Designed for the GTA-BarAnnouncer project.
 */

public class BarAnnouncer extends JavaPlugin implements Listener {

    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);

        final List<String> announce = getConfig().getStringList("announcements");

        Runnable tick = new Runnable() {
            int pos = 0;
            public void run() {
                if (++pos >= announce.size())
                    pos = 0;

                String s = ChatColor.translateAlternateColorCodes('&', announce.get(pos));

                float percent = ((float)pos/(float)announce.size());

                for (Player p : async_player_map.values()) {
                    StatusBarAPI.setStatusBar(p, s.replace("{PLAYER}", p.getName()), percent);
                }

            }
        };
        int delay = getConfig().getInt("delay");
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, tick, delay, delay);
    }

    public void onDisable() {
        saveDefaultConfig();
    }

    public static volatile HashMap<String, Player> async_player_map = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        async_player_map.put(event.getPlayer().getName(), event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws Exception {
        async_player_map.remove(event.getPlayer().getName());
        StatusBarAPI.removeStatusBar(event.getPlayer());
    }
}
