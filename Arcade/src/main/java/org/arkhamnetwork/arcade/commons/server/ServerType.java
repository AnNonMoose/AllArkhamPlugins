/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.server;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 *
 * @author devan_000
 */
public enum ServerType {

    SINGLE_GAMEMODE(0, ChatColor.WHITE + "Single Gamemode");

    private ServerType(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @Getter
    private int id;
    @Getter
    private String displayName;

}
