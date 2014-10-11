package me.gtacraft.plugins.gangs.enumeration;

import lombok.Getter;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */
public enum GangRole {

    LEADER(2, "**"),
    HOMIE(1, "*"),
    MEMBER(0, "");

    @Getter
    private int permissionLevel;
    @Getter
    private String prefix;

    GangRole(int permissionLevel, String prefix) {
        this.permissionLevel = permissionLevel;
        this.prefix = prefix;
    }

    public static GangRole fromPermissionLevel(int level) {
        for (GangRole role : values()) {
            if (role.getPermissionLevel() == level)
                return role;
        }
        return GangRole.MEMBER;
    }

    public static GangRole fromString(String role) {
        for (GangRole key: values()) {
            if (key.toString().equalsIgnoreCase(role))
                return key;
        }
        return GangRole.MEMBER;
    }

    public String toString() {
        return name().substring(0, 1).toUpperCase()+name().substring(1).toLowerCase();
    }
}
