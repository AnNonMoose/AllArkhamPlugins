package me.vaqxine.BuycraftAmplification.listeners;

import java.util.UUID;

import me.vaqxine.BuycraftAmplification.BuycraftAmplification;
import me.vaqxine.BuycraftAmplification.libs.UUIDLibrary;
import me.vaqxine.BuycraftAmplification.tasks.PendingCommandTask;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e){
        // Run any queued commands.
        final Player pl = e.getPlayer();
        
       Bukkit.getScheduler().runTaskLaterAsynchronously(BuycraftAmplification.getPlugin(), new Runnable(){
          public void run(){
                UUID uuid = BuycraftAmplification.spoof_uuid ? UUIDLibrary.getSpoofedUUIDFromName(pl.getName()) : pl.getUniqueId();
                PendingCommandTask.runLoginCommands(uuid, pl.getName());
         }
       }, 10L);

    }
}
