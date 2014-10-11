package me.gtacraft;

import net.minecraft.server.v1_7_R3.MinecraftEncryption;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Connor on 4/29/14. Designed for the GTA-NametagEdit project.
 */

public class MinecartName implements Listener {

    private static EntityHider hider;

    public static void setHider(EntityHider eh) {
        hider = eh;
    }

    private Minecart minecart;
    private Silverfish holder;

    public MinecartName(Minecart minecart, String displayName) {
        LivingEntity sv = (LivingEntity)minecart.getWorld().spawnEntity(minecart.getLocation().add(0, 100 ,0), EntityType.);
        sv.setCustomName(ChatColor.translateAlternateColorCodes('&', displayName));
        sv.setCustomNameVisible(true);
        minecart.setPassenger(sv);

        for (Player p : Bukkit.getOnlinePlayers()) {
            //hider.hideEntity(p, sv);
        }
    }

    public void handleMount(Player player) {

    }

    public void handleDismount(Player player) {

    }
}
