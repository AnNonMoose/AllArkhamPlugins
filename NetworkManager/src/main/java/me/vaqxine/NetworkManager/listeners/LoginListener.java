package me.vaqxine.NetworkManager.listeners;

import me.vaqxine.NetworkManager.NetworkManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginListener implements Listener {

      private NetworkManager plugin;
      
      public LoginListener(NetworkManager nm) {
            plugin = nm;
      }

      @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
      public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
            if (plugin.rebooting) {
                  e.setLoginResult(Result.KICK_OTHER);
                  e.setKickMessage(NetworkManager.restartMessage);
            }
      }
      
      @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
      public void onPlayerJoin(PlayerJoinEvent e){
          NetworkManager.async_player_map.put(e.getPlayer().getUniqueId().toString(), e.getPlayer());
      }
      
      @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
      public void onPlayerQuit(PlayerQuitEvent e){
          NetworkManager.async_player_map.remove(e.getPlayer().getUniqueId().toString());
      }
}
