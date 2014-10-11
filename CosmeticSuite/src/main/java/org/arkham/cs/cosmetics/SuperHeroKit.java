package org.arkham.cs.cosmetics;

import org.arkham.cs.gui.Category;
import org.arkham.cs.handler.KitManager;
import org.arkham.cs.interfaces.Button;
import org.arkham.cs.utils.PlayerMetaDataUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SuperHeroKit extends Button implements GlobalKit {

	private long cooldown = 24;
	private ItemStack[] items;

	public SuperHeroKit(ItemStack item) {
		super(2, Category.KITS, "cosmetics.kits.superhero", item);
		// 2x Gray Dye, 2x LightGray Dye, 2x Cyan Dye, 2x Purple Dye.
		//: 2x Pink Dye, 2x Lime Dye, 2x LightBlue Dye, 2x Magenta Dye, 2x Orange Dye.
		items = new ItemStack[] {
				new ItemStack(Material.INK_SACK, 2, (byte) 8),
				new ItemStack(Material.INK_SACK, 2, (byte) 7),
				new ItemStack(Material.INK_SACK, 2, (byte) 6),
				new ItemStack(Material.INK_SACK, 2, (byte) 5),
				new ItemStack(Material.INK_SACK, 2, (byte) 9),
				new ItemStack(Material.INK_SACK, 2, (byte) 10),
				new ItemStack(Material.INK_SACK, 2, (byte) 12),
				new ItemStack(Material.INK_SACK, 2, (byte) 13),
				new ItemStack(Material.INK_SACK, 2, (byte) 14)
		};
	}

	@Override
	public ItemStack[] getItems() {
		return items;
	}

	@Override
	public void giveItems(Player player) {
		player.getInventory().addItem(items);
	}
	
	@Override
	public long getCooldown(){
		return cooldown;
	}

	@Override
	public void onClick(Player player) {
		PlayerMetaDataUtil.removeFromSwitching(player);
		KitManager.giveKit(player, this);
	}

}
