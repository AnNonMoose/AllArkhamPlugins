package me.gtacraft.plugins.jetpack.task;

import me.gtacraft.plugins.jetpack.util.JetPackUtil;
import me.gtacraft.plugins.jetpack.util.ParticleEffects;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 7/11/14. Designed for the GTA-Jetpacks project.
 */

public class JetTask implements Runnable {

    public static volatile HashMap<String, Player> async_player_map = new HashMap<>();
    public static volatile HashMap<String, Double> speed_map = new HashMap<>();

    public void run() {
        for (Player player : async_player_map.values()) {
            int pack = JetPackUtil.hasPack(player);
            if (player.isSneaking()) {
                //check pack
                if (pack != -1) {
                    double multiplier = pack;

                    double prev = 0.01;
                    if (speed_map.containsKey(player.getName()))
                        prev = speed_map.remove(player.getName());
                    if (!(player.getAllowFlight())) {
                        player.setAllowFlight(true);
                    }

                    Vector pVec = player.getEyeLocation().getDirection();
                    pVec.setX(pVec.getX() / 5);
                    pVec.setZ(pVec.getZ() / 5);
                    pVec.setY(prev);
                    player.setVelocity(pVec);
                    prev+=((double)pack/20.0);
                    if (prev > 1.5)
                        prev = 1.5;
                    speed_map.put(player.getName(), prev);
                    ParticleEffects.CLOUD.sendToPlayer(player, player.getLocation(), .2f, .2f, .2f, .1f, 3);
                    player.playSound(player.getLocation(), Sound.FIZZ, 1, 1);
                }
                continue;
            }

            if (player.getAllowFlight() && pack != -1 && !(player.hasPermission("jetpack.bypass")))
                player.setAllowFlight(false);
            if ((speed_map.containsKey(player.getName()) ? speed_map.get(player.getName()) : 0) > 0) {
                speed_map.put(player.getName(), (speed_map.containsKey(player.getName()) ? speed_map.remove(player.getName())-((double)(-pack+6)/20.0) : 0));
            }
        }
    }
}
