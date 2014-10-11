/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.kit;

import java.util.List;
import lombok.Getter;
import org.arkhamnetwork.arcade.commons.podium.Podium;
import org.arkhamnetwork.arcade.commons.utils.DescriptionUtils;
import org.arkhamnetwork.arcade.commons.utils.InventoryUtils;
import org.arkhamnetwork.arcade.commons.utils.MessageUtils;
import org.arkhamnetwork.arcade.commons.utils.MobUtils;
import org.arkhamnetwork.arcade.minigame.skywars.struct.SkywarsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author devan_000
 */
public class KitShop {

    @Getter
    private Podium podium;
    @Getter
    private Kit kit;
    @Getter
    private Villager villager;
    @Getter
    private List<String> kitDescription;

    public KitShop(Podium podium, Kit kit) {
        this.podium = podium;
        this.kit = kit;

        this.kitDescription = DescriptionUtils.buildKitDescriptionMessage(kit);

        spawnVillager();
    }

    private void spawnVillager() {
        villager = MobUtils.spawnVillager(podium.getLocation(), true);

        StringBuilder nameBuilder = new StringBuilder();

        //Premium prefix
        if (!kit.getRanksWithPermission().isEmpty()) {
            nameBuilder.append(ChatColor.AQUA).append("").append(ChatColor.BOLD).append("Premium").append(ChatColor.WHITE).append(" - ");
        }

        //Kit name color statements price based.
        //If its above $0 its always going to be red.
        if (kit.getCreditCost() > 0) {
            nameBuilder.append(ChatColor.RED).append("").append(ChatColor.BOLD);
        } else if (kit.getCreditCost() <= 0 && kit.getRanksWithPermission().isEmpty()) {
            //Everyone has access
            nameBuilder.append(ChatColor.YELLOW);
        } else if (kit.getCreditCost() <= 0 && !kit.getRanksWithPermission().isEmpty()) {
            //Ranks have access kit its not $0.
            nameBuilder.append(ChatColor.BLUE);
        }

        nameBuilder.append(kit.getName());

        //Price tag.
        if (kit.getCreditCost() > 0) {
            nameBuilder.append(ChatColor.WHITE).append(" ").append(ChatColor.UNDERLINE).append(kit.getCreditCost()).append(" Credits");
        }

        villager.setCustomName(nameBuilder.toString());
        villager.setCustomNameVisible(true);
        villager.setAdult();
        villager.setBreed(false);
        villager.setCanPickupItems(false);
        villager.setFireTicks(0);
    }
}
