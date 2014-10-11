/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.arkhamnetwork.playersync.listeners.PlayerListener;
import org.arkhamnetwork.playersync.managers.SQLManager;
import org.arkhamnetwork.playersync.struct.PlayerSyncUser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Essentials;

/**
 *
 * @author devan_000
 */
public class PlayerSync extends JavaPlugin {

      private static PlayerSync plugin;
      private static Essentials essentials;
      private static Economy economy;

      public static PlayerSync get() {
            return plugin;
      }

      private PluginDescriptionFile pdf = null;

      public HashMap<UUID, PlayerSyncUser> users = new HashMap<>();
      public HashSet<String> new_player_uuids = new HashSet<>();
      public HashSet<World> worlds = new HashSet<>();

      public static boolean shutting_down = false;
      
      @Override
      public void onEnable() {
            plugin = this;
            new BukkitRunnable() {
				@Override
				public void run() {		
					worlds.addAll(Bukkit.getWorlds());
				}
			}.runTaskLater(this, 20L);
            essentials = Bukkit.getServer().getPluginManager().isPluginEnabled("Essentials") ? (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials") : null;
            pdf = getDescription();
            setupEconomy();
            log("==[ Plugin version " + pdf.getVersion() + " starting ]==");
            saveDefaultConfig();
            if (getConfig().getBoolean("libraryMode")) {
                  log("Running as just a library.");
            } else {
                  log("Running in full-mode.");
                  if (!SQLManager.onEnable()) {
                        log("*** Error with SQL connection. ***");
                        getServer().getPluginManager().disablePlugin(this);
                        return;
                  }
                  log("Attempting to set all users that were on this server to 'offline' status, safeguard.");
                  Statement statement;
                  try {
                        statement = SQLManager.C.createStatement();
                        statement.execute("UPDATE player_storage SET is_online='false', online_server_address='' WHERE online_server_address='" + (plugin.getServer().getIp() + ":" + plugin.getServer().getPort()) + "'");
                        if (!statement.isClosed()) {
                              statement.close();
                        }
                  } catch (SQLException ex) {
                  }
                  registerEvents();
            }
            log("==[ Plugin version " + pdf.getVersion() + " started ]==");
      }

      @Override
      public void onDisable() {
            shutting_down = true;
            log("==[ Plugin version " + pdf.getVersion() + " shutting down ]==");

            if (!getConfig().getBoolean("libraryMode")) {
                  for (Entry<UUID, PlayerSyncUser> data : new ArrayList<>(plugin.users.entrySet())) {
                        final PlayerSyncUser psu = data.getValue();
                        final Player player = psu.getBukkitPlayer();

                        if (player != null) {
                              psu.save(player.getName(), player.getUniqueId(), player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getEnderChest().getContents(), player.getActivePotionEffects(), player.getExp(), player.getHealth(), player.getMaxHealth(), player.getFoodLevel(), player.getLocation(), false);
                              player.setMetadata("no_save", new FixedMetadataValue(plugin, true));
                        }

                        log("Saved playerdata for " + data.getKey() + "!");
                  }
            }

            log("==[ Plugin version " + pdf.getVersion() + " shutdown ]==");
      }

      private boolean setupEconomy() {
            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                  economy = economyProvider.getProvider();
            }

            return (economy != null);
      }

      public Essentials getEssentials() {
            return essentials;
      }

      public Economy getEconomy() {
            return economy;
      }

      public void log(String message) {
            getLogger().info(message);
      }

      private void registerEvents() {
            PluginManager manager = getServer().getPluginManager();
            manager.registerEvents(new PlayerListener(), this);
      }

}
