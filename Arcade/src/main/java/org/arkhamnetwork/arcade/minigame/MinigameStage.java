/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author devan_000
 */
public class MinigameStage {

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private int id;

    public MinigameStage(int id, String name) {
        this.name = name;
        this.id = id;
    }

}
