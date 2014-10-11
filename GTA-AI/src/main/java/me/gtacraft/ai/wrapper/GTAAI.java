package me.gtacraft.ai.wrapper;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

/**
 * Created by Connor on 6/20/14. Designed for the GTA-AI project.
 */

public class GTAAI {

    private Location birth;
    private NPC wrapped;

    public GTAAI(NPC wrapped, Location init) {
        this.wrapped = wrapped;
        this.birth = init;
    }

    public void spawn() {
        wrapped.spawn(birth);
    }

    public NPC getNPCEntity() {
        return wrapped;
    }
}
