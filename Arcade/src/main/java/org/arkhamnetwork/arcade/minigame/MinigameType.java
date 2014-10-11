/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame;

import java.util.Arrays;
import lombok.Getter;
import org.arkhamnetwork.arcade.commons.plugin.ArcadeMiniPlugin;
import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;
import org.bukkit.Server;

/**
 *
 * @author devan_000
 */
public enum MinigameType {

    SKYWARS(new SkyWars("SkyWars", "0.1-SNAPSHOT", null,
            Arrays.asList(new String[]{
                "Battle to the death in an island enviroment!",
                "Scavenge for loot around the map",
                "Last player alive wins!"})));

    @Getter
    private final ArcadeMiniPlugin pluginMain;

    private MinigameType(ArcadeMiniPlugin pluginMain) {
        this.pluginMain = pluginMain;
    }

    public ArcadeMiniPlugin start(Server server) {
        pluginMain.setServer(server);
        pluginMain.onEnable();
        pluginMain.postEnable();
        return pluginMain;
    }

    public void end() {
        pluginMain.onDisable();
        pluginMain.postDisable();
    }
}
