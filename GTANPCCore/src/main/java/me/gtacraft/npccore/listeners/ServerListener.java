package me.gtacraft.npccore.listeners;

import me.gtacraft.npccore.GTANPCCore;
import me.gtacraft.npccore.enumerations.SpawnRequestType;
import me.gtacraft.npccore.struct.CalculationRequest;
import me.gtacraft.npccore.threads.AsyncCalculationThread;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class ServerListener implements Listener {

    private final GTANPCCore plugin = GTANPCCore.get();

    @EventHandler(priority= EventPriority.MONITOR, ignoreCancelled = true)
    public void onLoad(ChunkLoadEvent event) {

        if (plugin.configController.getConfiguration().isPassiveNPCEnabled()) {
            if (Math.random() * 100 <= plugin.configController.getConfiguration().getPassiveChanceToSpawn()) {
                AsyncCalculationThread.pendingSpawnRequests.add(new CalculationRequest(event.getChunk(), SpawnRequestType.NPC, plugin.configController.getConfiguration().getPassiveAmountPlaceOnChunkLoad()));
            }
        }

        if (plugin.configController.getConfiguration().isGangsterMobsEnabled()) {
            if (Math.random() * 100 <= plugin.configController.getConfiguration().getGangsterMobsChanceToSpawn()) {
                AsyncCalculationThread.pendingSpawnRequests.add(new CalculationRequest(event.getChunk(), SpawnRequestType.GANGSTER, plugin.configController.getConfiguration().getGangsterMobsAmountPlaceOnChunkLoad()));
            }
        }

        if (plugin.configController.getConfiguration().isPoliceOfficersEnabled()) {
            if (Math.random() * 100 <= plugin.configController.getConfiguration().getPoliceOfficersChanceToSpawn()) {
                AsyncCalculationThread.pendingSpawnRequests.add(new CalculationRequest(event.getChunk(), SpawnRequestType.POLICEOFFICER, plugin.configController.getConfiguration().getPoliceOfficersAmountPlaceOnChunkLoad()));
            }
        }
    }

}
