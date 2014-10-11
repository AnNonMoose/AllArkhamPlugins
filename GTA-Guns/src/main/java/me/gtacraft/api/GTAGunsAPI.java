package me.gtacraft.api;

import me.gtacraft.gun.Gun;
import me.gtacraft.gun.GunData;
import me.gtacraft.gun.GunFactory;
import me.gtacraft.util.GunUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Connor on 4/27/14. Designed for the GTA-Guns project.
 */

public class GTAGunsAPI {

    public static GunData giveGun(Player player, Gun gun) {
        ItemStack stack = GunUtil.getGunStack(gun);

        if (gun == null || stack == null)
            return null;

        GunData data = new GunData(gun, stack);
        GunUtil.updateStack(data, 1);

        player.getInventory().addItem(stack);
        return data;
    }

    public static GunData createData(Gun gun) {
        ItemStack stack = GunUtil.getGunStack(gun);

        if (gun == null || stack == null)
            return null;

        GunData data = new GunData(gun, stack);
        GunUtil.updateStack(data, 1);

        return data;
    }

    public static Gun getGunFromName(String name) {
        return GunFactory.getGun(name);
    }

    public static Gun getGunFromItem(ItemStack stack) {
        return GunFactory.getGun(stack);
    }
}
