package me.gtacraft.plugins.gangs.util;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class Var {

    @Getter
    @Setter
    private String key;
    @Getter
    @Setter
    private String value;

    public Var(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Var make(String key, String value) {
        return new Var(key, value);
    }
}
