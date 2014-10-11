package me.gtacraft.plugins.melondrop.task;

import com.google.common.collect.Lists;
import me.gtacraft.plugins.melondrop.GTAMelondrop;
import me.gtacraft.plugins.melondrop.util.FireworkEffectPlayer;
import me.gtacraft.plugins.melondrop.util.ParticleEffects;
import me.gtacraft.plugins.melondrop.item.ItemData;
import me.gtacraft.plugins.safezone.util.SafezoneUtil;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Connor on 7/12/14. Designed for the GTA-Melondrop project.
 */

public class MelonDropTask implements Runnable {

    public static int breakAttempts = 0;

    public static Block wrapped;

    public static int seconds = 0;

    public void run() {
        if (seconds < GTAMelondrop.run_interval && (wrapped == null)) {
            //don't run
            ++seconds;
            return;
        } else if (wrapped != null) {
            ParticleEffects.sendToLocation(ParticleEffects.ENCHANTMENT_TABLE, wrapped.getLocation(), 1f, 1f, 1f, 1f, 50);
            return;
        }

        seconds = 0;
        drop();
    }

    public static void drop() {
        Location spawnAt = null;
        while (spawnAt == null) {
            Location random = randomRange(GTAMelondrop.min, GTAMelondrop.max);
            Block top = random.getWorld().getHighestBlockAt(random);
            int topType = (top == null || top.getType().equals(Material.AIR)) ? Material.STATIONARY_WATER.getId() : top.getTypeId();
            if (topType == Material.STATIONARY_WATER.getId() || topType == Material.WATER.getId() || SafezoneUtil.isInSafeZone(random))
                continue;
            random.setY(top.getY()+1);
            spawnAt = random;
        }

        final Location finalSpawnAt = spawnAt;
        finalSpawnAt.getBlock().setType(Material.MELON_BLOCK);
        wrapped = finalSpawnAt.getBlock();

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a&l>> EVENT |&7 The &a&lGiant Magical Melon &7has fallen from the sky at &e&n(X: "+ spawnAt.getBlockX()+", Y: "+spawnAt.getBlockY()+", Z: "+spawnAt.getBlockZ()+")&r&7!"));
    }

    public static List<FallingBlock> remove_instantly = Lists.newArrayList();

    public static void tick() {
        if (breakAttempts >= 50 && wrapped != null) {
            //destroy + loot
            sendCrackPacket(wrapped, wrapped.getTypeId());
            wrapped.setType(Material.AIR);

            for (int i = 0; i < 50; i++) {
                ItemData pick = GTAMelondrop.getDrops().get((int)(Math.random()*GTAMelondrop.getDrops().size()));
                if (!(pick.getChance().didWin(0.0)))
                    continue;

                Item shoot = wrapped.getWorld().dropItem(wrapped.getLocation().clone().add(0.5, 0.6, 0.5), pick.rebuild());
                shoot.setPickupDelay(15);
                shoot.setVelocity(new Vector((Math.random())-.5, (Math.random()*1.2)+1, (Math.random())-.5));
                shoot.getWorld().playSound(shoot.getLocation(), Sound.ZOMBIE_WOODBREAK, 1, 1);
            }

            for (int i = 0; i < (int)(Math.random()*10); i++) {
                final FallingBlock fb = wrapped.getWorld().spawnFallingBlock(wrapped.getLocation().clone().add(0, 1, 0), Material.MELON_BLOCK.getId(), (byte)0);
                fb.setVelocity(new Vector((Math.random()*.2)-.1, (Math.random()*.2)+1, (Math.random()*.2)-.1));
                fb.setDropItem(false);
                remove_instantly.add(fb);
                Runnable explode = new Runnable() {
                    public void run() {
                        if (remove_instantly.contains(fb)) {
                            remove_instantly.remove(fb);
                            fb.remove();
                            try {
                                GTAMelondrop.fireworks.playFirework(fb.getWorld(),
                                        fb.getLocation(),
                                        FireworkEffect.builder().withColor(Color.GREEN).withColor(Color.LIME).with(FireworkEffect.Type.BURST).build());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                };
                Bukkit.getScheduler().scheduleSyncDelayedTask(GTAMelondrop.get(), explode, 20);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getLocation().distance(wrapped.getLocation()) < 20)
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l>> EVENT |&7 The &a&lGiant Magical Melon &7has burst, guns and loot can be seen falling from the sky!"));
            }

            wrapped = null;
            breakAttempts = 0;
        }
    }

    public static void sendCrackPacket(Block b, int typeID) {
        Packet particles = new PacketPlayOutWorldEvent(2001, Math.round(b.getX()), Math.round(b.getY()), Math.round(b.getZ()), typeID, false);
        ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().sendPacketNearby(b.getX(), b.getY(), b.getZ(), 16, ((CraftWorld) b.getWorld()).getHandle().dimension, particles);
    }

    public static Location randomRange(Location min, Location max) {
        int xMin = min.getBlockX();
        int xMax = max.getBlockX();
        int zMin = min.getBlockZ();
        int zMax = max.getBlockZ();

        int randX = xMin + (int)(Math.random() * ((xMax - xMin) +1));
        int randZ = zMin + (int)(Math.random() * ((zMax - zMin) + 1));

        return new Location(min.getWorld(), randX, 100, randZ);
    }
}
