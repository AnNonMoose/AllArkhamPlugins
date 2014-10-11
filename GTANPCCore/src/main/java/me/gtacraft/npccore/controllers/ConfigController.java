package me.gtacraft.npccore.controllers;

import lombok.Getter;
import me.gtacraft.npccore.GTANPCCore;
import me.gtacraft.npccore.struct.Configuration;
import me.gtacraft.npccore.struct.Controller;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class ConfigController implements Controller {

    private final GTANPCCore plugin = GTANPCCore.get();

    @Getter
    private Configuration configuration = null;

    @Override
    public void onEnable() {
        plugin.saveDefaultConfig();

        configuration = getConfiguration(plugin.getConfig());
    }

    @Override
    public void onDisable() {

    }

    private Configuration getConfiguration(FileConfiguration configBase) {
        Configuration conf = new Configuration();

        conf.setPassiveNPCEnabled(configBase.getBoolean("passivenpcs.enabled"));
        conf.setPassiveChanceToSpawn(configBase.getDouble("passivenpcs.chancetospawn"));
        conf.setPassiveAmountPlaceOnChunkLoad(configBase.getInt("passivenpcs.onchunkloadplace"));

        conf.setGangsterMobsEnabled(configBase.getBoolean("gangstermobs.enabled"));
        conf.setGangsterMobsChanceToSpawn(configBase.getDouble("gangstermobs.chancetospawn"));
        conf.setGangsterMobsAmountPlaceOnChunkLoad(configBase.getInt("gangstermobs.onchunkloadplace"));

        conf.setPoliceOfficersEnabled(configBase.getBoolean("policeofficers.enabled"));
        conf.setPoliceOfficersChanceToSpawn(configBase.getDouble("policeofficers.chancetospawn"));
        conf.setPoliceOfficersAmountPlaceOnChunkLoad(configBase.getInt("policeofficers.onchunkloadplace"));

        return conf;
    }
}
