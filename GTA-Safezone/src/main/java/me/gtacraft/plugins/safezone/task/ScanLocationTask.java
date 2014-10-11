package me.gtacraft.plugins.safezone.task;

import me.gtacraft.plugins.safezone.ParticleEffects;
import me.gtacraft.plugins.safezone.Safezone;
import me.gtacraft.plugins.safezone.util.SafezoneUtil;
import me.gtacraft.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Connor on 6/27/14. Designed for the GTA-Safezone project.
 */

public class ScanLocationTask implements Runnable {
    //Be sure to schedule this sync!
    public void run() {
        for (Player player : Safezone.async_player_map.keySet()) {
            if (SafezoneUtil.getSubscribedTime(player.getName()) != -1)
                ParticleEffects.sendToLocation(ParticleEffects.HAPPY_VILLAGER, player.getEyeLocation().add(0, 0.5, 0), 0.1f, 0.1f, 0.1f, (float)1, 3);

            boolean current = SafezoneUtil.isInSafeZone(player.getLocation());
            boolean before = isInSafezone(player);

            if (before != current) {
                if (before == true) {
                    int prev = SafezoneUtil.getSubscribedTime(player.getName());
                    if (prev != -1)
                        SafezoneUtil.unsubscribe(player.getName());

                    SafezoneUtil.subscribe(player.getName(), 20);
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*20, 1));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) &cYou are no longer in a safe zone."));
                    if (prev == -1)
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l(!) &eYou can't deal or receive damage for the next &n20&r&e seconds"));
                    if (Safezone.async_player_map.get(player) < 2) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l(!) &aType \"/safezone\" to teleport to the nearest safezone!"));
                        int at = Safezone.async_player_map.remove(player);
                        Safezone.async_player_map.put(player, at+1);
                    }
                    SoundUtil.playSound(player.getLocation(), "NOTE_PIANO-1-1-0,NOTE_PIANO-1-0-5", player);
                    ParticleEffects.sendCrackToLocation(true, Material.MELON.getId(), (byte)0x0, player.getEyeLocation(), new Random().nextFloat()*2, new Random().nextFloat()*2, new Random().nextFloat()*2, 200);
                } else if (before == false) {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    if (SafezoneUtil.getSubscribedTime(player.getName()) != -1)
                        SafezoneUtil.unsubscribe(player.getName());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l(!) &aYou have entered a safe zone."));
                    if (Safezone.async_player_map.get(player) < 2) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l(!) &aType \"/gta\" to change servers while in a safezone!"));
                        int at = Safezone.async_player_map.remove(player);
                        Safezone.async_player_map.put(player, at+1);
                    }
                    SoundUtil.playSound(player.getLocation(), "NOTE_PIANO-1-0-0,NOTE_PIANO-1-1-5", player);
                    ParticleEffects.sendCrackToLocation(true, Material.MELON.getId(), (byte)0x0, player.getEyeLocation(), new Random().nextFloat()*2, new Random().nextFloat()*2, new Random().nextFloat()*2, 200);
                }
            }

            if (current) {
                ParticleEffects.sendToLocation(ParticleEffects.HAPPY_VILLAGER, player.getEyeLocation().add(0, 0.5, 0), 0.1f, 0.1f, 0.1f, (float)1, 3);
            }
            setIsInSafezone(player);
        }
    }

    private boolean isInSafezone(Player player) {
        return player.hasMetadata("is_in_safezone") ? (Boolean)player.getMetadata("is_in_safezone").get(0).value() : false;
    }

    private void setIsInSafezone(Player player) {
        boolean is = SafezoneUtil.isInSafeZone(player.getLocation());
        player.setMetadata("is_in_safezone", new FixedMetadataValue(Safezone.getInstance(), is));
    }
}
