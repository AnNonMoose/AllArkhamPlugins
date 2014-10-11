package me.gtacraft.npccore.struct;

import lombok.Getter;
import lombok.Setter;
import me.gtacraft.npccore.enumerations.SpawnRequestType;
import org.bukkit.Chunk;
import org.bukkit.Location;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class CalculationRequest {

    @Getter
    private Chunk chunk;
    @Getter
    private SpawnRequestType spawnRequestType;
    @Getter
    private int amountToSpawn;

    public CalculationRequest(Chunk chunk, SpawnRequestType spawnRequestType, int amountToSpawn) {
        this.chunk = chunk;
        this.spawnRequestType = spawnRequestType;
        this.amountToSpawn = amountToSpawn;
    }

}
