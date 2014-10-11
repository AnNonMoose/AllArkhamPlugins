package me.gtacraft.npccore.struct;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class Configuration {

    @Getter
    @Setter
    private boolean passiveNPCEnabled;
    @Getter
    @Setter
    private double passiveChanceToSpawn;
    @Getter
    @Setter
    private int passiveAmountPlaceOnChunkLoad;

    @Getter
    @Setter
    private boolean gangsterMobsEnabled;
    @Getter
    @Setter
    private double gangsterMobsChanceToSpawn;
    @Getter
    @Setter
    private int gangsterMobsAmountPlaceOnChunkLoad;

    @Getter
    @Setter
    private boolean policeOfficersEnabled;
    @Getter
    @Setter
    private double policeOfficersChanceToSpawn;
    @Getter
    @Setter
    private int policeOfficersAmountPlaceOnChunkLoad;

}
