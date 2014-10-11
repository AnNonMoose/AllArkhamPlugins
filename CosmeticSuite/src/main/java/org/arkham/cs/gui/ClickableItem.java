package org.arkham.cs.gui;

import java.util.HashMap;

import org.arkham.cs.interfaces.ClickExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ClickableItem implements ClickExecutor {

	private ItemStack item;
	private static HashMap<ItemStack, ClickableItem> items = new HashMap<>();

	public ClickableItem(ItemStack item){
		this.item = item;
		items.put(item, this);
	}
	
	public ItemStack getItem(){
		return item;
	}
	
	public static ClickableItem fromItem(ItemStack item){
		return items.get(item);
	}

	@Override
	public abstract void doClick(Player player);

}
