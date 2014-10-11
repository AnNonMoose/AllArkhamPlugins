/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync.struct;

import com.arkhamnetwork.listeners.LogoutListener;
import com.earth2me.essentials.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.Getter;
import net.ess3.api.MaxMoneyException;
import org.apache.commons.lang.SerializationException;
import org.arkhamnetwork.playersync.PlayerSync;
import org.arkhamnetwork.playersync.managers.SQLManager;
import org.arkhamnetwork.playersync.utils.CompressionUtils;
import org.arkhamnetwork.playersync.utils.FileUtils;
import org.arkhamnetwork.playersync.utils.InventoryUtils;
import org.arkhamnetwork.playersync.utils.LocationUtils;
import org.arkhamnetwork.playersync.utils.LogicUtils;
import org.arkhamnetwork.playersync.utils.PlayerUtils;
import org.arkhamnetwork.playersync.utils.PotionEffectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.json.simple.parser.ParseException;

/**
 *
 * @author devan_000
 */
public final class PlayerSyncUser {

      private final PlayerSync plugin = PlayerSync.get();

      @Getter
      private boolean grabbing = true;
      @Getter
      private boolean saving = false;
      @Getter
      private Player bukkitPlayer;
      @Getter
      private double exp = 0;
      @Getter
      private double health = 20;
      @Getter
      private double maxHealth = 20;
      @Getter
      private double foodLevel = 20;
      @Getter
      private Location location = plugin.getServer().getWorlds().get(0).getSpawnLocation();
      @Getter
      private BigDecimal balance = new BigDecimal(0);
      @Getter
      private HashMap<String, Location> homes = new HashMap<>();
      @Getter
      private HashMap<String, Long> kit_cooldowns = new HashMap<>();

      private ItemStack[] cachedInventory = null;
      private ItemStack[] cachedArmor = null;
      private ItemStack[] cachedEnderchest = null;
      private PotionEffect[] cachedEffects = null;

      public PlayerSyncUser(UUID uuid, String userName) {
            try {
                  if (!SQLManager.DB.databaseContainsUUID(uuid.toString())) {
                        plugin.new_player_uuids.add(uuid.toString());
                        SQLManager.DB.addUserToDatabase(uuid.toString());
                  }

                  downloadDataAsync(uuid, userName);
            } catch (SQLException | SerializationException | ParseException ex) {
            }

      }

      public void downloadDataAsync(final UUID uuid, final String userName) throws SerializationException, ParseException {
            try {
                  grabbing = true;

                  //Set there data as online
                  SQLManager.DB.saveString("is_online", uuid.toString(), "true");
                  SQLManager.DB.saveString("online_server_address", uuid.toString(), (plugin.getServer().getIp() + ":" + plugin.getServer().getPort()));

                  //Inventory
                  cachedInventory = InventoryUtils.inventoryFromBytes(SQLManager.DB.getBytes("player_inventory", uuid.toString()));

                  //Armor
                  cachedArmor = InventoryUtils.inventoryFromBytes(SQLManager.DB.getBytes("player_armor", uuid.toString()));

                  //Enderchest
                  cachedEnderchest = InventoryUtils.inventoryFromBytes(SQLManager.DB.getBytes("player_enderchest", uuid.toString()));

                  //Effects
                  cachedEffects = PotionEffectUtils.effectsFromBytes(SQLManager.DB.getBytes("player_potioneffects", uuid.toString()));

                  //Exp Level
                  exp = ((float) SQLManager.DB.getDouble("player_explevel", uuid.toString()));

                  //Health
                  health = (SQLManager.DB.getDouble("player_health", uuid.toString()));

                  //Max Health
                  maxHealth = (SQLManager.DB.getDouble("player_maxhealth", uuid.toString()));

                  //Food Level
                  foodLevel = ((int) SQLManager.DB.getDouble("player_food", uuid.toString()));

                  //Location (thx harry /s/s/s)
                  location = LocationUtils.convertStringToLocation(SQLManager.DB.getString("player_location", uuid.toString()), true);

                  // Get vault-supported balance.
                  balance = new BigDecimal(SQLManager.DB.getDouble("player_balance", uuid.toString()));

                  // Get a map of "home name" and their matching location.
                  homes = LocationUtils.convertStringToHomeMap(SQLManager.DB.getString("player_homes", uuid.toString()));

                  // Get all essential's kit cooldowns
                  kit_cooldowns = LogicUtils.getKitCooldownsFromString(SQLManager.DB.getString("player_kit_cooldowns", uuid.toString()));

                  //If they are not first time joining we create a player dat to prevent essentials from flipping.
                  if (!plugin.new_player_uuids.contains(uuid.toString())) {
                        try {
                              File destination = new File(plugin.getServer().getWorlds().get(0).getName() + "/playerdata/" + uuid + ".dat");

                              if (destination.exists()) {
                                    destination.delete();
                              }

                              destination.createNewFile();

                              InputStream in = plugin.getResource("template.dat");
                              try (OutputStream out = new FileOutputStream(destination)) {
                                    byte[] buffer = new byte[1024];
                                    int bytesRead;
                                    //read from is to buffer
                                    while ((bytesRead = in.read(buffer)) != -1) {
                                          out.write(buffer, 0, bytesRead);
                                    }
                                    out.flush();
                              }

                        } catch (IOException ex) {
                        }
                  }

                  try {
                        //Handle arkhamcolorchat downloading
                        new File("plugins/ArkhamColorChat/users/" + userName + ".yml").delete();
                        FileUtils.saveBytesToFile(CompressionUtils.uncompress(SQLManager.DB.getBytes("arkhamcolorchat_datafile", uuid.toString())), new File("plugins/ArkhamColorChat/users/" + userName + ".yml"));
                  } catch (SQLException err) {
                  }

                  try {
                        //Handle marriage downloading
                        new File("plugins/Marriage/playerdata/" + userName + ".yml").delete();
                        FileUtils.saveBytesToFile(CompressionUtils.uncompress(SQLManager.DB.getBytes("marriage_datafile", uuid.toString())), new File("plugins/Marriage/playerdata/" + userName + ".yml"));
                  } catch (SQLException err) {
                  }

                  try {
                        //Handle redeemmcmmo downloading
                        new File("plugins/RedeemMCMMO/users/" + userName + ".yml").delete();
                        FileUtils.saveBytesToFile(CompressionUtils.uncompress(SQLManager.DB.getBytes("redeemmcmmo_datafile", uuid.toString())), new File("plugins/RedeemMCMMO/users/" + userName + ".yml"));
                  } catch (SQLException err) {
                  }

                  try {
                        //Handle users downloading
                        new File("plugins/MagicCrates/users/" + userName + ".yml").delete();
                        FileUtils.saveBytesToFile(CompressionUtils.uncompress(SQLManager.DB.getBytes("magiccrates_datafile", uuid.toString())), new File("plugins/MagicCrates/Users/" + userName + ".yml"));
                  } catch (SQLException err) {
                  }

            } catch (SQLException ex) {
                  ex.printStackTrace();
                  try {
                        //Set there data as offline
                        SQLManager.DB.saveString("is_online", uuid.toString(), "false");
                        SQLManager.DB.saveString("online_server_address", uuid.toString(), "");
                  } catch (SQLException ex1) {
                  }

                  grabbing = false;

                  bukkitPlayer.setMetadata("no_save", new FixedMetadataValue(plugin, true));
                  plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        public void run() {
                              bukkitPlayer.kickPlayer(ChatColor.RED + "An error was encountered while trying to load your permissions.");
                        }
                  });
                  return;
            } finally {
                  grabbing = false;
            }
      }

      public void setPlayerDataSync(Player player) {
            this.bukkitPlayer = player;

            //Remove any lingering data
            bukkitPlayer.getInventory().clear();
            bukkitPlayer.getInventory().setArmorContents(null);
            bukkitPlayer.getEnderChest().clear();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                  player.removePotionEffect(effect.getType());
            }
            bukkitPlayer.setExp(0);
            bukkitPlayer.setHealth(20);
            bukkitPlayer.setMaxHealth(20);
            bukkitPlayer.setFoodLevel(20);

            //Restore essentials user.
            try {
                  User ess_usr = plugin.getEssentials().getUser(bukkitPlayer.getUniqueId());
                  // Setup balance.
                  ess_usr.setMoney(balance);
                  // Setup homes.
                  for (Entry<String, Location> data : homes.entrySet()) {
                        ess_usr.setHome(data.getKey(), data.getValue());
                  }
                  // Setup kit cooldowns.
                  for (Entry<String, Long> data : kit_cooldowns.entrySet()) {
                        ess_usr.setKitTimestamp(data.getKey(), data.getValue());
                  }
            } catch (MaxMoneyException e) {
            }

            //Handle normal data
            if (cachedInventory != null) {
                  bukkitPlayer.getInventory().setContents(cachedInventory);
            }
            if (cachedArmor != null) {
                  bukkitPlayer.getInventory().setArmorContents(cachedArmor);
            }
            if (cachedEnderchest != null) {
                  bukkitPlayer.getEnderChest().setContents(cachedEnderchest);
            }
            if (cachedEffects != null) {
                  for (PotionEffect effect : cachedEffects) {
                        bukkitPlayer.addPotionEffect(effect);
                  }
            }
            bukkitPlayer.setExp((float) exp);
            bukkitPlayer.setHealth(health);
            bukkitPlayer.setMaxHealth(maxHealth);
            bukkitPlayer.setFoodLevel((int) foodLevel);

            //Handle world teleporting
            if (location != null && location.getWorld() != null) {
                  bukkitPlayer.teleport(location);
            } else {
                  bukkitPlayer.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
            }
      }

      public static interface Callback {

            public void done();
      }

      public void save(final String name, final UUID uuid, final ItemStack[] inventoryContents, final ItemStack[] armorContents, final ItemStack[] enderchestContents, final Collection<PotionEffect> effects, final float exp, final double health, final double maxHealth, final double food, final Location location) {
            save(name, uuid, inventoryContents, armorContents, enderchestContents, effects, exp, health, maxHealth, food, location, true, null);
      }

      public void save(final String name, final UUID uuid, final ItemStack[] inventoryContents, final ItemStack[] armorContents, final ItemStack[] enderchestContents, final Collection<PotionEffect> effects, final float exp, final double health, final double maxHealth, final double food, final Location location, final boolean GCFilesAsync) {
            save(name, uuid, inventoryContents, armorContents, enderchestContents, effects, exp, health, maxHealth, food, location, GCFilesAsync, null);
      }

      public void save(final String name, final UUID uuid, final ItemStack[] inventoryContents, final ItemStack[] armorContents, final ItemStack[] enderchestContents, final Collection<PotionEffect> effects, final float exp, final double health, final double maxHealth, final double food, final Location location, final boolean GCFilesAsync, final Callback callback) {
            if (saving || grabbing) {
                  return;
            }

            saving = true;

            if (!PlayerSync.shutting_down) {
                  plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                        public void run() {
                              saveMethod(bukkitPlayer, name, uuid, inventoryContents, armorContents, enderchestContents, effects, exp, health, maxHealth, food, location, GCFilesAsync, callback);
                        }
                  }, 20L);
            } else {
                  saveMethod(bukkitPlayer, name, uuid, inventoryContents, armorContents, enderchestContents, effects, exp, health, maxHealth, food, location, GCFilesAsync, callback);
            }

      }

      public void saveMethod(final Player player, final String name, final UUID uuid, final ItemStack[] inventoryContents, final ItemStack[] armorContents, final ItemStack[] enderchestContents, final Collection<PotionEffect> effects, final float exp, final double health, final double maxHealth, final double food, final Location location, final boolean GCFilesAsync, Callback callback) {
            try {
                  try {
                        LogoutListener.uploadEnchantTokens(player);
                  } catch (Exception ex) {
                        ex.printStackTrace();
                  }

                  //Call API save methods.
                  try {
                        com.arkhamnetwork.ArkhamBags.listeners.LogoutListener.saveAllBagsToSQL(uuid.toString(), name);
                  } catch (Exception ex) {
                        ex.printStackTrace();
                  }

                  //Inventory
                  SQLManager.DB.saveBytes("player_inventory", uuid.toString(), InventoryUtils.inventoryToBytes(inventoryContents));

                  //Armor
                  SQLManager.DB.saveBytes("player_armor", uuid.toString(), InventoryUtils.inventoryToBytes(armorContents));

                  //Enderchest
                  SQLManager.DB.saveBytes("player_enderchest", uuid.toString(), InventoryUtils.inventoryToBytes(enderchestContents));

                  //Effects
                  SQLManager.DB.saveBytes("player_potioneffects", uuid.toString(), PotionEffectUtils.effectsToBytes(effects));

                  //Exp
                  SQLManager.DB.saveDouble("player_explevel", uuid.toString(), exp);

                  //Health
                  SQLManager.DB.saveDouble("player_health", uuid.toString(), health);

                  //Max Health
                  SQLManager.DB.saveDouble("player_maxhealth", uuid.toString(), maxHealth);

                  //Food
                  SQLManager.DB.saveDouble("player_food", uuid.toString(), food);

                  //Location
                  SQLManager.DB.saveString("player_location", uuid.toString(), LocationUtils.convertLocationToString(location));

                  //Balance
                  SQLManager.DB.saveDouble("player_balance", uuid.toString(), plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(uuid)));

                  User ess_usr = plugin.getEssentials().getUser(uuid);

                  //Homes
                  try {
                        for (String h : ess_usr.getHomes()) {
                              homes.put(h, ess_usr.getHome(h));
                        }
                  } catch (Exception err) {
                        // WTF essentials nice random catches.
                  }

                  SQLManager.DB.saveString("player_homes", uuid.toString(), LocationUtils.convertHomeMapToString(homes));

                  //Kits
                  try {
                        for (String kit : plugin.getEssentials().getSettings().getKits().getKeys(false)) {
                              kit_cooldowns.put(kit, ess_usr.getKitTimestamp(kit));
                        }
                  } catch (Exception err) {
                        // WTF essentials nice random catches.
                  }

                  SQLManager.DB.saveString("player_kit_cooldowns", uuid.toString(), LogicUtils.getStringFromKitCooldowns(kit_cooldowns));

                  //Safeguard essentials.
                  plugin.getEssentials().getUser(uuid).reset();

                  try {
                        if (new File("plugins/ArkhamColorChat/users/" + name + ".yml").exists()) {
                              //Save the arkhamcolorchat file
                              SQLManager.DB.saveBytes("arkhamcolorchat_datafile", uuid.toString(), CompressionUtils.compress(Files.readAllBytes(Paths.get("plugins/ArkhamColorChat/users/" + name + ".yml"))));
                        }
                  } catch (IOException | SQLException ex) {
                  }

                  try {
                        if (new File("plugins/Marriage/playerdata/" + name + ".yml").exists()) {
                              //Save the marriage file
                              SQLManager.DB.saveBytes("marriage_datafile", uuid.toString(), CompressionUtils.compress(Files.readAllBytes(Paths.get("plugins/Marriage/playerdata/" + name + ".yml"))));
                        }
                  } catch (IOException | SQLException ex) {
                  }

                  try {
                        if (new File("plugins/RedeemMCMMO/users/" + name + ".yml").exists()) {
                              //Handle redeemmcmmo file
                              SQLManager.DB.saveBytes("redeemmcmmo_datafile", uuid.toString(), CompressionUtils.compress(Files.readAllBytes(Paths.get("plugins/RedeemMCMMO/users/" + name + ".yml"))));
                        }
                  } catch (IOException | SQLException ex) {
                  }

                  try {
                        if (new File("plugins/MagicCrates/Users/" + name + ".yml").exists()) {
                              //Handle magiccrates file
                              SQLManager.DB.saveBytes("magiccrates_datafile", uuid.toString(), CompressionUtils.compress(Files.readAllBytes(Paths.get("plugins/MagicCrates/Users/" + name + ".yml"))));
                        }
                  } catch (IOException | SQLException ex) {
                  }

                  //Set there data as offline
                  SQLManager.DB.saveString("is_online", uuid.toString(), "false");
                  SQLManager.DB.saveString("online_server_address", uuid.toString(), "");

                  plugin.users.remove(uuid);

                  if (GCFilesAsync) {
                        //Delete the old files
                        //Delay this to prevent resaving.
                        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                              public void run() {
                                    PlayerUtils.deletePlayerdataASync(uuid, name);
                              }
                        }, 10L);
                  } else {
                        PlayerUtils.deletePlayerdataASync(uuid, name);
                  }

                  if (callback != null) {
                        callback.done();
                  }

            } catch (SQLException ex) {
                  try {
                        //Set there data as offline
                        SQLManager.DB.saveString("is_online", uuid.toString(), "false");
                        SQLManager.DB.saveString("online_server_address", uuid.toString(), "");
                  } catch (SQLException ex1) {
                        saving = false;
                  }

                  saving = false;
            } finally {
                  saving = false;
            }
      }

      public void saveSimple(final UUID uuid) {
            try {
                  //Set there data as offline
                  SQLManager.DB.saveString("is_online", uuid.toString(), "false");
                  SQLManager.DB.saveString("online_server_address", uuid.toString(), "");
            } catch (SQLException ex) {
            }
      }

}
