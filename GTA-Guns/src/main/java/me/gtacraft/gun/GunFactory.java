package me.gtacraft.gun;

import me.gtacraft.GTAGuns;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Connor on 4/27/14. Designed for the GTA-Guns project.
 */

public class GunFactory {

    private static Map<String, Gun> possibleGuns = new HashMap();

    public static void blueprint(Gun gun, ConfigurationSection gunCFG, String build) {
        List<String> keys = new ArrayList<String>();
        keys.addAll(gunCFG.getKeys(false));

        for (int i = 0; i < keys.size(); i++) {
            String at = keys.get(i);
            String label = build.equals("") ? at : build+"."+at;

            if (gunCFG.isConfigurationSection(at)) {
                ConfigurationSection sec = gunCFG.getConfigurationSection(at);
                blueprint(gun, sec, label);
                continue;
            } else {
                Object value = gunCFG.get(at);

                if (gun == null)
                    gun = new Gun();

                gun.addAttribute(label.toLowerCase(), value);
            }
        }

        possibleGuns.put(gun.getAttribute("name").getStringValue().toLowerCase(), gun);
    }

    public static Gun getGun(String name) {
        return possibleGuns.get(name.toLowerCase());
    }

    public static Gun getGun(ItemStack stack) {
        for (Gun gun : possibleGuns.values()) {
            if ((int)gun.getAttribute("type.id").getIntValue() == stack.getTypeId() && (int)gun.getAttribute("type.data").getIntValue() == (int)stack.getData().getData()) {
                return gun;
            }
        }
        return null;
    }

    public static Map<String, Gun> getGuns() {
        return possibleGuns;
    }

    public static void reload() {
        possibleGuns.clear();
        GTAGuns.getInstnace().reloadGuns();
    }
}
