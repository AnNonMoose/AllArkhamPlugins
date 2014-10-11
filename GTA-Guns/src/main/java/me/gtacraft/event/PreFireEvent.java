package me.gtacraft.event;

import me.gtacraft.player.GunHolder;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Connor on 6/12/14. Designed for the GTA-Guns project.
 */

public class PreFireEvent extends Event implements Cancellable {

    private boolean cancelled;
    private boolean isGrenade;
    private static final HandlerList handlers = new HandlerList();
    private Location from;
    private GunHolder shooter;

    public PreFireEvent(Location firedAt, GunHolder shooter, boolean isGrenade) {
        this.from = firedAt;
        this.shooter = shooter;
    }

    public boolean isGrenade() { return isGrenade; }

    public GunHolder getShooter() { return shooter; }

    public Location getFiredFrom() {
        return from;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
