package me.gtacraft.npccore.controllers;

import me.gtacraft.npccore.GTANPCCore;
import me.gtacraft.npccore.struct.Controller;
import me.gtacraft.npccore.threads.AsyncCalculationThread;
import me.gtacraft.npccore.threads.MobSpawnThread;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class ThreadController implements Controller {

    private final GTANPCCore plugin = GTANPCCore.get();

    @Override
    public void onEnable() {
        plugin.aSyncCalculationThread = new AsyncCalculationThread().runTaskAsynchronously(plugin);
        plugin.mobSpawnThread = new MobSpawnThread().runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        plugin.aSyncCalculationThread.cancel();
        plugin.mobSpawnThread.cancel();
    }
}
