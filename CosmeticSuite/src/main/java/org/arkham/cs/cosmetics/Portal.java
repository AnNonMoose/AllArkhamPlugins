package org.arkham.cs.cosmetics;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.arkham.cs.CosmeticSuite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

public class Portal implements Listener {

	private Location loc;
	private Player player;
	public static ArrayList<Location> portals = new ArrayList<>();
	private static HashMap<UUID, Entry<Portal, Portal>> madePortals = new HashMap<>();

	public Portal(Location loc, Player player) {
		if(!checkPerms(player)){
			player.sendMessage(CosmeticSuite.PREFIX + "You cannot place a portal here");
			return;
		}
		Bukkit.getPluginManager().registerEvents(this, CosmeticSuite.getInstance());
		this.loc = loc;
		portals.add(loc);
		loc = loc.clone().add(0, 1, 0);
		this.player = player;
		portals.add(loc);
		Entry<Portal, Portal> ps = madePortals.get(player.getUniqueId());
		if(ps == null){
			ps = new SimpleEntry<>(this, null);
			madePortals.put(player.getUniqueId(), ps);
			return;
		}
		Portal portal1 = madePortals.get(player.getUniqueId()).getKey();
		if(portal1 == null){
			ps.setValue(this);
			madePortals.put(player.getUniqueId(), ps);
			return;
		}
		Portal portal2 = madePortals.get(player.getUniqueId()).getValue();
		if(portal2 != null){
			portal1.getLocation().getBlock().setType(Material.AIR);
			Location p1n = portal1.getLocation().clone().add(0, 1, 0);
			p1n.getBlock().setType(Material.AIR);
			portals.remove(p1n);
			portals.remove(loc);
			portal2.getLocation().getBlock().setType(Material.AIR);
			Location p2n = portal2.getLocation().clone().add(0, 1, 0);
			p2n.getBlock().setType(Material.AIR);
			madePortals.remove(player.getUniqueId());
			portals.remove(loc);
			portals.remove(p2n);	
		}
	}

	public Portal(Player player) {
		this(player.getLocation(), player);
	}

	public Player getPlayer() {
		return player;
	}

	public Location getLocation() {
		return loc;
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

	public void spark(boolean second) {
		if (!checkPerms(player)) {
			return;
		}
		Material portal = Material.STATIONARY_WATER;
		Location above = loc.clone().add(0, 1, 0);
		if(second){
			loc.getBlock().setMetadata("portal-2", new FixedMetadataValue(CosmeticSuite.getInstance(), ""));
			above.getBlock().setMetadata("portal-2", new FixedMetadataValue(CosmeticSuite.getInstance(), ""));
		} else {
			loc.getBlock().setMetadata("portal", new FixedMetadataValue(CosmeticSuite.getInstance(), ""));
			above.getBlock().setMetadata("portal", new FixedMetadataValue(CosmeticSuite.getInstance(), ""));
		}
		above.getBlock().setType(portal);
		portals.add(above);
		loc.getBlock().setType(portal);
	}

	@EventHandler
	public void physics(BlockPhysicsEvent event){
		Block block = event.getBlock();
		if(block.hasMetadata("portal")){
			event.setCancelled(true);
		}
		if(block.hasMetadata("portal-2")){
			event.setCancelled(true);
		}
	}

	
	
}
