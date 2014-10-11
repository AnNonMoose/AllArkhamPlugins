package org.arkham.cs.gui;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.BlockTrail;
import org.arkham.cs.cosmetics.CustomEffect;
import org.arkham.cs.utils.PlayerMetaDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BaseItems {

	public static ClickableItem hats() {
		return new ClickableItem(ItemFactory.create(Material.BUCKET, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Hats", ChatColor.AQUA + "Click to open the hat GUI")) {
			@Override
			public void doClick(Player player) {
				PlayerMetaDataUtil.setSwitchPage(player);
				CosmeticSuite.getInstance().getCommand().openHats(player);
			}
		};
	}

	public static ClickableItem effects() {
		return new ClickableItem(ItemFactory.create(Material.PORTAL, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Effects", ChatColor.AQUA + "Click to open the effects GUI")) {
			@Override
			public void doClick(Player player) {
				PlayerMetaDataUtil.setSwitchPage(player);
				CosmeticSuite.getInstance().getCommand().openEffects(player);
			}
		};
	}

	public static ClickableItem back() {
		return new ClickableItem(ItemFactory.create(Material.BLAZE_ROD, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Go Back")) {
			@Override
			public void doClick(Player player) {
				GUIPage cpage = GUIPage.getCurrent(player);
				GUIPage page = cpage.prev();
				if (page == null) {
					player.openInventory(CosmeticSuite.getInstance().getGuiManager().getMain(player));
					return;
				}
				if (cpage.getCategory() != page.getCategory()) {
					player.openInventory(CosmeticSuite.getInstance().getGuiManager().getMain(player));
					return;
				}
				PlayerMetaDataUtil.setSwitchPage(player);
				player.openInventory(page.getInv());
			}
		};
	}

	public static ClickableItem next() {
		return new ClickableItem(ItemFactory.create(Material.ARROW, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Next Page")) {
			@Override
			public void doClick(Player player) {
				GUIPage cpage = GUIPage.getCurrent(player);
				GUIPage page = cpage.next();
				if (page == null) {
					player.openInventory(CosmeticSuite.getInstance().getGuiManager().getMain(player));
					return;
				}
				if (cpage.getCategory() != page.getCategory()) {
					player.openInventory(CosmeticSuite.getInstance().getGuiManager().getMain(player));
					return;
				}
				PlayerMetaDataUtil.setSwitchPage(player);
				player.openInventory(page.getInv());
			}
		};
	}

	public static ClickableItem blocks() {
		return new ClickableItem(ItemFactory.create(Material.SEEDS, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Block Trails", ChatColor.AQUA + "Click to open the Blocks Trails GUI")) {
			@Override
			public void doClick(Player player) {
				PlayerMetaDataUtil.setSwitchPage(player);
				CosmeticSuite.getInstance().getCommand().openWalkingBlocks(player);
			}
		};
	}

	public static ClickableItem kits() {
		return new ClickableItem(ItemFactory.create(Material.DIAMOND_CHESTPLATE, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Kits", ChatColor.AQUA + "Click to open the Kits GUI")) {
			@Override
			public void doClick(Player player) {
				PlayerMetaDataUtil.setSwitchPage(player);
				CosmeticSuite.getInstance().getCommand().openKits(player);
			}
		};
	}

	public static ClickableItem pets() {
		return new ClickableItem(ItemFactory.create(Material.BONE, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Pets", ChatColor.AQUA + "Click to open the Pets GUI")) {
			@Override
			public void doClick(Player player) {
				Bukkit.dispatchCommand(player, "pet select");
			}
		};
	}

	public static ClickableItem itemEdit() {
		return new ClickableItem(ItemFactory.create(Material.BOOK_AND_QUILL, ChatColor.YELLOW + ChatColor.BOLD.toString() + "ItemEdit Command", ChatColor.AQUA + "Click to execute the /itemedit command.")) {
			@Override
			public void doClick(Player player) {
				Bukkit.dispatchCommand(player, "itemedit");
				player.closeInventory();
			}
		};
	}

	public static ClickableItem titleSelect() {
		return new ClickableItem(ItemFactory.create(Material.WRITTEN_BOOK, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Title Command", ChatColor.AQUA + "Click to execute the /title command.")) {
			@Override
			public void doClick(Player player) {
				Bukkit.dispatchCommand(player, "title");
				player.closeInventory();
			}
		};
	}

	public static ClickableItem titleColorEdit() {
		return new ClickableItem(ItemFactory.create(Material.CARPET, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Title Color Command", ChatColor.AQUA + "Click to execute the /titlecolor command.")) {
			@Override
			public void doClick(Player player) {
				Bukkit.dispatchCommand(player, "titlecolor");
				player.closeInventory();
			}
		};
	}

	public static ClickableItem color() {
		return new ClickableItem(ItemFactory.create(Material.INK_SACK, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Color Command", ChatColor.AQUA + "Click to execute the /color command.")) {
			@Override
			public void doClick(Player player) {
				Bukkit.dispatchCommand(player, "color");
				player.closeInventory();
			}
		};
	}

	@SuppressWarnings("deprecation")
	public static ClickableItem portal() {
		return new ClickableItem(ItemFactory.create(Material.getMaterial(119), ChatColor.YELLOW + ChatColor.BOLD.toString() + "Portal Command", ChatColor.AQUA + "Click to execute the /portal command.")) {
			@Override
			public void doClick(Player player) {
				PlayerMetaDataUtil.setSwitchPage(player);
				Bukkit.dispatchCommand(player, "portal");
			}
		};
	}

	@SuppressWarnings("deprecation")
	public static ClickableItem trail(Player player) {
		ItemStack display = ItemFactory.create(Material.STAINED_GLASS_PANE, ChatColor.YELLOW + ChatColor.BOLD.toString() + "No Trail Active", 1, DyeColor.WHITE.getData(), "noLore");
		CustomEffect effect = CosmeticSuite.getInstance().getEffectManager().getEffect(player);
		boolean has = effect != null;
		if (has) {
			display = ItemFactory.create(Material.STAINED_GLASS_PANE, ChatColor.GREEN + ChatColor.BOLD.toString() + effect.getName(), 1, DyeColor.GREEN.getData(), ChatColor.AQUA + "Click to disable your trail.");
		}
		return new ClickableItem(display) {
			@Override
			public void doClick(Player player) {
				// PlayerMetaDataUtil.setSwitchPage(player);
				CosmeticSuite.getInstance().getEffectManager().setEffect(player, null);
				player.sendMessage(CosmeticSuite.PREFIX + "Your trail effect has been disabled.");
				player.closeInventory();
				// Bukkit.dispatchCommand(player, "portal");
			}
		};
	}

	@SuppressWarnings("deprecation")
	public static ClickableItem blockTrails(Player player) {
		ItemStack display = ItemFactory.create(Material.STAINED_GLASS_PANE, ChatColor.YELLOW + ChatColor.BOLD.toString() + "No Block Trail Active", 1, DyeColor.WHITE.getData(), "noLore");
		BlockTrail effect = BlockTrail.get(player);
		boolean has = effect != null;
		if (has) {
			StringBuilder builder = new StringBuilder();
			String name = effect.getPermission();
			name = name.replace("cosmetics.cursedblocks.", "").replace("_", " ");
			if(name.contains(" ")){
				for(String s : name.split(" ")){
					builder.append(s.substring(0, 1).toUpperCase());
					builder.append(s.substring(1).toLowerCase());
					builder.append(" ");
				}
			} else {
				builder.append(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
			}
			display = ItemFactory.create(Material.STAINED_GLASS_PANE, ChatColor.GREEN + ChatColor.BOLD.toString() + builder.toString(), 1, DyeColor.GREEN.getData(), ChatColor.AQUA + "Click to disable your trail.");
		}
		return new ClickableItem(display) {
			@Override
			public void doClick(Player player) {
				// PlayerMetaDataUtil.setSwitchPage(player);
				BlockTrail.blocks.remove(player.getUniqueId());
				player.sendMessage(CosmeticSuite.PREFIX + "Your block trail effect has been disabled.");
				player.closeInventory();
				// Bukkit.dispatchCommand(player, "portal");
			}
		};
	}

	//Global Buffs
	//Diamond Block
	//Click - Purchase Global loot buffs @ website
	public static ClickableItem globalBuff() {
		return new ClickableItem(ItemFactory.create(Material.DIAMOND_BLOCK, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Global Buffs", ChatColor.AQUA + "More treasure, less grind.")) {
			@Override
			public void doClick(Player player) {
				player.closeInventory();
				CosmeticSuite cs = CosmeticSuite.getInstance();
				FileConfiguration config = cs.getConfig();
				String link = config.getString("buy-link", CosmeticSuite.PREFIX + "Purchase Global Loot Buffs at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org");
				link = ChatColor.translateAlternateColorCodes('&', link);
				player.sendMessage(link);
			}
		};
	}
}