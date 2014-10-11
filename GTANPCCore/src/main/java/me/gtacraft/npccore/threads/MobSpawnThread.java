package me.gtacraft.npccore.threads;

import me.gtacraft.npccore.GTANPCCore;
import me.gtacraft.npccore.enumerations.SpawnRequestType;
import me.gtacraft.npccore.struct.Gang;
import me.gtacraft.npccore.struct.SpawnRequest;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class MobSpawnThread extends BukkitRunnable {

    private GTANPCCore plugin = GTANPCCore.get();
    public static ConcurrentHashMap<Chunk, Collection<SpawnRequest>> spawnRequests = new ConcurrentHashMap<Chunk, Collection<SpawnRequest>>();
    private final Random random = new Random();

    int i = 0;

    @Override
    public void run() {

        for (Chunk requestChunk : spawnRequests.keySet()) {

            CopyOnWriteArrayList<UUID> gangMembers = new CopyOnWriteArrayList<UUID>();

            for (SpawnRequest request : spawnRequests.get(requestChunk)) {
                if (request.getSpawnRequestType() == SpawnRequestType.NPC) {
                    Entity e = request.getLocation().getWorld().spawnEntity(request.getLocation(), EntityType.VILLAGER);
                    Villager villager = (Villager) e;

                    if (Math.random() * 100 > 66.6) {
                        villager.setAdult();
                    } else {
                        villager.setBaby();
                    }

                    villager.setCustomName(ChatColor.YELLOW + "GTA Villager " + i++);
                    villager.setCustomNameVisible(true);
                    villager.setProfession(Villager.Profession.values()[(int) (Math.random() * Villager.Profession.values().length)]);
                    plugin.entityController.entityUUIDs.add(villager.getUniqueId());
                } else if (request.getSpawnRequestType() == SpawnRequestType.GANGSTER) {
                    Entity e = request.getLocation().getWorld().spawnEntity(request.getLocation(), EntityType.SKELETON);
                    Skeleton skeleton = (Skeleton) e;

                    if (Math.random() * 100 > 50) {
                        skeleton.setSkeletonType(Skeleton.SkeletonType.NORMAL);
                    } else {
                        skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
                    }

                    skeleton.setCustomName(ChatColor.RED + "GTA Gangster " + i++);
                    skeleton.setCustomNameVisible(true);
                    skeleton.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD, 1));
                    plugin.entityController.entityUUIDs.add(skeleton.getUniqueId());
                    gangMembers.add(skeleton.getUniqueId());
                } else if (request.getSpawnRequestType() == SpawnRequestType.POLICEOFFICER) {
                    Entity e = request.getLocation().getWorld().spawnEntity(request.getLocation(), EntityType.PIG_ZOMBIE);
                    PigZombie zombie = (PigZombie) e;

                    zombie.setAngry(false);
                    zombie.setBaby(false);

                    zombie.setCustomName(ChatColor.DARK_RED + "GTA Officer " + i++);
                    zombie.setCustomNameVisible(true);
                    zombie.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD, 1));
                    zombie.getEquipment().setHelmet(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()));
                    plugin.entityController.entityUUIDs.add(zombie.getUniqueId());
                }
            }

            Gang gang = new Gang(gangMembers);
            for (UUID gangMember : gang.getMembers()) {
                plugin.entityController.gangs.put(gangMember, gang);
            }

            spawnRequests.remove(requestChunk);
        }

    }
}
