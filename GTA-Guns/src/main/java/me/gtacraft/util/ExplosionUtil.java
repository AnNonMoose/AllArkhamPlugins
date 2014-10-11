package me.gtacraft.util;

import com.google.common.collect.Lists;
import me.gtacraft.GTAGuns;
import me.gtacraft.event.BulletHitBlockEvent;
import me.gtacraft.event.WeaponDamageEntityEvent;
import me.gtacraft.gun.BulletData;
import me.gtacraft.gun.GunData;
import me.vaqxine.VNPC.tasks.CrimeResponseTask;
import me.vaqxine.WorldRegeneration.RegenerationAPI;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 5/3/14. Designed for the GTA-Guns project.
 */

public class ExplosionUtil {

    public static List<Item> deny_pickup = new ArrayList<Item>();

    public static void explode(final BulletData bullet) {
        if (bullet == null)
            return;

        final GunData gun = bullet.getGunData();

        boolean boom = gun.getDefaultAttribute("explosion.enabled", false).getBooleanValue();
        if (boom == false)
            return;

        final double dist = gun.getDefaultAttribute("explosion.radius", 1.0).getDoubleValue();

        Location origin = bullet.getProjectile().getLocation();
        boolean isScatter = gun.getDefaultAttribute("explosion.scatter.enabled", false).getBooleanValue();

        if (isScatter) {
            int amount = gun.getAttribute("explosion.scatter.amount").getIntValue();
            ItemStack drop = new ItemStack(Material.getMaterial(gun.getAttribute("explosion.scatter.id").getIntValue()), 1, (short)0, (byte)gun.getAttribute("explosion.scatter.data").getIntValue());
            drop.setAmount(Short.MAX_VALUE);

            double expRadius = gun.getDefaultAttribute("explosion.scatter.radius", 1.0).getDoubleValue();
            for (int i = 0; i < amount; i++) {

                final Item dropped = origin.getWorld().dropItem(origin, drop.clone());
                dropped.setVelocity(new Vector((Math.random()*expRadius)-(expRadius/2), (Math.random()*expRadius), (Math.random()*expRadius)-(expRadius/2)));

                Runnable explode = new Runnable() {
                    public void run() {
                        Location hit = dropped.getLocation();
                        destroyBlocks(dist, hit, 15);
                        ParticleEffects.sendToLocation(ParticleEffects.LARGE_EXPLODE, hit, 0.5f, 0.5f, 0.5f, 0, 5);
                        SoundUtil.playSound(dropped.getLocation(), "EXPLODE-10-1-0", Bukkit.getOnlinePlayers());
                        for (LivingEntity e : hit.getWorld().getLivingEntities()) {
                            if (e.getLocation().distance(hit) <= dist && !e.isDead() && e.isValid()) {
                                WeaponDamageEntityEvent wdee = new WeaponDamageEntityEvent(bullet, e);
                                Bukkit.getPluginManager().callEvent(wdee);

                                if (!wdee.isCancelled())
                                    e.damage(ArmorUtil.recomputeDamage(e, gun.getDefaultAttribute("explosion.scatter.damage", 1.0).getDoubleValue()));
                            }
                        }
                        dropped.remove();
                    }
                };
                Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), explode, (long) ((gun.getAttribute("explosion.scatter.timeout").getIntValue()*20)+Math.random()*20));

                CrimeResponseTask.addCrimeLocation(dropped.getLocation(), bullet.getProjectile().getShooter());
                deny_pickup.add(dropped);
            }

            return;
        } else {
            Location projHit = bullet.getProjectile().getLocation();
            SoundUtil.playSound(projHit, "EXPLODE-10-1-0", Bukkit.getOnlinePlayers());
            ParticleEffects.sendToLocation(ParticleEffects.HUGE_EXPLOSION, projHit, 0f, 0f, 0f, 0, 1);
            destroyBlocks(dist, projHit, 15);
            CrimeResponseTask.addCrimeLocation(projHit, bullet.getProjectile().getShooter());
            for (LivingEntity close : projHit.getWorld().getLivingEntities()) {
                Location at = close.getLocation();
                if (at.distance(projHit) <= dist) {
                    WeaponDamageEntityEvent wdee = new WeaponDamageEntityEvent(bullet, close);
                    Bukkit.getPluginManager().callEvent(wdee);

                    if (!wdee.isCancelled())
                        close.damage(ArmorUtil.recomputeDamage(close, gun.getDefaultAttribute("explosion.damage", 1.0).getDoubleValue()));
                }
            }

            bullet.getProjectile().remove();
        }
    }

    /*static List<Material> state_blocks = Lists.newArrayList();
    static {
        state_blocks.add(Material.SAPLING);
        state_blocks.add(Material.RAILS);
        state_blocks.add(Material.ACTIVATOR_RAIL);
        state_blocks.add(Material.DETECTOR_RAIL);
        state_blocks.add(Material.POWERED_RAIL);
        state_blocks.add(Material.DEAD_BUSH);
        state_blocks.add(Material.getMaterial(31));
        state_blocks.add(Material.YELLOW_FLOWER);
        state_blocks.add(Material.RED_ROSE);
        state_blocks.add(Material.BROWN_MUSHROOM);
        state_blocks.add(Material.RED_MUSHROOM);
        state_blocks.add(Material.TORCH);
        state_blocks.add(Material.LADDER);
        state_blocks.add(Material.LEVER);
        state_blocks.add(Material.STONE_PLATE);
        state_blocks.add(Material.WOOD_PLATE);
        state_blocks.add(Material.REDSTONE_TORCH_OFF);
        state_blocks.add(Material.REDSTONE_TORCH_ON);
        state_blocks.add(Material.STONE_BUTTON);
        state_blocks.add(Material.WOOD_BUTTON);
        state_blocks.add(Material.SNOW);
        state_blocks.add(Material.CACTUS);
        state_blocks.add(Material.VINE);
        state_blocks.add(Material.WATER_LILY);
        state_blocks.add(Material.TRIPWIRE_HOOK);
        state_blocks.add(Material.BED_BLOCK);
        state_blocks.add(Material.getMaterial(141));
        state_blocks.add(Material.getMaterial(142));
        state_blocks.add(Material.GOLD_PLATE);
        state_blocks.add(Material.IRON_PLATE);
        state_blocks.add(Material.CARPET);
        state_blocks.add(Material.getMaterial(175));
        state_blocks.add(Material.FLOWER_POT);
    }*/

    public static void destroyBlocks(final double radius, final Location current, final int regenTime) {
        BulletHitBlockEvent bhbe = new BulletHitBlockEvent(current);
        Bukkit.getPluginManager().callEvent(bhbe);

        if (bhbe.isCancelled())
            return;

        Runnable async = new Runnable() {
            public void run() {
                List<Block> blocks = Lists.newArrayList();
                int xMin = (int)(current.getX()-radius);
                int yMin = (int)(current.getY()-radius);
                int zMin = (int)(current.getZ()-radius);
                for (int x = xMin; x < xMin+(radius*2); x++) {
                    for (int y = yMin; y < yMin+(radius*2); y++) {
                        for (int z = zMin; z < zMin+(radius*2); z++) {
                            Location hit = new Location(current.getWorld(), x, y, z);
                            if (hit.getBlock().getType().equals(Material.AIR))
                                continue;

                            if (hit.toVector().isInSphere(current.toVector(), radius)) {
                                if (hit.getY() <= 10)
                                    continue;

                                if (hit.getBlock().getType().equals(Material.WOODEN_DOOR) || hit.getBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
                                    Location up = hit.clone().add(0, 1, 0);
                                    Location down = hit.clone().add(0, -1, 0);
                                    if (up.getBlock().getType().equals(Material.WOODEN_DOOR) || up.getBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
                                        blocks.add(up.getBlock());
                                    } else if (down.getBlock().getType().equals(Material.WOODEN_DOOR) || down.getBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
                                        blocks.add(down.getBlock());
                                    }
                                }
                                blocks.add(hit.getBlock());
                            }
                        }
                    }
                }
                final List<Block> fblocks = blocks;
                Runnable destroy = new Runnable() {
                    public void run() {
                        for (final Block b : fblocks) {
                            RegenerationAPI.queueBlockForRegeneration(b, regenTime);
                            final int id = b.getTypeId();
                            if (Math.random()*10 < 8) {
                                Runnable async = new Runnable() {
                                    public void run() {
                                        sendPacket(b, id);
                                    }
                                };
                                Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGuns.getInstnace(), async);
                            }
                            b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getTypeId(), b.getData());
                            b.setType(Material.AIR);
                        }
                    }

                    void sendPacket(Block b, int id) {
                        Packet particles = new PacketPlayOutWorldEvent(2001, Math.round(b.getX()), Math.round(b.getY()), Math.round(b.getZ()), id, false);
                        ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().sendPacketNearby(b.getX(), b.getY(), b.getZ(), 16, ((CraftWorld) b.getWorld()).getHandle().dimension, particles);
                    }
                };
                Bukkit.getScheduler().scheduleSyncDelayedTask(GTAGuns.getInstnace(), destroy);
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGuns.getInstnace(), async);
    }

    public static List<Item> getDeniedItems() {
        return deny_pickup;
    }
}
