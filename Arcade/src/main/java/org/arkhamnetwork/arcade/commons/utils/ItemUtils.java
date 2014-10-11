/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author devan_000
 */
public class ItemUtils {

    public static ItemStack parseItemFromString(String itemString) {
        String[] spacedSplit = itemString.split(" ", 3);

        String[] dataValueSplit = spacedSplit[0].split(":");

        Material type = Material.getMaterial(Integer.valueOf(dataValueSplit[0]));

        if (type == null) {
            throw new IllegalArgumentException("Null material");
        }

        int amount = 0;
        try {
            amount = Integer.valueOf(spacedSplit[1].toUpperCase());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        ItemStack stack = new ItemStack(type, amount);

        if (dataValueSplit.length > 1) {
            stack.setDurability((short) Short.valueOf(dataValueSplit[1]));
        }

        if (spacedSplit.length > 2) {
            for (String metaDataValue : spacedSplit[2].split(" ")) {
                String[] colonSplit = metaDataValue.split(":");

                // Is it an enchantment?
                Enchantment enchantment = Enchantment.getByName(colonSplit[0]
                        .toUpperCase());
                if (enchantment != null) {
                    stack.addUnsafeEnchantment(enchantment,
                            Integer.valueOf(colonSplit[1]));
                    continue;
                }

                // Is it a display name?
                if (colonSplit[0].toLowerCase().equals("name")) {
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(MessageUtils
                            .translateToColorCode(colonSplit[1]).replace("_", " "));
                    stack.setItemMeta(meta);
                    continue;
                }

                if (colonSplit[0].toLowerCase().equals("lore")) {
                    String[] loreSplit = colonSplit[1].split(",");
                    List<String> lore = new ArrayList<>();
                    for (String loreValue : loreSplit) {
                        lore.add(MessageUtils.translateToColorCode(loreValue.replace("_", " ")));
                    }
                    ItemMeta meta = stack.getItemMeta();
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                    continue;
                }
            }
        }
        return stack;
    }
}
