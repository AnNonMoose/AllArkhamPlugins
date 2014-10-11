/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.miniplugin.namedatabase;

import lombok.Getter;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.FileConfiguration;
import org.arkhamnetwork.arcade.commons.configuration.yaml.file.YamlConfiguration;
import org.arkhamnetwork.arcade.commons.plugin.ArcadeMiniPlugin;
import org.arkhamnetwork.arcade.commons.storage.ArcadeHashMap;
import org.arkhamnetwork.arcade.core.Arcade;
import org.bukkit.Server;

/**
 *
 * @author devan_000
 */
public class NameDatabase extends ArcadeMiniPlugin {

    @Getter
    private NameDatabase plugin;

    public NameDatabase(String name, String version, Server server) {
        super(name, version, server, null);
        plugin = this;
    }

    //Storage maps.
    private static ArcadeHashMap<String, String> itemDatabase = new ArcadeHashMap<>();
    private static ArcadeHashMap<String, String> enchantDatabase = new ArcadeHashMap<>();

    public static String getItemDescription(int itemID, int dataValue) {
        //Potions are complicated.
        if (itemID == 373) {
            short potionData = (short) dataValue;
            if (potionData == 0) {
                return "Potion";
            } else {
                for (short bitPos = 14; bitPos > 5; bitPos--) {

                    short bitPow = (short) Math.pow(2, bitPos);
                    if (potionData >= bitPow) {
                        potionData -= bitPow;
                    }
                }
                dataValue = Short.toUnsignedInt(potionData);
            }
        }
        return itemDatabase.get(String.valueOf(itemID + ":" + dataValue));
    }

    public static String getEnchantDescription(int enchantID) {
        return enchantDatabase.get(String.valueOf(enchantID));
    }
    
    @Override
    public void onEnable() {
        // Needed - ArcadeMiniPlugin
        super.onEnable();

        log("Attempting to load databases...");
        FileConfiguration nameDatabase = YamlConfiguration.loadConfiguration(Arcade.getInstance().getResource("nameDatabase.yml"));

        for (String item : nameDatabase.getConfigurationSection("items").getKeys(false)) {
            itemDatabase.put(item, nameDatabase.getString("items." + item));
        }

        for (String enchantID : nameDatabase.getConfigurationSection("enchantments").getKeys(false)) {
            enchantDatabase.put(enchantID, nameDatabase.getString("enchantments." + enchantID));
        }

        // Needed - ArcadeMiniPlugin
        super.postEnable();
    }

    @Override
    public void onDisable() {
        // Needed - ArcadeMiniPlugin
        super.onDisable();

        // Needed - ArcadeMiniPlugin
        super.postDisable();
    }

    @Override
    public void postEnable() {
    }

    @Override
    public void postDisable() {
    }

}
