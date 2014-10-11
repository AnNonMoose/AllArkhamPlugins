/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.listener;

import org.arkhamnetwork.arcade.commons.kit.KitShop;
import org.arkhamnetwork.arcade.commons.userstorage.PlayerProfile;
import org.arkhamnetwork.arcade.commons.userstorage.PlayerRank;
import org.arkhamnetwork.arcade.commons.utils.MessageUtils;
import org.arkhamnetwork.arcade.commons.utils.PlayerUtils;
import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;
import org.arkhamnetwork.arcade.minigame.skywars.manager.SkywarsPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author devan_000
 */
public class SkywarsPlayerListener implements Listener {

    private SkyWars plugin = SkyWars.getSkywars();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        SkywarsPlayerManager.handleLogin(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        SkywarsPlayerManager.handleJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        SkywarsPlayerManager.handleLeave(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);

        SkywarsPlayerManager.handleLeave(event.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        PlayerProfile profile = plugin.getPlayers()
                .get(event.getPlayer().getUniqueId()).getUserProfile();

        if (profile != null) {
            String format = plugin.getChatFormat();
            format = format.replace("NAME", event.getPlayer().getName());

            if (profile.getPlayerRanks().isEmpty()) {
                format = format.replace("PREFIX", "");
            } else {
                StringBuilder rankBuilder = new StringBuilder();
                for (PlayerRank rank : profile.getPlayerRanks()) {
                    rankBuilder.append(rank.getPrefix());
                }
                format = format.replace("PREFIX", rankBuilder.toString());
            }

            if (profile.getPlayerRanks().isEmpty()) {
                format = format.replace("CHATCOLOR",
                        String.valueOf(ChatColor.WHITE));
            } else {
                format = format.replace(
                        "CHATCOLOR",
                        String.valueOf(profile.getPlayerRanks().get(0)
                                .getChatColor()));
            }

            format = format.replace("MESSAGE", event.getMessage());
            event.setFormat(MessageUtils.translateToColorCode(format));
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                    ChatColor.GREEN + "[Arcade] " + ChatColor.GRAY
                    + "Your profile is still loading.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFill(PlayerBucketFillEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEmpty(PlayerBucketEmptyEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDrop(PlayerDropItemEvent event) {
        if (plugin.getCurrentStage() == null
                || plugin.getCurrentStage().getId() != 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (plugin.getCurrentStage() == null || plugin.getCurrentStage().getId() != 0) {
            return;
        }
        
        if (event.getRightClicked() == null || event.getRightClicked().getType() != EntityType.VILLAGER) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!plugin.getKitShops().containsKey(event.getRightClicked().getUniqueId())) {
            return;
        }
        
        KitShop shop = plugin.getKitShops().get(event.getRightClicked().getUniqueId());
        
        SkywarsPlayerManager.attemptSelectKit(shop.getKit(), plugin.getPlayers().get(event.getPlayer().getUniqueId()), false);
    }
}
