/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author devan_000
 */
public class ConfigUtils {

    public static void createFolderIfNotExists(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void createFileIfNotExists(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void createConfigWithDefaultsIfNotExists(File file,
            InputStream defaults) {
        try {
            createFileIfNotExists(file);

            if (defaults != null) {
                YamlConfiguration.loadConfiguration(defaults).save(file);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
