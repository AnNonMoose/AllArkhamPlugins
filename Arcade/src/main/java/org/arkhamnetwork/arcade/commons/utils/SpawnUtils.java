/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import org.arkhamnetwork.arcade.commons.configuration.yaml.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author devan_000
 */
public class SpawnUtils {

    public static Location getLocationFromSection(ConfigurationSection section) {
        return new Location(Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"), section.getDouble("y"),
                section.getDouble("z"), (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch"));
    }

    public static Location getLocationFromString(String locationString) {
        String[] locationSplit = locationString.split(",");

        if (locationSplit.length == 4) {
            return new Location(Bukkit.getWorld(locationSplit[0]),
                    Integer.valueOf(locationSplit[1]),
                    Integer.valueOf(locationSplit[2]),
                    Integer.valueOf(locationSplit[3]));
        } else if (locationSplit.length == 6) {
            return new Location(Bukkit.getWorld(locationSplit[0]),
                    Integer.valueOf(locationSplit[1]),
                    Integer.valueOf(locationSplit[2]),
                    Integer.valueOf(locationSplit[3]),
                    (float) Integer.valueOf(locationSplit[4]),
                    (float) Integer.valueOf(locationSplit[5]));
        }

        throw new IllegalArgumentException("Invalid location string length.");
    }
}
