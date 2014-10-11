/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.manager;

import org.arkhamnetwork.arcade.commons.manager.Manager;
import org.arkhamnetwork.arcade.commons.scoreboard.ArcadeScoreboard;
import org.arkhamnetwork.arcade.core.configuration.ArcadeConfiguration;
import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;
import org.arkhamnetwork.arcade.minigame.skywars.struct.SkywarsPlayer;
import org.bukkit.ChatColor;

/**
 *
 * @author devan_000
 */
public class SkywarsScoreboardManager extends Manager {

    private final SkyWars plugin;

    public SkywarsScoreboardManager(SkyWars plugin) {
        this.plugin = plugin;
    }

    public void doGameTick() {
        if (plugin.getCurrentStage() == null) {
            return;
        }

        for (SkywarsPlayer player : plugin.getPlayers().values()) {
            ArcadeScoreboard currentScoreboard = player.getScoreboard();
            if (plugin.getCurrentStage().getId() == 0) {
                if (!plugin.hasEnoughPlayersToStart()) {
                    currentScoreboard.setName("&aWaiting for players...");
                } else {
                    currentScoreboard.setName("&aStarting in &f"
                            + plugin.getGameTask().getTimeUntilStart());
                }
                if (!currentScoreboard.hasLine(0)) {
                    currentScoreboard.addLine(0, " ", 15);
                }
                if (!currentScoreboard.hasLine(1)) {
                    currentScoreboard.addLine(1, "&ePlayers&9>", 14);
                }
                if (!currentScoreboard.hasLine(2)) {
                    currentScoreboard.addLine(
                            2,
                            String.valueOf(ChatColor.WHITE
                                    + ""
                                    + plugin.getPlayers().size()
                                    + ChatColor.WHITE
                                    + "/"
                                    + ChatColor.WHITE
                                    + ""
                                    + plugin.getCurrentMapData()
                                    .getMaxPlayers()), 13);
                } else {
                    currentScoreboard.updateLine(
                            2,
                            String.valueOf(ChatColor.WHITE
                                    + ""
                                    + plugin.getPlayers().size()
                                    + ChatColor.WHITE
                                    + "/"
                                    + ChatColor.WHITE
                                    + ""
                                    + plugin.getCurrentMapData()
                                    .getMaxPlayers()), 13);
                }
                if (!currentScoreboard.hasLine(3)) {
                    currentScoreboard.addLine(3, "  ", 12);
                }
                if (!currentScoreboard.hasLine(4)) {
                    currentScoreboard.addLine(4, "&eScore&9>", 11);
                }
                if (!currentScoreboard.hasLine(5)) {
                    if (player.getUserProfile() == null) {
                        currentScoreboard.addLine(5, ChatColor.GRAY
                                + "Fetching...", 10);
                    } else {
                        currentScoreboard.addLine(
                                5,
                                ChatColor.WHITE
                                + String.valueOf(player
                                        .getUserProfile().getScore()),
                                10);
                    }
                } else {
                    if (player.getUserProfile() == null) {
                        currentScoreboard.updateLine(5, ChatColor.GRAY
                                + "Fetching...", 10);
                    } else {
                        currentScoreboard.updateLine(
                                5,
                                ChatColor.WHITE
                                + String.valueOf(player
                                        .getUserProfile().getScore()),
                                10);
                    }
                }
                if (!currentScoreboard.hasLine(6)) {
                    currentScoreboard.addLine(6, ChatColor.YELLOW + "   ", 9);
                }

                if (!currentScoreboard.hasLine(7)) {
                    currentScoreboard.addLine(7, "&eCredits&9>", 8);
                }
                if (!currentScoreboard.hasLine(8)) {
                    if (player.getUserProfile() == null) {
                        currentScoreboard.addLine(8, ChatColor.GRAY
                                + "Fetching... ", 7);
                    } else {
                        currentScoreboard.addLine(
                                8,
                                ChatColor.WHITE
                                + String.valueOf(player
                                        .getUserProfile()
                                        .getArcadeCreditBalance())
                                + " ", 7);
                    }
                } else {
                    if (player.getUserProfile() == null) {
                        currentScoreboard.updateLine(8, ChatColor.GRAY
                                + "Fetching... ", 7);
                    } else {
                        currentScoreboard.updateLine(
                                8,
                                ChatColor.WHITE
                                + String.valueOf(player
                                        .getUserProfile()
                                        .getArcadeCreditBalance())
                                + " ", 7);
                    }
                }

                if (!currentScoreboard.hasLine(9)) {
                    currentScoreboard.addLine(9, ChatColor.YELLOW + "     ", 6);
                }

                if (!currentScoreboard.hasLine(10)) {
                    currentScoreboard.addLine(10, "&eMap&9>", 5);
                }
                if (!currentScoreboard.hasLine(11)) {
                    currentScoreboard.addLine(11, plugin.getCurrentMapData()
                            .getName(), 4);
                } else {
                    currentScoreboard.updateLine(11, plugin.getCurrentMapData()
                            .getName(), 4);
                }

                if (!currentScoreboard.hasLine(12)) {
                    currentScoreboard.addLine(12, ChatColor.YELLOW + "    ", 3);
                }
                if (!currentScoreboard.hasLine(13)) {
                    currentScoreboard.addLine(13, "&eServer&9>", 2);
                }
                if (!currentScoreboard.hasLine(14)) {
                    currentScoreboard.addLine(14, ChatColor.WHITE
                            + ArcadeConfiguration.getServerName(), 1);
                }
            }
        }
    }

}
