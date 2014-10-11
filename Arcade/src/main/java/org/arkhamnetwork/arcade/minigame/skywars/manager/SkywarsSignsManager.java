/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.manager;

import org.arkhamnetwork.arcade.commons.manager.Manager;
import org.arkhamnetwork.arcade.commons.pregame.PreGameManager;
import org.arkhamnetwork.arcade.commons.utils.DynamicSignUtils;
import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;

/**
 *
 * @author devan_000
 */
public class SkywarsSignsManager extends Manager {

    private final SkyWars plugin;

    private int currentTick = -1;

    public SkywarsSignsManager(SkyWars plugin) {
        this.plugin = plugin;
    }

    public void doGameTick() {
        currentTick++;

        if (plugin.getCurrentStage() != null
                && plugin.getCurrentStage().getId() == 0) {
            if (plugin.hasEnoughPlayersToStart()) {
                // Sign 1
                DynamicSignUtils.makeText(String.valueOf(plugin.getGameTask()
                        .getTimeUntilStart()), PreGameManager
                        .getDynamicSideSign1().clone().add(0.0, -36.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText(String.valueOf(plugin.getGameTask()
                        .getTimeUntilStart()), PreGameManager
                        .getDynamicSideSign1().clone().add(-1.0, -36.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Starting in", PreGameManager
                        .getDynamicSideSign1().clone().add(0.0, -30.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Starting in", PreGameManager
                        .getDynamicSideSign1().clone().add(-1.0, -30.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);

                // Sign 2
                DynamicSignUtils.makeText(String.valueOf(plugin.getGameTask()
                        .getTimeUntilStart()), PreGameManager
                        .getDynamicSideSign2().clone().add(0.0, -36.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText(String.valueOf(plugin.getGameTask()
                        .getTimeUntilStart()), PreGameManager
                        .getDynamicSideSign2().clone().add(1.0, -36.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Starting in", PreGameManager
                        .getDynamicSideSign2().clone().add(0.0, -30.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Starting in", PreGameManager
                        .getDynamicSideSign2().clone().add(1.0, -30.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);

            } else {
                // Sign 1
                DynamicSignUtils.makeText(
                        "Waiting for players...",
                        PreGameManager.getDynamicSideSign1().clone()
                        .add(0.0, -36.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText(
                        "Waiting for players...",
                        PreGameManager.getDynamicSideSign1().clone()
                        .add(-1.0, -36.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("...", PreGameManager
                        .getDynamicSideSign1().clone().add(0.0, -30.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("...", PreGameManager
                        .getDynamicSideSign1().clone().add(-1.0, -30.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);

                // Sign 2
                DynamicSignUtils.makeText(
                        "Waiting for players...",
                        PreGameManager.getDynamicSideSign2().clone()
                        .add(0.0, -36.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText(
                        "Waiting for players...",
                        PreGameManager.getDynamicSideSign2().clone()
                        .add(1.0, -36.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("...", PreGameManager
                        .getDynamicSideSign2().clone().add(0.0, -30.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("...", PreGameManager
                        .getDynamicSideSign2().clone().add(1.0, -30.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
            }

            if (currentTick == 0) {
                // Sign 1
                DynamicSignUtils.makeText("Welcome to", PreGameManager
                        .getDynamicSideSign1().clone().add(-1.0, 0.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("the ArkhamNetwork", PreGameManager
                        .getDynamicSideSign1().clone().add(-1.0, -6.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Arcade", PreGameManager
                        .getDynamicSideSign1().clone().add(-1.0, -12.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("BETA !", PreGameManager
                        .getDynamicSideSign1().clone().add(-1.0, -18.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Welcome to",
                        PreGameManager.getDynamicSideSign1(),
                        PreGameManager.getDynamicSideSign1Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("the ArkhamNetwork", PreGameManager
                        .getDynamicSideSign1().clone().add(0.0, -6.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Arcade", PreGameManager
                        .getDynamicSideSign1().clone().add(0.0, -12.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("BETA !", PreGameManager
                        .getDynamicSideSign1().clone().add(0.0, -18.0, 0.0),
                        PreGameManager.getDynamicSideSign1Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);

                // Sign 2
                DynamicSignUtils.makeText("Welcome to",
                        PreGameManager.getDynamicSideSign2(),
                        PreGameManager.getDynamicSideSign2Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("the ArkhamNetwork", PreGameManager
                        .getDynamicSideSign2().clone().add(0.0, -6.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Arcade", PreGameManager
                        .getDynamicSideSign2().clone().add(0.0, -12.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("BETA !", PreGameManager
                        .getDynamicSideSign2().clone().add(0.0, -18.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 159,
                        (byte) 1, DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Welcome to", PreGameManager
                        .getDynamicSideSign2().clone().add(1.0, 0.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("the ArkhamNetwork", PreGameManager
                        .getDynamicSideSign2().clone().add(1.0, -6.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("Arcade", PreGameManager
                        .getDynamicSideSign2().clone().add(1.0, -12.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                DynamicSignUtils.makeText("BETA !", PreGameManager
                        .getDynamicSideSign2().clone().add(1.0, -18.0, 0.0),
                        PreGameManager.getDynamicSideSign2Face(), 89, (byte) 1,
                        DynamicSignUtils.TextAlign.CENTER, true);
                return;
            }
        }
    }

}
