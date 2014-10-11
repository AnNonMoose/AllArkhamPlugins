package me.gtacraft.plugins.gangs;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.List;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class GangManager {

    @Getter
    private static List<Gang> gangs = Lists.newArrayList();

    public static void insertGang(Gang gang) {
        gangs.add(gang);
    }

    public static void removeGang(Gang gang) {
        gangs.remove(gang);
    }

    public static Gang getGang(OfflinePlayer player) {
        for (Gang g : gangs) {
            if (g.getAllMembers().contains(player))
                return g;
        }

        return null;
    }

    public static Gang getGang(GangMember player) {
        for (Gang g : gangs) {
            if (g.getOnlineMembers().contains(player))
                return g;
        }

        return null;
    }

    public static Gang getGang(String name) {
        for (Gang g : gangs) {
            if (g.getName().equalsIgnoreCase(name))
                return g;
        }

        return null;
    }
}
