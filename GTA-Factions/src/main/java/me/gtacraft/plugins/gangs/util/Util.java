package me.gtacraft.plugins.gangs.util;

import com.google.common.collect.Lists;
import me.gtacraft.plugins.gangs.GangMember;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class Util {

    public static String fromMembers(List<OfflinePlayer> members) {
        try {
            String string = "";
            for (OfflinePlayer member : members) {
                string+=member.getUniqueId()+",";
            }

            return string.substring(0, string.length()-1);
        } catch (Exception err) {
            return "";
        }
    }

    public static List<OfflinePlayer> fromString(String string) {
        List<OfflinePlayer> result = Lists.newArrayList();
        for (String split : string.split(",")){
            result.add(Bukkit.getOfflinePlayer(UUID.fromString(split)));
        }

        return result;
    }
}
