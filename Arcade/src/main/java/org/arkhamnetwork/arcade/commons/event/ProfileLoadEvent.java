/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.event;

import lombok.Getter;
import org.arkhamnetwork.arcade.commons.userstorage.PlayerProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author devan_000
 */
public class ProfileLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Getter
    private final PlayerProfile profile;

    public ProfileLoadEvent(PlayerProfile profile) {
        this.profile = profile;
    }

}
