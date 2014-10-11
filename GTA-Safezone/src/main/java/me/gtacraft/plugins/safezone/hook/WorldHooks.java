package me.gtacraft.plugins.safezone.hook;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Connor on 6/27/14. Designed for the GTA-Safezone project.
 */

public class WorldHooks {

    private static WorldGuardPlugin wgp;

    public static void init() {
        wgp = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

    public static ProtectedRegion getRegion(World world, String id) {
        return wgp.getGlobalRegionManager().get(world).getRegion(id);
    }

    public static void setAllowPlayer(ProtectedRegion region, Player player) {
        LocalPlayer local = wgp.wrapPlayer(player);
        DefaultDomain dd = region.getOwners();
        dd.addPlayer(local);
        region.setOwners(dd);
    }

    public static void setDenyPlayer(ProtectedRegion region, Player player) {
        LocalPlayer local = wgp.wrapPlayer(player);
        DefaultDomain dd = region.getOwners();
        dd.removePlayer(local);
        region.setOwners(dd);
    }

    public static boolean containsPoint(Region region, Location check) {
        //short circuit eval
        if (!region.getWorld().equals(check.getWorld()))
            return false;

        Vector weVec = new Vector(check.getX(), check.getY(), check.getZ());
        if (region.contains(weVec))
            return true;

        return false;
    }

    public static List<ProtectedRegion> getAllRegions(World world) {
        RegionManager rm = wgp.getRegionManager(world);
        List<ProtectedRegion> ret = Lists.newArrayList();
        for (ProtectedRegion pr : rm.getRegions().values()) {
            ret.add(pr);
        }
        return ret;
    }

    public static World getWorldManager(ProtectedRegion region) {
        for (World world : Bukkit.getWorlds()) {
            RegionManager possible = wgp.getRegionManager(world);
            if (possible == null)
                continue;

            if (possible.hasRegion(region.getId()))
                return world;
        }
        return null;
    }
}
