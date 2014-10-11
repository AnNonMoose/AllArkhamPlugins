/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.vaqxine.NetworkManager.utils;

import java.util.concurrent.ConcurrentHashMap;

import me.vaqxine.NetworkManager.NetworkManager;
import net.minecraft.server.v1_7_R4.MinecraftServer;

import org.bukkit.entity.Player;

import com.google.gson.Gson;

/**
 *
 * @author devan_000
 */
public class StatusUtils {

      private static volatile NetworkManager plugin = NetworkManager.getPlugin();
      private static volatile Gson gson = new Gson();

      public static String getStatus() {
            ConcurrentHashMap<String, Object> json = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, Object> players = new ConcurrentHashMap<>();

            try {
                  final Player[] onlinePlayers = plugin.getServer().getOnlinePlayers();
                  for (Player playerObject : onlinePlayers) {
                        ConcurrentHashMap<String, Object> playerBuilder = new ConcurrentHashMap<>();
                        playerBuilder.put("uuid", playerObject.getUniqueId());
                        playerBuilder.put("health_level", playerObject.getHealth());
                        playerBuilder.put("food_level", playerObject.getFoodLevel());
                        playerBuilder.put("xp_level", playerObject.getLevel());
                        playerBuilder.put("air_level", playerObject.getRemainingAir());
                        playerBuilder.put("max_health", playerObject.getMaxHealth());
                        playerBuilder.put("max_air", playerObject.getMaximumAir());
                        playerBuilder.put("world", playerObject.getWorld().getName());
                        playerBuilder.put("gamemode", playerObject.getGameMode());
                        players.put(playerObject.getName(), playerBuilder);
                  }

                  json.put("players", players);
                  json.put("online", plugin.getServer().getOnlinePlayers().length);
                  json.put("max", plugin.getServer().getMaxPlayers());
                  json.put("version", plugin.getServer().getVersion());
                  json.put("tps", String.valueOf(Math.min(Math.round(MinecraftServer.getServer().recentTps[0] * 100.00) / 100.00, 20.00)));

                  return gson.toJson(json);
            } finally {
                  json = null;
                  players = null;
            }
      }

      public static String getBasicStatus() {
            ConcurrentHashMap<String, Object> json = new ConcurrentHashMap<>();

            json.put("online", plugin.getServer().getOnlinePlayers().length);
            json.put("max", plugin.getServer().getMaxPlayers());
            json.put("version", plugin.getServer().getVersion());
            json.put("tps", String.valueOf(Math.min(Math.round(MinecraftServer.getServer().recentTps[0] * 100.00) / 100.00, 20.00)));

            return gson.toJson(json);
      }

}
