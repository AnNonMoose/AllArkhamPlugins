/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import java.util.UUID;
import org.arkhamnetwork.arcade.commons.bar.FakeDragon;
import org.arkhamnetwork.arcade.commons.storage.ArcadeHashMap;
import org.arkhamnetwork.arcade.core.Arcade;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author devan_000
 */
public class BarUtils implements Listener {

    private static Arcade plugin = Arcade.getInstance();

    private static final ArcadeHashMap<UUID, FakeDragon> players = new ArcadeHashMap<>();
    private static final ArcadeHashMap<UUID, Integer> timers = new ArcadeHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerLoggout(PlayerQuitEvent event) {
        quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        quit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        handleTeleport(event.getPlayer(), event.getTo().clone());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerRespawnEvent event) {
        handleTeleport(event.getPlayer(), event.getRespawnLocation().clone());
    }

    private void handleTeleport(final Player player, final Location loc) {
        if (!hasBar(player)) {
            return;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin,
                new Runnable() {
                    @Override
                    public void run() {
						// Check if the player still has a dragon after the two
                        // ticks! ;)
                        if (!hasBar(player)) {
                            return;
                        }

                        FakeDragon oldDragon = getDragon(player, "");

                        float health = oldDragon.health;
                        String message = oldDragon.name;

                        NMSUtil.sendNMSPacket(player, getDragon(player, "")
                                .getDestroyPacket());

                        players.remove(player.getUniqueId());

                        FakeDragon dragon = addDragon(player, loc, message);
                        dragon.health = health;

                        sendDragon(dragon, player);
                    }

                }, 2L);
    }

    private void quit(Player player) {
        removeBar(player);
    }

    public static void setMessage(String message) {
        final Player[] players = Arcade.getInstance().getServer()
                .getOnlinePlayers();
        for (Player player : players) {
            setMessage(player, message);
        }
    }

    public static void setMessage(Player player, String message) {
        FakeDragon dragon = getDragon(player, message);

        dragon.name = cleanMessage(message);
        dragon.health = FakeDragon.MAX_HEALTH;

        cancelTimer(player);

        sendDragon(dragon, player);
    }

    public static void setMessage(String message, float percent) {
        final Player[] players = Arcade.getInstance().getServer()
                .getOnlinePlayers();
        for (Player player : players) {
            setMessage(player, message, percent);
        }
    }

    public static void setMessage(Player player, String message, float percent) {
        FakeDragon dragon = getDragon(player, message);

        dragon.name = cleanMessage(message);
        dragon.health = (percent / 100f) * FakeDragon.MAX_HEALTH;

        cancelTimer(player);

        sendDragon(dragon, player);
    }

    public static void setMessage(String message, int seconds) {
        final Player[] players = Arcade.getInstance().getServer()
                .getOnlinePlayers();
        for (Player player : players) {
            setMessage(player, message, seconds);
        }
    }

    public static void setMessage(final Player player, String message,
            int seconds) {
        FakeDragon dragon = getDragon(player, message);

        dragon.name = cleanMessage(message);
        dragon.health = FakeDragon.MAX_HEALTH;

        final float dragonHealthMinus = FakeDragon.MAX_HEALTH / seconds;

        cancelTimer(player);

        timers.put(player.getUniqueId(), Bukkit.getScheduler()
                .runTaskTimerAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        FakeDragon drag = getDragon(player, "");
                        drag.health -= dragonHealthMinus;

                        if (drag.health <= 1) {
                            removeBar(player);
                            cancelTimer(player);
                        } else {
                            sendDragon(drag, player);
                        }
                    }

                }, 20L, 20L).getTaskId());

        sendDragon(dragon, player);
    }

    public static boolean hasBar(Player player) {
        return players.get(player.getUniqueId()) != null;
    }

    public static void removeBar(Player player) {
        if (!hasBar(player)) {
            return;
        }

        NMSUtil.sendNMSPacket(player, getDragon(player, "").getDestroyPacket());

        players.remove(player.getUniqueId());

        cancelTimer(player);
    }

    public static void setHealth(Player player, float percent) {
        if (!hasBar(player)) {
            return;
        }

        FakeDragon dragon = getDragon(player, "");
        dragon.health = (percent / 100f) * FakeDragon.MAX_HEALTH;

        cancelTimer(player);

        if (percent == 0) {
            removeBar(player);
        } else {
            sendDragon(dragon, player);
        }
    }

    public static float getHealth(Player player) {
        if (!hasBar(player)) {
            return -1;
        }

        return getDragon(player, "").health;
    }

    public static String getMessage(Player player) {
        if (!hasBar(player)) {
            return "";
        }

        return getDragon(player, "").name;
    }

    private static String cleanMessage(String message) {
        if (message.length() > 64) {
            message = message.substring(0, 63);
        }

        return message;
    }

    private static void cancelTimer(Player player) {
        Integer timerID = timers.remove(player.getUniqueId());

        if (timerID != null) {
            Bukkit.getScheduler().cancelTask(timerID);
        }
    }

    private static void sendDragon(FakeDragon dragon, Player player) {
        NMSUtil.sendNMSPacket(player, dragon.getMetaPacket(dragon.getWatcher()));
        NMSUtil.sendNMSPacket(player,
                dragon.getTeleportPacket(player.getLocation().add(0, -300, 0)));
    }

    private static FakeDragon getDragon(Player player, String message) {
        if (hasBar(player)) {
            return players.get(player.getUniqueId());
        } else {
            return addDragon(player, cleanMessage(message));
        }
    }

    private static FakeDragon addDragon(Player player, String message) {
        FakeDragon dragon = NMSUtil.newDragon(message, player.getLocation()
                .add(0, -300, 0));

        NMSUtil.sendNMSPacket(player, dragon.getSpawnPacket());

        players.put(player.getUniqueId(), dragon);

        return dragon;
    }

    private static FakeDragon addDragon(Player player, Location loc,
            String message) {
        FakeDragon dragon = NMSUtil.newDragon(message, loc.add(0, -300, 0));

        NMSUtil.sendNMSPacket(player, dragon.getSpawnPacket());

        players.put(player.getUniqueId(), dragon);

        return dragon;
    }

}
