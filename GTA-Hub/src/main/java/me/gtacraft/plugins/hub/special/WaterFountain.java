package me.gtacraft.plugins.hub.special;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Connor on 7/11/14. Designed for the GTA-Hub project.
 */

public class WaterFountain {

    private List<Location> water_locs = Lists.newArrayList();

    private List<FallingBlock> remove = Lists.newArrayList();

    public WaterFountain(List<Location> water_locs) {
        this.water_locs = water_locs;
    }

    public void tick() {
        if (Bukkit.getOnlinePlayers().length == 0)
            return;

        if (remove.size() > 100)
            return;

        for (Location loc : water_locs) {
            if (Math.random()*100 > 90)
                loc.getWorld().playSound(loc, Sound.WATER, 1, 1);
            FallingBlock water = loc.getWorld().spawnFallingBlock(loc, Material.WATER.getId(), (byte)0);
            water.setDropItem(false);
            water.setVelocity(new Vector((Math.random()*0.25)-0.125, 0.5, (Math.random()*0.25)-0.125));
        }
    }

    public boolean handleFallingBlock(FallingBlock block) {
        if (remove.contains(block)) {
            block.remove();
            remove.remove(block);
        }

        return true; //just to let the listener cancel the event
    }
}
