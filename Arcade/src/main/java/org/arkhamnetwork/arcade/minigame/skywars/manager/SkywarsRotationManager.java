/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.manager;

import org.arkhamnetwork.arcade.commons.manager.Manager;
import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;

/**
 *
 * @author devan_000
 */
public class SkywarsRotationManager extends Manager {

    private static SkyWars plugin = SkyWars.getSkywars();

    public static int getNextMap() {
        final int lastMap = plugin.getLastPlayedMapID();

        if (plugin.getConfiguredMaps().get(lastMap + 1) == null) {
            // Return 0 which will be the first map.
            return 0;
        } else {
            // The next map is not null, so we choose it.
            return lastMap + 1;
        }
    }

}
