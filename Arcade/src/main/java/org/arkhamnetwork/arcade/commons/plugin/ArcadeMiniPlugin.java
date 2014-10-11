/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.plugin;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

/**
 *
 * @author devan_000
 */
public abstract class ArcadeMiniPlugin {

    @Getter
    @Setter
    private String version;
    @Getter
    @Setter
    private String customName;
    @Getter
    @Setter
    private Server server;
    @Getter
    @Setter
    private List<String> pluginDescription;

    private boolean shuttingDown = false;

    public ArcadeMiniPlugin(String name, String version, Server pluginServer,
            List<String> pluginDescription) {
        this.customName = name;
        this.version = version;
        this.server = pluginServer;
        this.pluginDescription = pluginDescription;
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
        server.getConsoleSender().sendMessage(
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

    public void broadcast(String message) {
        server.broadcastMessage(ChatColor.YELLOW + getCustomName()
                + ChatColor.BLUE + "> " + ChatColor.WHITE + message);
    }

    public void messagePlayer(Player player, String message) {
        player.sendMessage(ChatColor.YELLOW + getCustomName()
                + ChatColor.BLUE + "> " + ChatColor.WHITE + message);
    }
}
