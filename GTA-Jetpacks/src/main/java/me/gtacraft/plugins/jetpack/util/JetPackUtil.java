package me.gtacraft.plugins.jetpack.util;

import me.gtacraft.plugins.jetpack.GTAJetpack;
import me.gtacraft.plugins.jetpack.JetPackManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Connor on 7/11/14. Designed for the GTA-Jetpacks project.
 */

public class JetPackUtil {

    public static int hasPack(Player player) {
        ItemStack armor = player.getEquipment().getChestplate();
        return JetPackManager.getTier(armor);
    }
}
