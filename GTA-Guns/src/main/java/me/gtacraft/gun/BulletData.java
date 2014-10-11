package me.gtacraft.gun;

import org.bukkit.Location;
import org.bukkit.entity.Projectile;

import java.util.HashMap;

/**
 * Created by Connor on 4/29/14. Designed for the GTA-Guns project.
 */

public class BulletData {

    private static HashMap<Projectile, BulletData> datMap = new HashMap<Projectile, BulletData>();

    private Location initial;
    private long sysMills;
    private GunData gd;
    private Projectile proj;

    public BulletData(Projectile prj, GunData dat) {
        gd = dat;
        proj = prj;
        initial = prj.getLocation();
        sysMills = System.currentTimeMillis();
        datMap.put(prj, this);
    }

    public static BulletData getBulletData(Projectile proj) {
        return datMap.remove(proj);
    }

    public Location getInitialLocation() {
        return initial;
    }

    public GunData getGunData() {
        return gd;
    }

    public Projectile getProjectile() {
        return proj;
    }

    public double getBulletSpeed() {
        long sysMills = System.currentTimeMillis();
        long diff = sysMills - this.sysMills;
        double time = (double)diff/(double)1000;

        double distance = proj.getLocation().distance(initial);

        return distance/time;
    }
}
