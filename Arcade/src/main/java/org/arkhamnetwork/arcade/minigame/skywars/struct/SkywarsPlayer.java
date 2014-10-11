/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.struct;

import lombok.Getter;
import lombok.Setter;
import org.arkhamnetwork.arcade.commons.async.Callback;
import org.arkhamnetwork.arcade.commons.kit.Kit;
import org.arkhamnetwork.arcade.commons.scoreboard.ArcadeScoreboard;
import org.arkhamnetwork.arcade.commons.userstorage.PlayerProfile;
import org.arkhamnetwork.arcade.commons.userstorage.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

/**
 *
 * @author devan_000
 */
public class SkywarsPlayer {

    @Getter
    private Player bukkitPlayer;
    @Getter
    private PlayerProfile userProfile = null;
    @Getter
    private ArcadeScoreboard scoreboard = null;
    @Getter
    @Setter
    private Kit kit = null;

    public SkywarsPlayer(final Player player) {
        bukkitPlayer = player;

        final long startingTime = System.currentTimeMillis();
        UserManager.registerUser(player.getUniqueId(),
                new Callback<PlayerProfile>() {
                    @Override
                    public void done(PlayerProfile done) {
                        if (player != null && player.isOnline()) {
                            userProfile = done;
                            player.sendMessage(ChatColor.GREEN
                                    + "[Arcade] "
                                    + ChatColor.GRAY
                                    + "Loaded your playerdata in "
                                    + ChatColor.GREEN
                                    + (System.currentTimeMillis() - startingTime)
                                    + " ms" + ChatColor.GRAY + ".");
                        }
                    }
                });

        scoreboard = new ArcadeScoreboard();
        scoreboard.setSlot(DisplaySlot.SIDEBAR);
        scoreboard.setForPlayer(player);
    }

}
