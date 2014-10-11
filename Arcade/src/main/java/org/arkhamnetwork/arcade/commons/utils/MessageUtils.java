/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import org.bukkit.ChatColor;

/**
 *
 * @author devan_000
 */
public class MessageUtils {

    public static String translateToColorCode(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
