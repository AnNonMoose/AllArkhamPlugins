package me.gtacraft.event;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Connor on 6/12/14. Designed for the GTA-Guns project.
 */

public class SpecialFireEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Projectile proj; private Item item;
    private Location from;

    public SpecialFireEvent(Location firedAt, Projectile proj, Item item) {
        this.from = firedAt;
        this.proj = proj; this.item = item;
    }

    public Location getFiredFrom() {
        return from;
    }

    public Projectile getProjectile() {
        return proj;
    }

    public Item getItem() {
        return item;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
