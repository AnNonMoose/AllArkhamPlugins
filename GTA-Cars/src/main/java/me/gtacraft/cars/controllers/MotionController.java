package me.gtacraft.cars.controllers;

import me.gtacraft.cars.events.UpdateCarEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by tacticalsk8er on 4/28/14.
 */
public class MotionController {

    public static void moveCar(Player p, float forward, float sideways) {
        Vector vector;
        Entity entity = p.getVehicle();

        if (entity == null) {
            return;
        }

        if (!(entity instanceof Minecart)) {
            return;
        }

        Minecart car = (Minecart) entity;

        Vector playerDirection = p.getEyeLocation().getDirection();
        if (forward == 0) {
            return;
        }

        boolean forwards;
        if (forward > 0) {
            forwards = true;
        } else {
            forwards = false;
        }

        double x = playerDirection.getX();
        double y = -0.35; //Basic gravity
        double z = playerDirection.getZ();
        if(!forwards) {
            x = 0 - x;
            z = 0 - z;
        }

        vector = new Vector(x, y, z);
        if (car.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.RAILS))
            vector = vector.multiply(2);
        Bukkit.getPluginManager().callEvent(new UpdateCarEvent(car, vector, p, false));
    }
}
