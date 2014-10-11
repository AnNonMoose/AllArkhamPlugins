package org.arkham.cs.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.gui.Category;
import org.arkham.cs.gui.ClickableItem;
import org.arkham.cs.gui.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Button {


	public abstract void onClick(Player player);

	private int slot, id;
	private Category cat;
	private ItemStack item;
	private String permission;
	private static HashMap<Category, HashMap<ItemStack, Button>> buttons = new HashMap<>();
	public static HashMap<String, Button> buttonPerms = new HashMap<>();
	public static ArrayList<Button> allButtons = new ArrayList<>();

	public Button(int slot, Category cat, String permission, ItemStack item){
		this.slot = slot;		
		if(!buttons.isEmpty()){
			id = buttons.size() + 1;
		} else {
			id = 1;
		}
		this.cat = cat;
		this.item = item;
		this.permission = permission;
		HashMap<ItemStack, Button> bs = buttons.get(cat) == null ? new HashMap<ItemStack, Button>() : buttons.get(cat);
		bs.put(item, this);
		allButtons.add(this);
		buttons.put(cat, bs);
		buttonPerms.put(permission, this);
	}

	public String getPermission() {
		return permission;
	}
	
	public ItemStack getDisplay(){
		return item;
	}

	public ClickableItem noPermissionItem(){
		ItemStack item = ItemFactory.create(Material.STAINED_GLASS_PANE, ChatColor.RED + name(getDisplay()), 1, (byte) 14, ChatColor.RED + "You do not own this item");
		return new ClickableItem(item) {
			@Override
			public void doClick(Player player) {		
				CosmeticSuite cs = CosmeticSuite.getInstance();
				FileConfiguration config = cs.getConfig();
				String link = config.getString("buy-link", CosmeticSuite.PREFIX + "Purchase this rank at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org");
				link = ChatColor.translateAlternateColorCodes('&', link);
				player.sendMessage(link);
				player.closeInventory();
			}
		};
	}

	private String name(ItemStack item){
		return "Locked";
	}

	public int getSlot(){
		return slot;
	}

	public void setSlot(int slot){
		this.slot = slot;
	}

	public int getId(){
		return id;
	}
	
	public void setItem(ItemStack item){
		this.item = item;
		HashMap<ItemStack, Button> bs = buttons.get(cat);
		bs.put(item, this);
		buttons.put(cat, bs);
	}

	public Category getCategory() {
		return cat;
	}

	public static Button getButton(Category cat, ItemStack name){
		return buttons.get(cat).get(name);
	}

	public static Button fromPermission(String perm){
		return buttonPerms.get(perm);
	}

	public static String serialze(List<Button> buttons){
		StringBuilder builder = new StringBuilder();
		int buttonsSize = buttons.size();
		for(int i = 0; i < buttonsSize; i++){
			Button button = buttons.get(i);
			builder.append(button.getPermission() + ",");
		}
		return builder.toString();
	}

	public static List<Button> deserialize(String permissions){
		if(buttonPerms.isEmpty()){
			System.out.println("ButtonPerms is empty, filling it");
			for(Button button : allButtons){
				buttonPerms.put(button.getPermission(), button);
			}
		}
		if(!permissions.contains(",") ){
			System.out.println("No comma, must be " + permissions);
			return new ArrayList<>();
		}
		String[] str = permissions.split(",");
		List<Button> buttons = new ArrayList<>();
		for(int i = 0; i < str.length; i++){
			String perm = str[i];
			Button button = fromPermission(perm);
			buttons.add(button);
		}
		return buttons;
	}

}
