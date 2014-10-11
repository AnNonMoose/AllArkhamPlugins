package me.gtacraft.plugins.gangs;

import lombok.Getter;
import lombok.Setter;
import me.gtacraft.plugins.gangs.enumeration.GangRole;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import java.util.HashMap;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class GangMember {

    private static HashMap<String, GangMember> members = new HashMap<>();

    @Getter
    private OfflinePlayer wrapped;
    @Getter
    @Setter
    private GangRole role;
    @Getter
    @Setter
    private Gang gang;
    @Getter
    @Setter
    private boolean gangChat;

    public GangMember(OfflinePlayer player, GangRole role) {
        this.wrapped = player;
        this.role = role;

        this.members.put(player.getName(), this);
    }

    public static GangMember fromPlayer(OfflinePlayer player) {
        return members.get(player.getName());
    }

    public static void forget(GangMember member) {
        members.remove(member.getWrapped().getName());
    }
}
