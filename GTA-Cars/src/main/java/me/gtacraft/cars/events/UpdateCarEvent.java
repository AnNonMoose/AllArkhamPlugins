package me.gtacraft.cars.events;

import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

/**
 * Created by tacticalsk8er on 4/28/14.
 */
public class UpdateCarEvent extends VehicleUpdateEvent {

    private Vehicle vehicle;
    private Vector movement;
    private Player p;
    private boolean nos;

    public UpdateCarEvent(Vehicle vehicle, Vector movement, Player p, boolean nos) {
        super(vehicle);
        this.vehicle = vehicle;
        this.movement = movement;
        this.p = p;
        this.nos = nos;
    }

    public Vehicle getCar() {
        return vehicle;
    }

    public Vector getMovement() {
        return movement;
    }

    public Player getPlayer() {
        return p;
    }

    public boolean isNos() {
        return nos;
    }
}
