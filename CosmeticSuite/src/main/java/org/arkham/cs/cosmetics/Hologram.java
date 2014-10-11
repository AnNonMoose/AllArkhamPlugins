package org.arkham.cs.cosmetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_7_R4.EntityHorse;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EntityWitherSkull;
import net.minecraft.server.v1_7_R4.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.arkham.cs.CosmeticSuite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Hologram {
	private static final double distance = 0.23;
	private static Player chat_sender = null;

	private static List<Integer> showLine(final Location loc, String text) {
		WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
		final EntityWitherSkull skull = new EntityWitherSkull(world);
		skull.setLocation(loc.getX(), loc.getY() + 1 + 55, loc.getZ(), 0, 0);
		// ((CraftWorld) loc.getWorld()).getHandle().addEntity(skull);
		final PacketPlayOutSpawnEntity packet_skull = new PacketPlayOutSpawnEntity(skull, 66);
		final EntityHorse horse = new EntityHorse(world);
		horse.setLocation(loc.getX(), loc.getY() + 55, loc.getZ(), 0, 0);
		horse.setAge(-1700000);
		horse.setCustomName(text);
		horse.setCustomNameVisible(true);
		final PacketPlayOutSpawnEntityLiving packedt = new PacketPlayOutSpawnEntityLiving(horse);
		CosmeticSuite.getInstance().getServer().getScheduler().runTaskAsynchronously(CosmeticSuite.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Player player : CosmeticSuite.getInstance().getServer().getOnlinePlayers()) {
					EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
					nmsPlayer.playerConnection.sendPacket(packedt);
					nmsPlayer.playerConnection.sendPacket(packet_skull);
					PacketPlayOutAttachEntity pa = new PacketPlayOutAttachEntity(0, horse, skull);
					nmsPlayer.playerConnection.sendPacket(pa);
				}
			}
		});

		return Arrays.asList(skull.getId(), horse.getId());
	}

	private List<String> lines = new ArrayList<String>();
	private final List<Integer> ids = new ArrayList<Integer>();
	private boolean showing = false;

	private Location location;
	private final JavaPlugin plugin;

	public Hologram(JavaPlugin plugin, String... lines) {
		this.lines.addAll(Arrays.asList(lines));
		this.plugin = plugin;
	}

	public Hologram(JavaPlugin plugin, Player chatter, String... lines) {
		this.lines.addAll(Arrays.asList(lines));
		this.plugin = plugin;
		Hologram.chat_sender = chatter;
		if (CosmeticSuite.getInstance().getServer().getPlayer(chat_sender.getUniqueId()) != null) {
			Hologram.chat_sender.setMetadata("hologram", new FixedMetadataValue(CosmeticSuite.getInstance(), this));
		}
	}

	public void change(String... lines) {
		destroy();
		this.lines = Arrays.asList(lines);
		show(this.location);
	}

	public void destroy() {
		if (this.showing == false) {
			this.location = null;
			return;
			/*
			 * try { throw new Exception("Isn't showing!"); } catch (Exception
			 * e) { e.printStackTrace(); }
			 */
		}
		int[] ints = new int[this.ids.size()];
		for (int j = 0; j < ints.length; j++) {
			ints[j] = this.ids.get(j);
		}
		final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ints);

		/*
		 * for(Entity ent : this.location.getWorld().getEntities()){
		 * if(ent.getType() == EntityType.WITHER_SKULL){ //
		 * System.out.println("WITHER_SKULL @ " + ent.getLocation().toString());
		 * if(ent.getLocation().distanceSquared(this.location) <= 4){
		 * ent.remove(); } } }
		 */

		CosmeticSuite.getInstance().getServer().getScheduler().runTaskAsynchronously(CosmeticSuite.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
				}
			}
		});

		this.showing = false;
		this.location = null;
	}

	public void show(Location loc) {
		if (this.showing == true) {
			try {
				throw new Exception("Is already showing!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Location first = loc.clone().add(0, (this.lines.size() / 2) * distance, 0);
		for (int i = 0; i < this.lines.size(); i++) {
			this.ids.addAll(showLine(first.clone(), this.lines.get(i)));
			first.subtract(0, distance, 0);
		}
		this.showing = true;
		this.location = loc;
	}

	public void show(Player pl, Location loc) {
		if (this.showing == true) {
			try {
				throw new Exception("Is already showing!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Location first = loc.clone().add(0, (this.lines.size() / 2) * distance, 0);
		for (int i = 0; i < this.lines.size(); i++) {
			this.ids.addAll(showLine(first.clone(), this.lines.get(i)));
			first.subtract(0, distance, 0);
		}
		this.showing = true;
		this.location = loc;
	}

	public void show(List<Player> lpl, Location loc) {
		if (this.showing == true) {
			try {
				throw new Exception("Is already showing!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Location first = loc.clone().add(0, (this.lines.size() / 2) * distance, 0);
		for (int i = 0; i < this.lines.size(); i++) {
			this.ids.addAll(showLine(first.clone(), this.lines.get(i)));
			first.subtract(0, distance, 0);
		}
		this.showing = true;
		this.location = loc;
	}

	public void show(Location loc, long ticks) {
		show(loc);
		new BukkitRunnable() {
			@Override
			public void run() {
				destroy();
			}
		}.runTaskLater(this.plugin, ticks);
	}
}
