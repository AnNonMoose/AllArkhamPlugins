/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.core.listener;

import java.io.IOException;
import java.util.Arrays;
import org.arkhamnetwork.arcade.commons.bungee.BungeecordHook;
import org.arkhamnetwork.arcade.core.Arcade;
import org.arkhamnetwork.arcade.core.configuration.ArcadeWebConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author devan_000
 */
public class LobbySignsListener implements Listener {

    private Arcade plugin = Arcade.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null
                || (event.getClickedBlock().getType() != Material.SIGN
                && event.getClickedBlock().getType() != Material.SIGN_POST && event
                .getClickedBlock().getType() != Material.WALL_SIGN)) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();

        if (sign == null) {
            return;
        }

        if (ArcadeWebConfiguration.getLobbySignLines().size() != 4) {
            return;
        }

        if (ChatColor
                .stripColor(sign.getLine(0))
                .toLowerCase()
                .equals(ArcadeWebConfiguration.getLobbySignLines().get(0)
                        .toLowerCase())
                && ChatColor
                .stripColor(sign.getLine(1))
                .toLowerCase()
                .equals(ArcadeWebConfiguration.getLobbySignLines()
                        .get(1).toLowerCase())
                && ChatColor
                .stripColor(sign.getLine(2))
                .toLowerCase()
                .equals(ArcadeWebConfiguration.getLobbySignLines()
                        .get(2).toLowerCase())
                && ChatColor
                .stripColor(sign.getLine(3))
                .toLowerCase()
                .equals(ArcadeWebConfiguration.getLobbySignLines()
                        .get(3).toLowerCase())) {
            event.getPlayer().sendMessage(
                    ChatColor.GREEN + "[Arcade] " + ChatColor.GRAY
                    + "Attempting to connect you to the lobby...");
            try {
                BungeecordHook.sendPluginMessage(
                        event.getPlayer(),
                        Arrays.asList(new String[]{
                            "Connect",
                            ArcadeWebConfiguration
                            .getBungeeLobbyServerName()}));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
