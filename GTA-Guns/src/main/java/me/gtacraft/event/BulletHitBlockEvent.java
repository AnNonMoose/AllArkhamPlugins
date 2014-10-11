package me.gtacraft.event;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Connor on 7/7/14. Designed for the GTA-Guns project.
 */

public class BulletHitBlockEvent extends Event implements Cancellable {

    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();
    private Location hit;

    public BulletHitBlockEvent(Location hit) {
        this.hit = hit;
    }

    public Location getLocationHit() { return hit; }

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
