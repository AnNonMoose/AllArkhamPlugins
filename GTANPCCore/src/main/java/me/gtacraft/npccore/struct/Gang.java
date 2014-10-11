package me.gtacraft.npccore.struct;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class Gang {

    @Getter
    @Setter
    private List<UUID> members = new ArrayList<UUID>();

    @Getter
    @Setter
    public List<UUID> angryAtPlayers = new ArrayList<UUID>();

    public Gang(List<UUID> members) {
        this.members.addAll(members);
    }

}
