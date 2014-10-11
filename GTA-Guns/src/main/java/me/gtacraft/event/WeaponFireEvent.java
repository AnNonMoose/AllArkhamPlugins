package me.gtacraft.event;

import me.gtacraft.gun.BulletData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Connor on 6/12/14. Designed for the GTA-Guns project.
 */

public class WeaponFireEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private BulletData data;

    public WeaponFireEvent(BulletData data) {
        this.data = data;
    }

    public BulletData getBulletData() {
        return data;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
