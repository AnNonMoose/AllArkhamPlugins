/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.userstorage;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 *
 * @author devan_000
 */
public enum PlayerRank {

    OWNER(0, ChatColor.DARK_RED, "Owner", "&4[Owner] ", false, 0.0, false, 0, false),
    DEVELOPER(1, ChatColor.RED, "Developer", "&b[Developer]", false, 0.0, false, 0, false),
    ADMIN(2, ChatColor.RED, "Admin", "&c[Admin]", false, 0.0, false, 0, false),
    MODERATOR(3, ChatColor.DARK_PURPLE, "Moderator", "&5[Moderator]", false, 0.0, false, 0, false),
    HELPER(4, ChatColor.DARK_AQUA, "Helper", "&3[Helper]", false, 0.0, false, 0, false),
    HAWKFIRE(5, ChatColor.GREEN, "Hawkfire", "&7[Hawkfire]", true, 5.0, true, 30, true),
    JOKER(5, ChatColor.AQUA, "Joker", "&d[Joker]", true, 15.0, true, 60, true),
    ROBIN(5, ChatColor.GOLD, "Robin", "&e[Robin]", true, 30.0, true, 90, true),
    BATMAN(5, ChatColor.YELLOW, "Batman", "&8[Batman]", true, 50.0, false, 0, true);

    private PlayerRank(int id, ChatColor chatColor, String rankName,
            String prefix, boolean canBePurchased, double rankCost,
            boolean subscription, int subscriptionTimeDays, boolean visibleInDescriptions) {
        this.id = id;
        this.chatColor = chatColor;
        this.rankName = rankName;
        this.prefix = prefix;
        this.canBePurchased = canBePurchased;
        this.rankCost = rankCost;
        this.subscription = subscription;
        this.subscriptionTimedays = subscriptionTimeDays;
        this.visibleInDescriptions = visibleInDescriptions;
    }

    @Getter
    private final int id;
    @Getter
    private final ChatColor chatColor;
    @Getter
    private final String rankName;
    @Getter
    private final String prefix;
    @Getter
    private final boolean canBePurchased;
    @Getter
    private final double rankCost;
    @Getter
    private final boolean subscription;
    @Getter
    private final int subscriptionTimedays;
    @Getter
    private final boolean visibleInDescriptions;
}
