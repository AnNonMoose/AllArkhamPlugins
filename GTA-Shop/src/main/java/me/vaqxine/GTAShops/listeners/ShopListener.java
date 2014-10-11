package me.vaqxine.GTAShops.listeners;

import java.util.HashSet;

import me.gtacraft.api.GTAGunsAPI;
import me.gtacraft.economy.EconomyAPI;
import me.gtacraft.event.WeaponDamageEntityEvent;
import me.gtacraft.gun.GunFactory;
import me.gtacraft.util.GunUtil;
import me.vaqxine.GTAShops.GTAShops;
import me.vaqxine.GTAShops.utils.ItemUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {
    
    HashSet<Player> good_customer = new HashSet<Player>();
    
    public static String systemName(String s){
        return ChatColor.stripColor(s.replace(" ", "_"));
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e){
        if(e.getRightClicked() instanceof Player && e.getRightClicked().hasMetadata("NPC")){
            // SHOP?!
            Player p_npc = (Player)e.getRightClicked();
            if(GTAShops.shop_inventories.containsKey(systemName(p_npc.getName()))){
                // Yup.
                Player pl = e.getPlayer();
                pl.openInventory(GTAShops.shop_inventories.get(systemName(p_npc.getName())));
                pl.playSound(pl.getLocation(), Sound.VILLAGER_HAGGLE, 1F, 1F);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e){
        String title = e.getInventory().getTitle();
        if(GTAShops.shop_inventories.containsKey(systemName(title))){
            // SHOP!
            Player pl = (Player)e.getWhoClicked();
            e.setCancelled(true);
            e.setResult(Result.DENY);
            pl.updateInventory();
            
            ItemStack is = e.getCurrentItem().clone();
            if(is == null || is.getType() == Material.AIR) return; // Don't care.
            if(!e.getInventory().equals(pl.getOpenInventory().getTopInventory())) return; // Don't care.
            
            double price = ItemUtils.getPrice(is);
            double balance = EconomyAPI.getUserBalance(pl.getUniqueId());
            
            if(balance < price){
                pl.sendMessage(ChatColor.RED + "You cannot afford a(n) " + ItemUtils.getItemName(is) + "!");
                pl.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Price " + ChatColor.GRAY + "$" + price);
                pl.closeInventory();
                return;
            }
            
            if(price <= 0.0D){
                // Most likely not something they can buy.
                return;
            }
            
            if(pl.getInventory().firstEmpty() == -1) {
                pl.sendMessage(ChatColor.RED + "You don't have any room in your backpack!");
                pl.closeInventory();
                return;
            }
            
            // Take the money.
            EconomyAPI.setUserBalance(pl.getUniqueId(), balance - price);
            pl.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "- $" + price);
            pl.playSound(pl.getLocation(), Sound.VILLAGER_YES, 1F, 1F);
            
            // Give the item. TODO Special case for detecting and giving guns.
            if(GunUtil.isGun(is)){
                // It's a gun.
                String name = ChatColor.stripColor(ItemUtils.getItemName(is));
                GTAGunsAPI.giveGun(pl, GTAGunsAPI.getGunFromName(name));
            } else {
                pl.getInventory().addItem(ItemUtils.removePrice(is));
            }
            
            good_customer.add(pl);
            
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        String title = e.getInventory().getTitle();
        Player pl = (Player)e.getPlayer();
        
        if(good_customer.contains(pl) && GTAShops.shop_inventories.containsKey(systemName(title))){
            pl.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "NPC " + ChatColor.WHITE + title + ": " + ChatColor.GRAY + "Thank you for your business!");
            pl.playSound(pl.getLocation(), Sound.VILLAGER_HAGGLE, 1F, 1F);
            good_customer.remove(pl);
        }
    }
}
