/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author devan_000
 */
public class InventoryUtils {

    public static int getInventorySize(int itemsSize) {
        if (itemsSize <= 9) {
            return 9;
        } else if (itemsSize <= 18) {
            return 18;
        } else if (itemsSize <= 27) {
            return 27;
        } else if (itemsSize <= 36) {
            return 36;
        } else if (itemsSize <= 45) {
            return 45;
        } else if (itemsSize <= 54) {
            return 54;
        }
        return 54;
    }

    public static void populateBuyConfirmKitInventory(Inventory inventory) {
        ItemStack confirm = ItemUtils.parseItemFromString("133 1 name:&a&lConfirm_Purchase lore:&7Click_this_to_confirm_and_purchase_the_kit.");
        ItemStack deny = ItemUtils.parseItemFromString("152 1 name:&4&lCancel_Purchase lore:&7Click_this_to_cancel_the_purchase.");

        inventory.setItem(18, confirm);
        inventory.setItem(19, confirm);
        inventory.setItem(20, confirm);
        inventory.setItem(27, confirm);
        inventory.setItem(28, confirm);
        inventory.setItem(29, confirm);
        inventory.setItem(36, confirm);
        inventory.setItem(37, confirm);
        inventory.setItem(38, confirm);
        
        inventory.setItem(24, deny);
        inventory.setItem(25, deny);
        inventory.setItem(26, deny);
        inventory.setItem(33, deny);
        inventory.setItem(34, deny);
        inventory.setItem(35, deny);
        inventory.setItem(42, deny);
        inventory.setItem(43, deny);
        inventory.setItem(44, deny);
    }

}
