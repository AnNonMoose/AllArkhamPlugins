/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync.listeners;

import java.sql.SQLException;
import java.util.UUID;
import org.arkhamnetwork.playersync.PlayerSync;
import org.arkhamnetwork.playersync.managers.SQLManager;
import org.arkhamnetwork.playersync.struct.PlayerSyncUser;
import org.arkhamnetwork.playersync.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author devan_000
 */
public class PlayerListener implements Listener {

      private final PlayerSync plugin = PlayerSync.get();

      @EventHandler(priority = EventPriority.MONITOR)
      public void onAsyncJoin(final AsyncPlayerPreLoginEvent event) {
            if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                  return;
            }

            if (event.getUniqueId() == null) {
                  return;
            }

            try {
                  String value = SQLManager.DB.getString("is_online", event.getUniqueId().toString());

                  if (value != null && value.toLowerCase().equals("true")) {
                        event.setKickMessage(ChatColor.RED + "Your playerdata is still syncing, please wait and try logging in again soon.");
                        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                        return;
                  }
            } catch (SQLException ex) {
                  ex.printStackTrace();
            }

            if (plugin.users.containsKey(event.getUniqueId())) {
                  event.setKickMessage(ChatColor.RED + "Your playerdata is still syncing, please wait.");
                  event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                  return;
            }

            PlayerUtils.deletePlayerdataASync(event.getUniqueId(), event.getName());
            
            //This will hold the join thread hostage until they have joined.
            plugin.users.put(event.getUniqueId(), new PlayerSyncUser(event.getUniqueId(), event.getName()));
      }

      //Handle cases where the user gets kicked, as say the server is full?
      @EventHandler(priority = EventPriority.MONITOR)
      public void onLogin(final PlayerLoginEvent event) {
            final Player player = event.getPlayer();
            //Return if the user was allowed to join.
            if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
                  return;
            }

            //Saveguards
            player.removeMetadata("no_save", plugin);
            if (Bukkit.getPluginManager().isPluginEnabled("BukkitPlotCommands")) {
                  player.removeMetadata("no_save", Bukkit.getPluginManager().getPlugin("BukkitPlotCommands"));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("BukkitPrisonCommands")) {
                  player.removeMetadata("no_save", Bukkit.getPluginManager().getPlugin("BukkitPrisonCommands"));
            }

            //Huh? The user was allowed to join?
            final PlayerSyncUser user = plugin.users.get(event.getPlayer().getUniqueId());
            if (user == null) {
                  return;
            }
            final UUID uuid = event.getPlayer().getUniqueId();

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                  public void run() {
                        //We need to set there online data to false.
                        user.saveSimple(uuid);
                        PlayerUtils.deletePlayerdataASync(event.getPlayer().getUniqueId(), event.getPlayer().getName());

                        //Cleanup, do it main thread to avoid concurrency.
                        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                              public void run() {
                                    plugin.users.remove(event.getPlayer().getUniqueId());
                              }
                        });
                  }
            });

            return;
      }

      @EventHandler(priority = EventPriority.MONITOR)
      public void onJoin(PlayerJoinEvent event) {
            final Player player = event.getPlayer();

            //Saveguards
            player.removeMetadata("no_save", plugin);
            if (Bukkit.getPluginManager().isPluginEnabled("BukkitPlotCommands")) {
                  player.removeMetadata("no_save", Bukkit.getPluginManager().getPlugin("BukkitPlotCommands"));
            }
            if (Bukkit.getPluginManager().isPluginEnabled("BukkitPrisonCommands")) {
                  player.removeMetadata("no_save", Bukkit.getPluginManager().getPlugin("BukkitPrisonCommands"));
            }

            PlayerSyncUser user = plugin.users.get(event.getPlayer().getUniqueId());

            if (user != null) {
                  user.setPlayerDataSync(event.getPlayer());
            }
      }

      // Dont use monitoras bags uses it.
      @EventHandler(priority = EventPriority.LOWEST)
      public void onQuit(final PlayerQuitEvent event) {
            final Player player = event.getPlayer();
            final UUID uuid = event.getPlayer().getUniqueId();
            final PlayerSyncUser user = plugin.users.get(uuid);

            if (!player.hasMetadata("no_save")) {
                  //This method will cleanup the user from the users map at the end.
                  if (player.getOpenInventory() != null) {
                        player.getOpenInventory().close();
                  }

                  if (user != null) {
                        user.save(player.getName(), player.getUniqueId(), player.getInventory().getContents().clone(), player.getInventory().getArmorContents().clone(), player.getEnderChest().getContents().clone(), player.getActivePotionEffects(), player.getExp(), player.getHealth(), player.getMaxHealth(), player.getFoodLevel(), player.getLocation().clone(), true);
                  }
            } else {
                  //Saveguards
                  player.removeMetadata("no_save", plugin);
                  if (Bukkit.getPluginManager().isPluginEnabled("BukkitPlotCommands")) {
                        player.removeMetadata("no_save", Bukkit.getPluginManager().getPlugin("BukkitPlotCommands"));
                  }
                  if (Bukkit.getPluginManager().isPluginEnabled("BukkitPrisonCommands")) {
                        player.removeMetadata("no_save", Bukkit.getPluginManager().getPlugin("BukkitPrisonCommands"));
                  }

                  if (user != null) {
                        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                              public void run() {
                                    //We need to set there online data to false.
                                    user.saveSimple(uuid);
                                    PlayerUtils.deletePlayerdataASync(event.getPlayer().getUniqueId(), event.getPlayer().getName());

                                    //Cleanup, do it main thread to avoid concurrency.
                                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                                          public void run() {
                                                plugin.users.remove(event.getPlayer().getUniqueId());
                                          }
                                    });
                              }
                        });
                  }
            }
      }
}
