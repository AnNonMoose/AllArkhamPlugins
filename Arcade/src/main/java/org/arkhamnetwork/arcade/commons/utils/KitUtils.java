/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import java.util.ArrayList;
import java.util.List;
import org.arkhamnetwork.arcade.commons.configuration.yaml.ConfigurationSection;
import org.arkhamnetwork.arcade.commons.kit.Kit;
import org.arkhamnetwork.arcade.commons.userstorage.PlayerRank;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author devan_000
 */
public class KitUtils {

    public static Kit getKitFromSection(ConfigurationSection section) {
        String kitName = section.getName();
        List<ItemStack> items = new ArrayList<>();
        for (String itemString : section.getStringList("items")) {
            items.add(ItemUtils.parseItemFromString(itemString));
        }
        int creditCost = section.getInt("creditCost");
        List<PlayerRank> ranksWithPermission = new ArrayList<>();
        for (String rankString : section.getStringList("ranksWithPermission")) {
            PlayerRank valueRank = PlayerRank.valueOf(rankString);

            if (valueRank != null) {
                ranksWithPermission.add(valueRank);
            }
        }

        List<PotionEffect> effects = new ArrayList<>();
        for (String effectString : section.getStringList("effects")) {
            String[] split = effectString.split(":");

            if (split.length == 2) {
                PotionEffectType type = PotionEffectType.getByName(split[0]);
                int amplifier = (Integer.valueOf(split[1]) - 1);
                int duration = Integer.MAX_VALUE;
                
                if (type != null) {
                    effects.add(new PotionEffect(type, duration, amplifier));
                }
            }
        }

        return new Kit(kitName, items, creditCost, ranksWithPermission, effects);
    }

}
