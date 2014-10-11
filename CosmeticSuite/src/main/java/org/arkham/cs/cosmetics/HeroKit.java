package org.arkham.cs.cosmetics;

import org.arkham.cs.gui.Category;
import org.arkham.cs.handler.KitManager;
import org.arkham.cs.interfaces.Button;
import org.arkham.cs.utils.PlayerMetaDataUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeroKit extends Button implements GlobalKit {

	private long cooldown = 24;
	private ItemStack[] items;

	public HeroKit(ItemStack item) {
		super(0, Category.KITS, "cosmetics.kits.hero", item);
		// 2x Gray Dye, 2x LightGray Dye, 2x Cyan Dye, 2x Purple Dye.
		items = new ItemStack[] {
				new ItemStack(Material.INK_SACK, 2, (byte) 8),
				new ItemStack(Material.INK_SACK, 2, (byte) 7),
				new ItemStack(Material.INK_SACK, 2, (byte) 6),
				new ItemStack(Material.INK_SACK, 2, (byte) 5)
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