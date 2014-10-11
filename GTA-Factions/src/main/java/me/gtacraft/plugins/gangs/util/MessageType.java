package me.gtacraft.plugins.gangs.util;

import lombok.Getter;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public enum MessageType {
    //use variables
    // %p = player
    // %m = message
    // %r = role

    GROUP_CHAT("&f(&a%r%&f) %p% &7: &a%m%"),
    WARNING("&e&lWARNING: &f%m%"),
    INFO("&6&lINFO: &f%m%"),
    SUCCESS("&a&lSUCCESS: &f%m%"),
    ERROR("&c&lERROR: &f%m%");

    @Getter
    private String pattern;

    MessageType(String pattern) {
        this.pattern = pattern;
    }
}