package org.arkham.cs.cosmetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.gui.Category;
import org.arkham.cs.interfaces.Button;
import org.arkham.cs.utils.PlayerMetaDataUtil;
import org.arkham.cs.utils.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Hat extends Button {

	public static List<Hat> hats = new ArrayList<>();
	private Rank rank;
	private static HashMap<Rank, List<Hat>> hatsByRank = new HashMap<>();

	/**
	 * @param slot
	 * @param item
	 * @param name
	 * @param lore
	 */
	public Hat(int slot, ItemStack item, Category name, String permission, Rank rank) {
		super(slot, name, permission, item);
		this.rank = rank;
		List<Hat> h = hatsByRank.get(rank);
		if(h == null){
			h = new ArrayList<>();
		}
		if(rank == Rank.SUPERHERO){
			h.addAll(hatsByRank.get(Rank.HERO));
		}
		h.add(this);
		hatsByRank.put(rank, h);
		hats.add(this);
	}

	/**
	 * 
	 * @param slot
	 * @param item
	 * @param rank
	 * @param permission
	 */
	public Hat(int slot, ItemStack item, Rank rank, String permission){
		this(slot, item, Category.HATS, permission, rank);
	}
	/**
	 * @param mat
	 * @param slot
	 */
	public Hat(Material mat, int slot, Rank rank){
		this(slot, new ItemStack(mat), Category.HATS, "cosmetics.hats." + mat.name().toLowerCase(), rank);
	}

	public static List<Hat> getHats(Rank rank){
		return hatsByRank.get(rank);
	}

	@Override
	public void onClick(final Player player) {
		player.sendMessage(CosmeticSuite.PREFIX + "You have now equiped the hat " + getPermission().replace("cosmetics.hats.", "").replace("_", " "));
		PlayerMetaDataUtil.removeFromSwitching(player);
		player.closeInventory();
		new BukkitRunnable(){
			@Override
			public void run(){
				PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(player.getEntityId(), 4, CraftItemStack.asNMSCopy(getDisplay()));
				for(Player p :  Bukkit.getOnlinePlayers()){
					if(p.getUniqueId().equals(player.getUniqueId())){
						PacketPlayOutEntityEquipment pequip = new PacketPlayOutEntityEquipment(player.getEntityId(), 3, CraftItemStack.asNMSCopy(getDisplay()));
						((CraftPlayer)player).getHandle().playerConnection.sendPacket(pequip);
						continue;
					}
					((CraftPlayer)p).getHandle().playerConnection.sendPacket(equip);
				}
			}
		}.runTaskLater(CosmeticSuite.getInstance(), 20L);
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}
}