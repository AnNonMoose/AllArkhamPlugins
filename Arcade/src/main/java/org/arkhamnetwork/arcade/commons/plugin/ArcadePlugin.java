/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.plugin;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author devan_000
 */
public abstract class ArcadePlugin extends JavaPlugin {

    @Getter
    @Setter
    private String version;
    @Getter
    @Setter
    private String customName;

    private boolean shuttingDown = false;

    public ArcadePlugin(String name, String version) {
        this.customName = name;
        this.version = version;
    }

    public void onEnable() {
        log("Plugin version " + getVersion() + " starting.");
    }

    public void postEnable() {
        log("Plugin version " + getVersion() + " started.");
    }

    public void onDisable() {
        log("Plugin version " + getVersion() + " shutting down.");

    }

    public void postDisable() {
        log("Plugin version " + getVersion() + " shutdown.");
    }

    public void log(String message) {
        getServer().getConsoleSender().sendMessage(
                ChatColor.YELLOW + getCustomName() + ChatColor.BLUE + "> "
                + ChatColor.WHITE + message);
    }

    public void shutdown(String reason) {
        if (!shuttingDown) {
            shuttingDown = Boolean.TRUE;
            getServer().getConsoleSender().sendMessage(
                    ChatColor.YELLOW + getCustomName() + ChatColor.DARK_RED
                    + " - SHUTTING DOWN" + ChatColor.BLUE + "> "
                    + ChatColor.WHITE + reason);
            getServer().shutdown();
        }
    }
}
