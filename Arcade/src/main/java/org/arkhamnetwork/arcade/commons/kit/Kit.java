/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.kit;

import java.util.List;
import lombok.Getter;
import org.arkhamnetwork.arcade.commons.userstorage.PlayerRank;
import org.arkhamnetwork.arcade.commons.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author devan_000
 */
public class Kit {

    @Getter
    private String name;
    @Getter
    private List<ItemStack> items;
    @Getter
    private int creditCost;
    @Getter
    private List<PlayerRank> ranksWithPermission;
    @Getter
    private List<PotionEffect> effects;
    @Getter
    private Inventory buyConfirmInventory;

    public Kit(String name, List<ItemStack> items, int creditCost, List<PlayerRank> ranksWithPermission, List<PotionEffect> effects) {
        this.name = name;
        this.items = items;
        this.creditCost = creditCost;
        this.ranksWithPermission = ranksWithPermission;
        this.effects = effects;
        this.buyConfirmInventory = Bukkit.createInventory(null, 54, ChatColor.BLUE + "Shop> " + ChatColor.YELLOW + name);
        InventoryUtils.populateBuyConfirmKitInventory(this.buyConfirmInventory);
    }

}
