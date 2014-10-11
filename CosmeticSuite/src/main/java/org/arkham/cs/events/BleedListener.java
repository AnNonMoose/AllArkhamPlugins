package org.arkham.cs.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldEvent;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.handler.PlayerHandler;
import org.arkham.cs.utils.Rank;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class BleedListener implements Listener {

	private List<Material> leather = new ArrayList<>(Arrays.asList(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS));
	
	public BleedListener(CosmeticSuite cs){
		cs.getServer().getPluginManager().registerEvents(this, cs);
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)){
			return;
		}
		if(!(event.getDamager() instanceof Player)){
			return;
		}
		Player hit = (Player) event.getEntity();
		Player damager = (Player) event.getDamager();
		if(PlayerHandler.getRank(damager) != Rank.SUPERHERO){
			return;
		}
		play(hit.getEyeLocation(), 10, 0.5, Material.REDSTONE, (byte) 0);
	}

	public void play(final Location l, final int particles, final double velMult, final Material m, final byte data) {
		CosmeticSuite.getInstance().getServer().getScheduler().runTaskAsynchronously(CosmeticSuite.getInstance(), new Runnable(){
			@SuppressWarnings("deprecation")
			@Override
			public void run(){
				int particle_id = m.getId();
				Packet particles = new PacketPlayOutWorldEvent(2001, Math.round(l.getBlockX()), Math.round(l.getBlockY()), Math.round(l.getBlockZ()), particle_id, false);
				((CraftServer) CosmeticSuite.getInstance().getServer()).getServer().getPlayerList().sendPacketNearby(l.getBlockX(), l.getBlockY(), l.getBlockZ(), 16, ((CraftWorld) l.getWorld()).getHandle().dimension, particles);
			}
		});
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		event.getDrops().clear();
		for(int i = 0; i < player.getInventory().getContents().length; i++){
			ItemStack item = player.getInventory().getContents()[i];
			if(item == null || item.getType() == Material.AIR){
				continue;
			}
			event.getDrops().add(item);
		}
		for(int i = 0; i < player.getInventory().getArmorContents().length; i++){
			ItemStack item = player.getInventory().getArmorContents()[i];
			if(item == null || item.getType() == Material.AIR){
				continue;
			}
			if(leather.contains(item.getType())){
				String name = item.getType().name().toLowerCase().replace("leather_", "");
				switch(name){
				case "chestplate":
					event.getDrops().add(new ItemStack(Material.DIAMOND_CHESTPLATE));
					break;
				case "leggings":
					event.getDrops().add(new ItemStack(Material.DIAMOND_LEGGINGS));
					break;
				case "helmet":
					event.getDrops().add(new ItemStack(Material.DIAMOND_HELMET));
					break;
				case "boots":
					event.getDrops().add(new ItemStack(Material.DIAMOND_BOOTS));
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event){
		Recipe recipe = event.getRecipe();
		ItemStack result = recipe.getResult();
		Material resultType = result.getType();
		if(!leather.contains(resultType)){
			return;
		}
		LeatherArmorMeta meta = (LeatherArmorMeta) result.getItemMeta();
		if(meta.getColor() != null){
			event.setCancelled(true);
			event.setCurrentItem(null);
			return;
		}
	}
}