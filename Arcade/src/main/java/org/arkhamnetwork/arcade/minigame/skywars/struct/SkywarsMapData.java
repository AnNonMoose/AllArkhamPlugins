/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.struct;

import lombok.Getter;
import org.arkhamnetwork.arcade.commons.configuration.yaml.ConfigurationSection;
import org.arkhamnetwork.arcade.commons.storage.ArcadeHashMap;

/**
 *
 * @author devan_000
 */
public class SkywarsMapData {

    @Getter
    private String name = null;
    @Getter
    private int minPlayers = 0;
    @Getter
    private int maxPlayers = 0;
    @Getter
    private int timerLengthSeconds = 0;
    @Getter
    private ArcadeHashMap<String, String> authors = new ArcadeHashMap<>();

    public SkywarsMapData(String name, int minPlayers, int maxPlayers,
            int timerLengthSeconds, ConfigurationSection authorSection) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.timerLengthSeconds = timerLengthSeconds;

        for (String authorName : authorSection.getKeys(false)) {
            this.authors.put(authorName, authorSection.getString(authorName));
        }
    }

}
