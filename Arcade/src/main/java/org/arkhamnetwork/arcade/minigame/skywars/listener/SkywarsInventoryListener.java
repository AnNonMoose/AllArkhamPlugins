/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.minigame.skywars.listener;

import org.arkhamnetwork.arcade.commons.kit.Kit;
import org.arkhamnetwork.arcade.minigame.skywars.SkyWars;
import org.arkhamnetwork.arcade.minigame.skywars.manager.SkywarsPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author devan_000
 */
public class SkywarsInventoryListener implements Listener {

    private SkyWars plugin = SkyWars.getSkywars();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKitInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        //If the game has started.
        if (plugin.getCurrentStage() == null || plugin.getCurrentStage().getId() != 0) {
            return;
        }
        event.setCancelled(true);
        
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory == null || clickedInventory.getTitle() == null) {
            return;
        }

        String title = ChatColor.stripColor(clickedInventory.getTitle());

        //Validate if it is a kit shop or not.
        if (!title.contains("Shop>")) {
            return;
        }

        Player player = ((Player) event.getWhoClicked());

        if (player.getOpenInventory() != null) {
            player.getOpenInventory().close();
        }

        ItemStack clicked = event.getCurrentItem();
        
        //He didnt want to confirm.
        if (clicked != null && clicked.getType() != Material.EMERALD_BLOCK) {
            return;
        }
        
        Kit clickedKit = plugin.getKits().get(title.replace("Shop> ", ""));
        if (clickedKit != null) {
            SkywarsPlayerManager.attemptSelectKit(clickedKit, plugin.getPlayers().get(player.getUniqueId()), true);
        }
    }
}
