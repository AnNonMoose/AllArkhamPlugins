/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.tasks;

import lombok.Getter;
import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author devan_000
 */
public class SkywarsGameTask extends BukkitRunnable {

    private SkyWars skywars = SkyWars.getSkywars();

    @Getter
    private int timeUntilStart;

    @Override
    public synchronized void run() {

        skywars.getScoreboardManager().doGameTick();
        skywars.getSignsManager().doGameTick();

    }

}
