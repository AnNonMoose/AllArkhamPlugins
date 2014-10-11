package org.arkham.cs.cosmetics;

import java.util.HashMap;
import java.util.UUID;

import org.arkham.cs.CosmeticSuite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

public class PortalLink implements Listener {
	
	private Portal portal_1, portal_2;
	private Player player;
	private static HashMap<UUID, PortalLink> links = new HashMap<>();
	
	public PortalLink(Portal p1, Portal p2, Player player) {
		if(!checkPerms(player)){
			player.sendMessage(CosmeticSuite.PREFIX + "You cannot place a portal here.");
			return;
		}
		this.portal_1 = p1;
		this.portal_2 = p2;
		this.player = player;
		links.put(player.getUniqueId(), this);
		spark();
		player.sendMessage(CosmeticSuite.PREFIX + "You have now linked 2 portal.");
		Bukkit.getPluginManager().registerEvents(this, CosmeticSuite.getInstance());
	}
	
	public void spark(){
		portal_1.spark(false);
		portal_2.spark(true);
	}

	/**
	 * @return the portal_1
	 */
	public Portal getPortal_1() {
		return portal_1;
	}

	/**
	 * @param portal_1 set the portal_1
	 */
	public void setPortal_1(Portal portal_1) {
		this.portal_1 = portal_1;
	}

	/**
	 * @return the portal_2
	 */
	public Portal getPortal_2() {
		return portal_2;
	}

	/**
	 * @param portal_2 set the portal_2
	 */
	public void setPortal_2(Portal portal_2) {
		this.portal_2 = portal_2;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
	
	public boolean checkPerms(Player player) {
		WorldGuardPlugin api = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		ApplicableRegionSet regions = api.getRegionManager(player.getLocation().getWorld()).getApplicableRegions(player.getLocation());
		LocalPlayer lPlayer = api.wrapPlayer(player);
		if (regions.size() == 0) {
			return true;
		}
		return regions.canBuild(lPlayer);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event){
		if(event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()){
			return;
		}
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		Block block = loc.getBlock();
		if(block.getType() != Material.STATIONARY_WATER){
			return;
		}
		if(block.hasMetadata("portal")){
			player.teleport(portal_2.getLocation().getBlock().getRelative(BlockFace.NORTH).getLocation());
			return;
		}
		if(block.hasMetadata("portal-2")){
			player.teleport(portal_1.getLocation().getBlock().getRelative(BlockFace.NORTH).getLocation());
		}
	}
	
}
