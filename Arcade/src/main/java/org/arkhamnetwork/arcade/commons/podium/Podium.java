/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.podium;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 *
 * @author devan_000
 */
public class Podium {
    
    @Getter
    private Location location;
    @Getter
    @Setter
    private boolean occupied = false;
    
    public Podium(Location location) {
        this.location = location;
    }
    
}
