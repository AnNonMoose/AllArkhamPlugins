package org.arkham.cs.events;

import java.util.HashMap;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldEvent;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.BlockTrail;
import org.arkham.cs.cosmetics.TrailingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MoveListener implements Listener {

	private HashMap<Location, Material> blocks = new HashMap<>();

	public MoveListener(){
		CosmeticSuite cs = CosmeticSuite.getInstance();
		cs.getServer().getPluginManager().registerEvents(this, cs);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event){
		if(event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()){
			return;
		}
		Player player = event.getPlayer();
		final Location loc = player.getLocation().clone();
		if(player.isFlying()){
			TrailingBlock tb = TrailingBlock.get(player);
			if(tb == null){
				return;
			}
			tb.run(player);
			return;
		}
		BlockTrail cb = BlockTrail.get(player);
		if(cb == null){
			return;
		}
		if(loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR){
			return;
		}
		final Block below = loc.getBlock().getRelative(BlockFace.DOWN);
		if(below.getState() instanceof Chest){
			return;
		}
		if(below.getState() instanceof Furnace){
			return;
		}
		if(blocks.containsKey(below.getLocation())){
			return;
		}
		if(below.getType() == Material.WATER || below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA || below.getType() == Material.STATIONARY_WATER){
			return;
		}
		blocks.put(below.getLocation(), below.getType());
		final Material bellow_type = below.getType();
		below.setMetadata("spawned", new FixedMetadataValue(CosmeticSuite.getInstance(), ""));
		below.setType(cb.getDisplay().getType());
		below.setData(cb.getDisplay().getData().getData());
		play(below.getLocation(), below.getType());
		new BukkitRunnable() {
			@Override
			public void run() {
				Location l = loc.getBlock().getRelative(BlockFace.DOWN).getLocation();
				l.getBlock().setType(bellow_type);
				play(l, bellow_type);
				below.removeMetadata("spawned", CosmeticSuite.getInstance());
				blocks.remove(l);
			}
		}.runTaskLater(CosmeticSuite.getInstance(), 20L * 5);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		if(block.hasMetadata("spawned")){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event){
		for(Block block : event.blockList()){
			if(block.hasMetadata("spawned")){
				event.blockList().remove(block);
			}
		}
	}

	public void play(final Location l, final Material m){
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
	public void onShutDown(PluginDisableEvent event){
		Plugin plugin = event.getPlugin();
		if(!CosmeticSuite.getInstance().equals(plugin)){
			return;
		}
		for(Location loc : blocks.keySet()){
			Material type = blocks.get(loc);
			loc.getBlock().setType(type);
			loc.getBlock().removeMetadata("spawned", CosmeticSuite.getInstance());
		}
		blocks.clear();
	}
}