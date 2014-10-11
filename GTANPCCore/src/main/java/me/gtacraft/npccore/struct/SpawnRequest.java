package me.gtacraft.npccore.struct;

import lombok.Getter;
import lombok.Setter;
import me.gtacraft.npccore.enumerations.SpawnRequestType;
import org.bukkit.Location;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class SpawnRequest {

    @Getter
    @Setter
    private boolean completed = false;
    @Getter
    private Location location;
    @Getter
    private SpawnRequestType spawnRequestType;

    public SpawnRequest(Location location, SpawnRequestType spawnRequestType) {
        this.location = location;
        this.spawnRequestType = spawnRequestType;
    }

}
