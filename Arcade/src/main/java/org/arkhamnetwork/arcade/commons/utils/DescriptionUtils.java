/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.arkhamnetwork.arcade.commons.kit.Kit;
import org.arkhamnetwork.arcade.commons.storage.ArcadeHashMap;
import org.arkhamnetwork.arcade.miniplugin.namedatabase.NameDatabase;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author devan_000
 */
public class DescriptionUtils {

    public static List<String> buildKitDescriptionMessage(Kit kit) {
        List<String> builtMessage = new ArrayList<>();

        builtMessage.add(ChatColor.AQUA + "" + ChatColor.BOLD + ""
                + ChatColor.STRIKETHROUGH
                + "=============================================");

        builtMessage.add(ChatColor.YELLOW + "Kit - "
                + ChatColor.RED + "" + ChatColor.BOLD + kit.getName());

        builtMessage.add(" ");

        builtMessage.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Contents:");

        for (ItemStack item : kit.getItems()) {
            builtMessage.add(ChatColor.WHITE + "- " + item.getAmount() + "x " + NameDatabase.getItemDescription(item.getTypeId(), item.getDurability()));
            for (Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
                builtMessage.add(ChatColor.GRAY + "- (" + NameDatabase.getEnchantDescription(enchantment.getKey().getId()) + " Level " + String.valueOf(enchantment.getValue()) + ")");
            }
        }

        builtMessage.add(" ");

        builtMessage.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Effects:");

        for (PotionEffect effect : kit.getEffects()) {
            builtMessage.add(ChatColor.WHITE + "- " + effect.getType().getName() + " " + NumberUtils.integerToRomanNumeral((effect.getAmplifier() + 1)));
        }

        builtMessage.add(ChatColor.AQUA + "" + ChatColor.BOLD + ""
                + ChatColor.STRIKETHROUGH
                + "=============================================");

        return builtMessage;
    }

    public static List<String> buildGameDescriptionMessage(String gameName,
            List<String> gameDescription, String mapName,
            ArcadeHashMap<String, String> mapAuthors, int gameMinPlayers,
            int gameMaxPlayers) {
        List<String> builtMessage = new ArrayList<>();

        builtMessage.add(ChatColor.AQUA + "" + ChatColor.BOLD + ""
                + ChatColor.STRIKETHROUGH
                + "=============================================");
        builtMessage.add(ChatColor.YELLOW + "You are playing - "
                + ChatColor.RED + "" + ChatColor.BOLD + gameName);
        builtMessage.add(" ");

        for (String descriptionLine : gameDescription) {
            builtMessage.add(ChatColor.WHITE + "- " + descriptionLine);
        }
        builtMessage.add(" ");
        builtMessage.add(ChatColor.YELLOW
                + "Map - "
                + ChatColor.RED
                + ""
                + ChatColor.BOLD
                + mapName
                + ChatColor.GRAY
                + " made by "
                + ChatColor.RED
                + ""
                + ChatColor.BOLD
                + mapAuthors.keySet().toString().replace("[", "")
                .replace("]", "") + ChatColor.GRAY + ".");
        builtMessage.add(ChatColor.AQUA + "" + ChatColor.BOLD + ""
                + ChatColor.STRIKETHROUGH
                + "=============================================");

        return builtMessage;
    }

}
