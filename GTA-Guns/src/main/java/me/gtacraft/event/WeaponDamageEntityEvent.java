package me.gtacraft.event;

import me.gtacraft.gun.BulletData;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Connor on 6/27/14. Designed for the GTA-Guns project.
 */

public class WeaponDamageEntityEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private BulletData bd;
    private Entity entity;

    public WeaponDamageEntityEvent(BulletData bd, Entity entity) {
        this.entity = entity;
        this.bd = bd;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled;

    public Entity getEntity() {
        return entity;
    }

    public BulletData getBulletData() {
        return bd;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
