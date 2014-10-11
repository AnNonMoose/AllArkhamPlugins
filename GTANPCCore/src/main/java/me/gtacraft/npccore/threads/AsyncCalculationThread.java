package me.gtacraft.npccore.threads;

import me.gtacraft.npccore.enumerations.SpawnRequestType;
import me.gtacraft.npccore.struct.CalculationRequest;
import me.gtacraft.npccore.struct.SpawnRequest;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class AsyncCalculationThread extends BukkitRunnable {

    public static CopyOnWriteArrayList<CalculationRequest> pendingSpawnRequests = new CopyOnWriteArrayList<CalculationRequest>();
    private final Random random = new Random();

    @Override
    public void run() {

        while (true) {

            for (CalculationRequest request : pendingSpawnRequests) {

                Chunk chunk = request.getChunk();

                for (Entity entity : chunk.getEntities()) {
                    if (entity instanceof Villager || entity instanceof Skeleton || entity instanceof PigZombie) {
                        entity.remove();
                    }
                }

                CopyOnWriteArrayList<SpawnRequest> requests = new CopyOnWriteArrayList<SpawnRequest>();

                for (int i = 0; i < request.getAmountToSpawn(); i++) {
                    int x = random.nextInt(16);
                    int z = random.nextInt(16);

                    Location block = chunk.getBlock(x, 0, z).getLocation();
                    Location blockAbove = chunk.getBlock(x, 0, z).getLocation();

                    for (int y = 1; y < 258; y++) {
                        try {
                            block.setY(y);
                            blockAbove.setY(y + 1);

                            if (block == null || block.getBlock() == null) {
                                continue;
                            }

                            if (block.getBlock().getType().equals(Material.AIR) && blockAbove.getBlock().getType().equals(Material.AIR)) {
                                requests.add(new SpawnRequest(block, request.getSpawnRequestType()));
                                break;
                            }

                        } catch (IllegalStateException ex) {
                        }
                    }
                }

                MobSpawnThread.spawnRequests.put(chunk, requests);
                pendingSpawnRequests.remove(request);
            }

            //Sleep for 1 second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

}
