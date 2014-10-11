/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author devan_000
 */
public class PlayerUtils {

    public static void resetPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFireTicks(0);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setExp(0);
        player.setMaxHealth(20.0);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

}
