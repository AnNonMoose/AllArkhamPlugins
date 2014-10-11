package org.arkham.cs.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.CustomEffect;
import org.arkham.cs.effects.EffectManager;
import org.arkham.cs.handler.PurchaseHandler;
import org.arkham.cs.interfaces.Button;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIPage {

	private int id;
	private String title;
	private Inventory inv;
	private Category cat;

	private static HashMap<Integer, GUIPage> pages = new HashMap<>();
	private static List<Button> addedButtons = new ArrayList<>();

	public GUIPage(String title, Category cat) {
		this.title = title;
		if (!pages.isEmpty()) {
			this.id = Collections.max(pages.keySet()) + 1;
		} else {
			this.id = 1;
		}
		pages.put(id, this);
		title = ChatColor.translateAlternateColorCodes('&', title);
		this.inv = Bukkit.createInventory(null, 54, title);
		this.cat = cat;
		for (int i = 36; i < inv.getSize() - 9; i++) {
			ItemStack item = ItemFactory.create(Material.STAINED_GLASS_PANE, ChatColor.BLACK + " ", 1, (byte) 15, "noLore");
			inv.setItem(i, item);
		}
		inv.setItem(53, BaseItems.next().getItem());
		inv.setItem(45, BaseItems.back().getItem());
		inv.setItem(46, BaseItems.back().getItem());
		inv.setItem(47, BaseItems.back().getItem());
		inv.setItem(48, BaseItems.back().getItem());
		inv.setItem(53, BaseItems.next().getItem());
		inv.setItem(52, BaseItems.next().getItem());
		inv.setItem(51, BaseItems.next().getItem());
		inv.setItem(50, BaseItems.next().getItem());
		inv.setItem(49, ItemFactory.create(Material.NETHER_STAR, ChatColor.AQUA + ChatColor.BOLD.toString() + "Arkham" + ChatColor.YELLOW  + ChatColor.BOLD.toString()+ "Network"));
	}

	public Category getCategory() {
		return cat;
	}

	public GUIPage prev() {
		return pages.get(id - 1);
	}

	public GUIPage next() {
		return pages.get(id + 1);
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Inventory getInv() {
		return inv;
	}

	public static HashMap<Integer, GUIPage> getPages() {
		return pages;
	}

	public static GUIPage first() {
		return pages.get(1);
	}

	public static GUIPage getCurrent(Player player) {
		if (player.getOpenInventory() == null) {
			return null;
		}
		String title = player.getOpenInventory().getTitle();
		for (GUIPage page : pages.values()) {
			if (page.getTitle().equalsIgnoreCase(title)) {
				return page;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static void addButton(Button button, Category cat, Player player) {
		List<GUIPage> pages = CosmeticSuite.getInstance().getGuiManager().getPages(cat);
		if (addedButtons.contains(button) && cat != Category.EFFECTS) {
			return;
		}
		for (GUIPage page : pages) {
			if (page.getInv().getItem(35) != null && page.getInv().getItem(35).getType() != Material.AIR) {
				continue;
			}
			int firstEmtpy = page.getInv().firstEmpty();
			if (button.getSlot() != firstEmtpy) {
				button.setSlot(firstEmtpy);
			}
			ItemStack display = !PurchaseHandler.hasPurchased(player, button) ? button.noPermissionItem().getItem() : button.getDisplay();
			if (cat == Category.EFFECTS) {
				addedButtons.remove(button);
				EffectManager mngr = CosmeticSuite.getInstance().getEffectManager();
				CustomEffect ce = (CustomEffect) button;
				if (!PurchaseHandler.hasPurchased(player, ce)) {
					display = button.noPermissionItem().getItem();
				} 
				if (mngr.getEffect(player) != null) {
					if (mngr.getEffect(player).getEffect() == ce.getEffect()) {
						display = ItemFactory.create(Material.STAINED_GLASS_PANE, ChatColor.GREEN + "Active Effect", 1, DyeColor.GREEN.getData(), "noLore");
					}
				}
				ce.setItem(display);
				firstEmtpy = ce.getSlot();
			}
			page.getInv().setItem(firstEmtpy, display);
			addedButtons.add(button);
			break;
		}
	}
}
