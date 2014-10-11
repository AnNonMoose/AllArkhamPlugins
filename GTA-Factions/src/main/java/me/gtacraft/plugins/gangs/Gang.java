package me.gtacraft.plugins.gangs;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.UUID;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class Gang {

    @Getter
    @Setter
    private Location hideout;
    @Getter
    private List<OfflinePlayer> allMembers;
    @Getter
    private List<GangMember> onlineMembers = Lists.newArrayList();
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private boolean friendlyFire = false;

    public Gang(List<OfflinePlayer> members, String name, int friendlyFire) {
        this.allMembers = members;
        this.name = name;
        this.friendlyFire = (friendlyFire == 1 ? true : false);
    }
}
