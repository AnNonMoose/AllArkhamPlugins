package org.arkham.cs.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.BlockTrail;
import org.arkham.cs.cosmetics.CustomEffect;
import org.arkham.cs.cosmetics.Hat;
import org.arkham.cs.cosmetics.HeroKit;
import org.arkham.cs.cosmetics.SuperHeroKit;
import org.arkham.cs.handler.ParticleLibManager;
import org.arkham.cs.handler.PurchaseHandler;
import org.arkham.cs.handler.ParticleLibManager.FancyEffect;
import org.arkham.cs.interfaces.Button;
import org.arkham.cs.utils.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

public class GUIManager implements Listener {

	private Inventory main;
	private HashMap<Category, List<GUIPage>> pages = new HashMap<>();
	private static HeroKit heroKit;
	private static SuperHeroKit superHeroKit;

	/**
	 * @return the heroKit
	 */
	public HeroKit getHeroKit() {
		return heroKit;
	}

	/**
	 * @return the superHeroKit
	 */
	public SuperHeroKit getSuperHeroKit() {
		return superHeroKit;
	}

	public GUIManager() {
		main = Bukkit.createInventory(null, 27, "Arkham Cosmetics");
		main.setItem(2, BaseItems.blocks().getItem());
		main.setItem(3, BaseItems.hats().getItem());
		main.setItem(4, BaseItems.pets().getItem());
		main.setItem(5, BaseItems.effects().getItem());
		main.setItem(6, BaseItems.kits().getItem());
		for (int i = 9; i < 18; i++) {
			main.setItem(i, ItemFactory.create(Material.STAINED_GLASS_PANE, ChatColor.BLACK + "", 0, (byte) 15, "noLore"));
		}
		main.setItem(23, BaseItems.portal().getItem());
		main.setItem(21, BaseItems.color().getItem());
		main.setItem(20, BaseItems.itemEdit().getItem());
		main.setItem(24, BaseItems.globalBuff().getItem());
		
		main.setItem(19, BaseItems.titleColorEdit().getItem());
		main.setItem(25, BaseItems.titleSelect().getItem());
	}

	public void loadPages() {
		loadPagesFromYML();
		for (Category cat : Category.values()) {
			List<GUIPage> pages = new ArrayList<>();
			for (GUIPage page : GUIPage.getPages().values()) {
				if (page.getCategory() == cat) {
					pages.add(page);
				}
			}
			this.pages.put(cat, pages);
		}
	}

	@SuppressWarnings({ "unused", "deprecation" })
	private void loadPagesFromYML() {
		/**
		 * Dis is the hats, looks ugly, ik.
		 */
		{
			int created = 1;
			new GUIPage(ChatColor.BLACK + "Hats: " + ChatColor.DARK_RED + (1), Category.HATS);
			new GUIPage(ChatColor.BLACK + "Hats: " + ChatColor.DARK_RED + (2), Category.HATS);
			new GUIPage(ChatColor.BLACK + "Hats: " + ChatColor.DARK_RED + (3), Category.HATS);
			new GUIPage(ChatColor.BLACK + "Hats: " + ChatColor.DARK_RED + (4), Category.HATS);
		}

		/**
		 * Dis is effects
		 */
		{
			int created = 1;
			int i = 0;

			new GUIPage("Particle Effects " + 1, Category.EFFECTS);
			for (FancyEffect fancy : FancyEffect.values()) {
				new CustomEffect(i, Category.EFFECTS, fancy, "cosmetics.effects." + fancy.name().toLowerCase(), ItemFactory.create(Material.STAINED_GLASS_PANE, ParticleLibManager.name(fancy), 1, (byte) DyeColor.WHITE.getData(), "noLore"), 0, ParticleLibManager.getRank(fancy), ParticleLibManager.name(fancy));
				i++;
				if (i % 35 == 0) {
					created++;
					new GUIPage("Particle Effects " + created, Category.EFFECTS);
					i = 0;
				}
			}
			CustomEffect.addSuperHeroToHero();
		}
		/**
		 * Dis is Curse blocks
		 */
		{
			int created = 1;
			new GUIPage("Block Trails " + created, Category.CURSE_BLOCKS);
			new GUIPage("Block Trails " + 2, Category.CURSE_BLOCKS);
			new GUIPage("Block Trails " + 3, Category.CURSE_BLOCKS);
		}
		/**
		 * Dis is Kits
		 */
		{
			new GUIPage("Kits", Category.KITS);
		}
	}

	public static void setUp() {
		setUpHeroHats();
		setUpSuperHeroHats();
		setUpHeroCurseBlocks();
		setUpSuperHeroCurseBlocks();
		ItemStack hitem = new ItemStack(Material.IRON_CHESTPLATE);
		ItemMeta meta = hitem.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Kit Hero");
		hitem.setItemMeta(meta);
		heroKit = new HeroKit(hitem);
		ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemMeta hmeta = item.getItemMeta();
		hmeta.setDisplayName(ChatColor.DARK_RED + "Kit SuperHero");
		item.setItemMeta(hmeta);
		superHeroKit = new SuperHeroKit(item);
	}

	public List<GUIPage> getPages(Category cat) {
		return pages.get(cat);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (player.hasMetadata("switchedPages")) {
			return;
		}
		if (player.hasMetadata("inGUI")) {
			player.removeMetadata("inGUI", CosmeticSuite.getInstance());
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (player.hasMetadata("inGUI")) {
			event.setCancelled(true);
			event.setResult(Result.DENY);
		}
		if (event.getInventory() == null) {
			return;
		}
		if (event.getCurrentItem() == null) {
			return;
		}
		ItemStack item = event.getCurrentItem();
		if(item.equals(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15))) {
			return;
		}
		if( item.getType() == Material.NETHER_STAR && !event.getInventory().getTitle().contains("container.crafting") && !event.getInventory().getTitle().contains("Lobby Selector")){
			player.openInventory(getMain(player));
			return;
		}
		if (ClickableItem.fromItem(item) == null) {
			if (GUIPage.getCurrent(player) == null) {
				return;
			}
			Button button = Button.getButton(GUIPage.getCurrent(player).getCategory(), item);
			if (button == null) {
				System.out.println("Button == null");
				return;
			}
			button.onClick(player);
			return;
		}
		ClickableItem cItem = ClickableItem.fromItem(item);
		cItem.doClick((Player) event.getWhoClicked());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PurchaseHandler.setUpPurchases(event.getPlayer());
		CosmeticSuite.getInstance().getChatColorManager().sync(event.getPlayer());
	}

	public Inventory getMain(Player player) {
		main.setItem(22, BaseItems.trail(player).getItem());
		main.setItem(22-9, BaseItems.blockTrails(player).getItem());
		return main;
	}
	
	private static String[] herohats() {
		String stuff = "Dirt, Stone, Grass, Podzol, Cobblestone, Sandstone, Glass, Sand, WoodLogs, Planks, Iron_block, Gold_block, Diamond_block, Emerald_block, Glowstone, Ice, Pumpkin, Clay, Snow_block, diamond_ore, gold_ore, iron_ore, coal_ore, redstone_ore, lapis_ore, emerald_ore, Netherrack, Netherbrick, StoneBrick, Melon_block, Quartz_block, Hay, Coal, PackedIce, Leaves, CraftingTable, Anvil, Enderchest, Furnace, EnchantmentTable, EndFrame, Cactus, Fence, Jukebox, Redstone_block, TnT, Beacon, RedstoneLamp, Dispenser, NoteBlock";
		return stuff.split(", ");
	}

	public static void setUpHeroHats() {
		String[] hats = herohats();
		for (int i = 0; i < hats.length; i++) {
			String s = hats[i];
			s = s.toUpperCase();
			s = s.replace(" ", "_");
			if (s.equalsIgnoreCase("WoodLogs")) {
				ItemStack oak = new ItemStack(Material.LOG);
				ItemStack spruce = new ItemStack(Material.LOG, 1, (byte) 1);
				ItemStack birch = new ItemStack(Material.LOG, 1, (byte) 2);
				ItemStack jungle = new ItemStack(Material.LOG, 1, (byte) 3);
				ItemStack acacia = new ItemStack(Material.LOG, 1, (byte) 4);
				ItemStack dark = new ItemStack(Material.LOG, 1, (byte) 5);
				new Hat(i, oak, Rank.HERO, "cosmetics.hats.oak_log");
				new Hat(i, spruce, Rank.HERO, "cosmetics.hats.spruce_log");
				new Hat(i, birch, Rank.HERO, "cosmetics.hats.birch_log");
				new Hat(i, jungle, Rank.HERO, "cosmetics.hats.jungle_log");
				new Hat(i, acacia, Rank.HERO, "cosmetics.hats.acacia_log");
				new Hat(i, dark, Rank.HERO, "cosmetics.hats.dark_log");
			} else if (s.equalsIgnoreCase("Leaves")) {
				ItemStack oak = new ItemStack(Material.LEAVES);
				ItemStack spruce = new ItemStack(Material.LEAVES, 1, (byte) 1);
				ItemStack birch = new ItemStack(Material.LEAVES, 1, (byte) 2);
				ItemStack jungle = new ItemStack(Material.LEAVES, 1, (byte) 3);
				new Hat(i, oak, Rank.HERO, "cosmetics.hats.oak_leaves");
				new Hat(i, spruce, Rank.HERO, "cosmetics.hats.spruce_leaves");
				new Hat(i, birch, Rank.HERO, "cosmetics.hats.birch_leaves");
				new Hat(i, jungle, Rank.HERO, "cosmetics.hats.jungle_leaves");
			} else if (s.equalsIgnoreCase("Planks")) {
				ItemStack oak = new ItemStack(Material.WOOD);
				ItemStack spruce = new ItemStack(Material.WOOD, 1, (byte) 1);
				ItemStack birch = new ItemStack(Material.WOOD, 1, (byte) 2);
				ItemStack jungle = new ItemStack(Material.WOOD, 1, (byte) 3);
				ItemStack acacia = new ItemStack(Material.WOOD, 1, (byte) 4);
				ItemStack dark = new ItemStack(Material.WOOD, 1, (byte) 5);
				new Hat(i, oak, Rank.HERO, "cosmetics.hats.oak_plank");
				new Hat(i, spruce, Rank.HERO, "cosmetics.hats.spruce_plank");
				new Hat(i, birch, Rank.HERO, "cosmetics.hats.birch_plank");
				new Hat(i, jungle, Rank.HERO, "cosmetics.hats.jungle_plank");
				new Hat(i, acacia, Rank.HERO, "cosmetics.hats.acacia_plank");
				new Hat(i, dark, Rank.HERO, "cosmetics.hats.dark_plank");
			} else if (s.equalsIgnoreCase("Netherbrick")) {
				s = Material.NETHER_BRICK.name();
			} else if (s.equalsIgnoreCase("Redstonelamp")) {
				s = Material.REDSTONE_LAMP_ON.name();
			} else if (s.equalsIgnoreCase("Endframe")) {
				s = Material.ENDER_PORTAL_FRAME.name();
			} else if (s.equalsIgnoreCase("podzol")) {
				ItemStack item = new ItemStack(Material.DIRT, 1, (byte) 2);
				new Hat(i, item, Category.HATS, "cosmetics.hats.podzol", Rank.HERO);
			} else if (s.equalsIgnoreCase("stonebrick")) {
				s = Material.SMOOTH_BRICK.name();
			} else if (s.equalsIgnoreCase("hay")) {
				s = Material.HAY_BLOCK.name();
			} else if (s.equalsIgnoreCase("packedice")) {
				s = Material.PACKED_ICE.name();
			} else if (s.equalsIgnoreCase("craftingtable")) {
				s = Material.WORKBENCH.name();
			} else if (s.equalsIgnoreCase("enderchest")) {
				s = Material.ENDER_CHEST.name();
			} else if (s.equalsIgnoreCase("enchantmenttable")) {
				s = Material.ENCHANTMENT_TABLE.name();
			} else if (s.equalsIgnoreCase("noteblock")) {
				s = Material.NOTE_BLOCK.name();
			} else {
				Material mat = Material.valueOf(s);
				new Hat(mat, i, Rank.HERO);
			}
		}
	}

	private static String[] superherohats() {
		String stuff = "Sponge, Bookshelf, Lava, Water, Endportal, Stained Glass, Colored Clay, Colored Wools, Jack o’ lantern, Ladder, Vines, Rails, Bars, Lilypad, ColoredPanes, Hopper, Cobweb";
		return stuff.split(", ");
	}

	public static void setUpSuperHeroHats() {
		String[] str = superherohats();
		for (int i = 0; i < str.length; i++) {
			String s = str[i];
			s = s.toUpperCase();
			if (s.equalsIgnoreCase("endportal")) {
				s = Material.ENDER_PORTAL.name();
			} else if (s.equalsIgnoreCase("stained glass")) {
				stainedglass(i);
			} else if (s.equalsIgnoreCase("colored clay")) {
				coloredclay(i);
			} else if (s.equalsIgnoreCase("colored wools")) {
				coloredwool(i);
			} else if (s.equalsIgnoreCase("Jack o’ lantern")) {
				s = Material.JACK_O_LANTERN.name();
			} else if (s.equalsIgnoreCase("vines")) {
				s = Material.VINE.name();
			} else if (s.equalsIgnoreCase("rails")) {
				ItemStack a_rail = new ItemStack(Material.ACTIVATOR_RAIL);
				ItemStack d_rail = new ItemStack(Material.DETECTOR_RAIL);
				ItemStack rail = new ItemStack(Material.RAILS);
				ItemStack p_rail = new ItemStack(Material.POWERED_RAIL);
				new Hat(i, rail, Rank.SUPERHERO, "cosmetics.hats.rail");
				new Hat(i, a_rail, Rank.SUPERHERO, "cosmetics.hats.activator_rail");
				new Hat(i, d_rail, Rank.SUPERHERO, "cosmetics.hats.detector_rail");
				new Hat(i, p_rail, Rank.SUPERHERO, "cosmetics.hats.powered_rail");
			} else if (s.equalsIgnoreCase("lilypad")) {
				s = Material.WATER_LILY.name();
			} else if (s.equalsIgnoreCase("coloredpanes")) {
				stainedglass_panes(i);
			} else if (s.equalsIgnoreCase("hopper")) {
				s = Material.HOPPER.name();
			} else if (s.equalsIgnoreCase("cobweb")) {
				s = Material.WEB.name();
			} else if (s.equalsIgnoreCase("bars")) {
				s = Material.IRON_BARDING.name();
			} else {
				Material mat = Material.valueOf(s);
				new Hat(mat, i, Rank.SUPERHERO);
			}
		}
	}

	private static String[] heroCurseBlocks() {
		String stuff = "Dirt, Stone, Grass, Podzol, Cobblestone, Sandstone, Glass, Sand, Woods, Planks, Iron_block, Pumpkin, Netherrack, Nether_brick";
		return stuff.split(", ");
	}

	public static void setUpHeroCurseBlocks() {
		String[] blocks = heroCurseBlocks();
		for (int i = 0; i < blocks.length; i++) {
			String s = blocks[i];
			s = s.toUpperCase();
			if (s.equalsIgnoreCase("podZol")) {
				ItemStack item = new ItemStack(Material.DIRT, 1, (byte) 2);
				String permission = "cosmetics.cursedblocks.podzol";
				new BlockTrail(i, permission, item, Rank.HERO);
			} else if (s.equalsIgnoreCase("woods")) {
				ItemStack oak = new ItemStack(Material.LOG);
				ItemStack spruce = new ItemStack(Material.LOG, 1, (byte) 1);
				ItemStack birch = new ItemStack(Material.LOG, 1, (byte) 2);
				ItemStack jungle = new ItemStack(Material.LOG, 1, (byte) 3);
				ItemStack acacia = new ItemStack(Material.LOG, 1, (byte) 4);
				ItemStack dark = new ItemStack(Material.LOG, 1, (byte) 5);
				new BlockTrail(i, "cosmetics.cursedblocks.oak_log", oak, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.spruce_log", spruce, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.birch_log", birch, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.jungle_log", jungle, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.acacia_log", acacia, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.dark_log", dark, Rank.HERO);
			} else if (s.equalsIgnoreCase("planks")) {
				ItemStack oak = new ItemStack(Material.WOOD);
				ItemStack spruce = new ItemStack(Material.WOOD, 1, (byte) 1);
				ItemStack birch = new ItemStack(Material.WOOD, 1, (byte) 2);
				ItemStack jungle = new ItemStack(Material.WOOD, 1, (byte) 3);
				ItemStack acacia = new ItemStack(Material.WOOD, 1, (byte) 4);
				ItemStack dark = new ItemStack(Material.WOOD, 1, (byte) 5);
				new BlockTrail(i, "cosmetics.cursedblocks.oak_plank", oak, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.spruce_plank", spruce, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.birch_plank", birch, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.jungle_plank", jungle, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.acacia_plank", acacia, Rank.HERO);
				new BlockTrail(i, "cosmetics.cursedblocks.dark_plank", dark, Rank.HERO);
			} else {
				Material mat = Material.valueOf(s);
				ItemStack item = new ItemStack(mat);
				String permission = "cosmetics.cursedblocks." + mat.name().toLowerCase();
				new BlockTrail(i, permission, item, Rank.HERO);
			}
		}
	}

	private static String[] superherocurseblocks() {
		String stuff = "Sponge, Bookshelf, Stained Glass, Diamond_Block, Gold_Block, Emerald_Block, ColoredWool, ColoredClays, Hay_block";
		return stuff.split(", ");
	}

	public static void setUpSuperHeroCurseBlocks() {
		String[] blocks = superherocurseblocks();
		for (int i = 0; i < blocks.length; i++) {
			String s = blocks[i];
			s = s.toUpperCase();
			if (s.equalsIgnoreCase("stained glass")) {
				stainedglassB(i);
			} else if (s.equalsIgnoreCase("ColoredClays")) {
				coloredclayB(i);
			} else if (s.equalsIgnoreCase("ColoredWool")) {
				coloredwoolB(i);
			} else {
				Material mat = Material.valueOf(s);
				ItemStack item = new ItemStack(mat);
				new BlockTrail(i, item, Rank.SUPERHERO, "cosmetics.cursedblocks." + mat.name().toLowerCase());
			}
		}
	}

	private static void stainedglass(int i) {
		ItemStack stainedglass = new ItemStack(Material.STAINED_GLASS);
		ItemStack stainedglass_orange = new ItemStack(Material.STAINED_GLASS, 1, (short) 1);
		ItemStack stainedglass_magenta = new ItemStack(Material.STAINED_GLASS, 1, (short) 2);
		ItemStack stainedglass_light_blue = new ItemStack(Material.STAINED_GLASS, 1, (short) 3);
		ItemStack stainedglass_yellow = new ItemStack(Material.STAINED_GLASS, 1, (short) 4);
		ItemStack stainedglass_lime = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
		ItemStack stainedglass_pink = new ItemStack(Material.STAINED_GLASS, 1, (short) 6);
		ItemStack stainedglass_gray = new ItemStack(Material.STAINED_GLASS, 1, (short) 7);
		ItemStack stainedglass_light_gray = new ItemStack(Material.STAINED_GLASS, 1, (short) 8);
		ItemStack stainedglass_cyan = new ItemStack(Material.STAINED_GLASS, 1, (short) 9);
		ItemStack stainedglass_purple = new ItemStack(Material.STAINED_GLASS, 1, (short) 10);
		ItemStack stainedglass_blue = new ItemStack(Material.STAINED_GLASS, 1, (short) 11);
		ItemStack stainedglass_brown = new ItemStack(Material.STAINED_GLASS, 1, (short) 12);
		ItemStack stainedglass_green = new ItemStack(Material.STAINED_GLASS, 1, (short) 13);
		ItemStack stainedglass_red = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);
		ItemStack stainedglass_black = new ItemStack(Material.STAINED_GLASS, 1, (short) 15);
		new Hat(i, stainedglass, Rank.SUPERHERO, "cosmetics.hats.stainedglass");
		new Hat(i, stainedglass_black, Rank.SUPERHERO, "cosmetics.hats.stainedglass.black");
		new Hat(i, stainedglass_blue, Rank.SUPERHERO, "cosmetics.hats.stainedglass.blue");
		new Hat(i, stainedglass_brown, Rank.SUPERHERO, "cosmetics.hats.stainedglass.brown");
		new Hat(i, stainedglass_cyan, Rank.SUPERHERO, "cosmetics.hats.stainedglass.cyan");
		new Hat(i, stainedglass_gray, Rank.SUPERHERO, "cosmetics.hats.stainedglass.gray");
		new Hat(i, stainedglass_green, Rank.SUPERHERO, "cosmetics.hats.stainedglass.green");
		new Hat(i, stainedglass_light_blue, Rank.SUPERHERO, "cosmetics.hats.stainedglass.light_blue");
		new Hat(i, stainedglass_light_gray, Rank.SUPERHERO, "cosmetics.hats.stainedglass.light_gray");
		new Hat(i, stainedglass_lime, Rank.SUPERHERO, "cosmetics.hats.stainedglass.lime");
		new Hat(i, stainedglass_magenta, Rank.SUPERHERO, "cosmetics.hats.stainedglass.magenta");
		new Hat(i, stainedglass_orange, Rank.SUPERHERO, "cosmetics.hats.stainedglass.orange");
		new Hat(i, stainedglass_pink, Rank.SUPERHERO, "cosmetics.hats.stainedglass.pink");
		new Hat(i, stainedglass_purple, Rank.SUPERHERO, "cosmetics.hats.stainedglass.purple");
		new Hat(i, stainedglass_red, Rank.SUPERHERO, "cosmetics.hats.stainedglass.red");
		new Hat(i, stainedglass_yellow, Rank.SUPERHERO, "cosmetics.hats.stainedglass.yellow");
	}

	private static void coloredclay(int i) {
		ItemStack stainedclay = new ItemStack(Material.STAINED_CLAY);
		ItemStack stainedclay_orange = new ItemStack(Material.STAINED_CLAY, 1, (short) 1);
		ItemStack stainedclay_magenta = new ItemStack(Material.STAINED_CLAY, 1, (short) 2);
		ItemStack stainedclay_light_blue = new ItemStack(Material.STAINED_CLAY, 1, (short) 3);
		ItemStack stainedclay_yellow = new ItemStack(Material.STAINED_CLAY, 1, (short) 4);
		ItemStack stainedclay_lime = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
		ItemStack stainedclay_pink = new ItemStack(Material.STAINED_CLAY, 1, (short) 6);
		ItemStack stainedclay_gray = new ItemStack(Material.STAINED_CLAY, 1, (short) 7);
		ItemStack stainedclay_light_gray = new ItemStack(Material.STAINED_CLAY, 1, (short) 8);
		ItemStack stainedclay_cyan = new ItemStack(Material.STAINED_CLAY, 1, (short) 9);
		ItemStack stainedclay_purple = new ItemStack(Material.STAINED_CLAY, 1, (short) 10);
		ItemStack stainedclay_blue = new ItemStack(Material.STAINED_CLAY, 1, (short) 11);
		ItemStack stainedclay_brown = new ItemStack(Material.STAINED_CLAY, 1, (short) 12);
		ItemStack stainedclay_green = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
		ItemStack stainedclay_red = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
		ItemStack stainedclay_black = new ItemStack(Material.STAINED_CLAY, 1, (short) 15);
		new Hat(i, stainedclay, Rank.SUPERHERO, "cosmetics.hats.stainedclay");
		new Hat(i, stainedclay_black, Rank.SUPERHERO, "cosmetics.hats.stainedclay.black");
		new Hat(i, stainedclay_blue, Rank.SUPERHERO, "cosmetics.hats.stainedclay.blue");
		new Hat(i, stainedclay_brown, Rank.SUPERHERO, "cosmetics.hats.stainedclay.brown");
		new Hat(i, stainedclay_cyan, Rank.SUPERHERO, "cosmetics.hats.stainedclay.cyan");
		new Hat(i, stainedclay_gray, Rank.SUPERHERO, "cosmetics.hats.stainedclay.gray");
		new Hat(i, stainedclay_green, Rank.SUPERHERO, "cosmetics.hats.stainedclay.green");
		new Hat(i, stainedclay_light_blue, Rank.SUPERHERO, "cosmetics.hats.stainedclay.light_blue");
		new Hat(i, stainedclay_light_gray, Rank.SUPERHERO, "cosmetics.hats.stainedclay.light_gray");
		new Hat(i, stainedclay_lime, Rank.SUPERHERO, "cosmetics.hats.stainedclay.lime");
		new Hat(i, stainedclay_magenta, Rank.SUPERHERO, "cosmetics.hats.stainedclay.magenta");
		new Hat(i, stainedclay_orange, Rank.SUPERHERO, "cosmetics.hats.stainedclay.orange");
		new Hat(i, stainedclay_pink, Rank.SUPERHERO, "cosmetics.hats.stainedclay.pink");
		new Hat(i, stainedclay_purple, Rank.SUPERHERO, "cosmetics.hats.stainedclay.purple");
		new Hat(i, stainedclay_red, Rank.SUPERHERO, "cosmetics.hats.stainedclay.red");
		new Hat(i, stainedclay_yellow, Rank.SUPERHERO, "cosmetics.hats.stainedclay.yellow");
	}

	private static void coloredwool(int i) {
		for (DyeColor color : DyeColor.values()) {
			Wool wool = new Wool(color);
			ItemStack item = wool.toItemStack();
			new Hat(i, item, Rank.SUPERHERO, "cosmetics.hats." + color.name().toLowerCase() + "_wool");
		}
	}

	private static void stainedglass_panes(int i) {
		ItemStack stainedglasspane = new ItemStack(Material.STAINED_GLASS_PANE);
		ItemStack stainedglasspane_orange = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
		ItemStack stainedglasspane_magenta = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2);
		ItemStack stainedglasspane_light_blue = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
		ItemStack stainedglasspane_yellow = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
		ItemStack stainedglasspane_lime = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
		ItemStack stainedglasspane_pink = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6);
		ItemStack stainedglasspane_gray = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		ItemStack stainedglasspane_light_gray = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8);
		ItemStack stainedglasspane_cyan = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
		ItemStack stainedglasspane_purple = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10);
		ItemStack stainedglasspane_blue = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
		ItemStack stainedglasspane_brown = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 12);
		ItemStack stainedglasspane_green = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
		ItemStack stainedglasspane_red = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemStack stainedglasspane_black = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		new Hat(i, stainedglasspane, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane");
		new Hat(i, stainedglasspane_black, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.black");
		new Hat(i, stainedglasspane_blue, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.blue");
		new Hat(i, stainedglasspane_brown, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.brown");
		new Hat(i, stainedglasspane_cyan, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.cyan");
		new Hat(i, stainedglasspane_gray, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.gray");
		new Hat(i, stainedglasspane_green, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.green");
		new Hat(i, stainedglasspane_light_blue, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.light_blue");
		new Hat(i, stainedglasspane_light_gray, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.light_gray");
		new Hat(i, stainedglasspane_lime, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.lime");
		new Hat(i, stainedglasspane_magenta, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.magenta");
		new Hat(i, stainedglasspane_orange, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.orange");
		new Hat(i, stainedglasspane_pink, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.pink");
		new Hat(i, stainedglasspane_purple, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.purple");
		new Hat(i, stainedglasspane_red, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.red");
		new Hat(i, stainedglasspane_yellow, Rank.SUPERHERO, "cosmetics.hats.stainedglasspane.yellow");
	}

	private static void coloredwoolB(int i) {
		for (DyeColor color : DyeColor.values()) {
			Wool wool = new Wool(color);
			ItemStack item = wool.toItemStack();
			new BlockTrail(i, "cosmetics.cursedblocks." + color.name().toLowerCase() + "_wool", item, Rank.SUPERHERO);
		}
	}

	private static void coloredclayB(int i) {
		ItemStack stainedclay = new ItemStack(Material.STAINED_CLAY);
		ItemStack stainedclay_orange = new ItemStack(Material.STAINED_CLAY, 1, (short) 1);
		ItemStack stainedclay_magenta = new ItemStack(Material.STAINED_CLAY, 1, (short) 2);
		ItemStack stainedclay_light_blue = new ItemStack(Material.STAINED_CLAY, 1, (short) 3);
		ItemStack stainedclay_yellow = new ItemStack(Material.STAINED_CLAY, 1, (short) 4);
		ItemStack stainedclay_lime = new ItemStack(Material.STAINED_CLAY, 1, (short) 5);
		ItemStack stainedclay_pink = new ItemStack(Material.STAINED_CLAY, 1, (short) 6);
		ItemStack stainedclay_gray = new ItemStack(Material.STAINED_CLAY, 1, (short) 7);
		ItemStack stainedclay_light_gray = new ItemStack(Material.STAINED_CLAY, 1, (short) 8);
		ItemStack stainedclay_cyan = new ItemStack(Material.STAINED_CLAY, 1, (short) 9);
		ItemStack stainedclay_purple = new ItemStack(Material.STAINED_CLAY, 1, (short) 10);
		ItemStack stainedclay_blue = new ItemStack(Material.STAINED_CLAY, 1, (short) 11);
		ItemStack stainedclay_brown = new ItemStack(Material.STAINED_CLAY, 1, (short) 12);
		ItemStack stainedclay_green = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
		ItemStack stainedclay_red = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
		ItemStack stainedclay_black = new ItemStack(Material.STAINED_CLAY, 1, (short) 15);
		new BlockTrail(i, stainedclay, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay");
		new BlockTrail(i, stainedclay_black, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.black");
		new BlockTrail(i, stainedclay_blue, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.blue");
		new BlockTrail(i, stainedclay_brown, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.brown");
		new BlockTrail(i, stainedclay_cyan, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.cyan");
		new BlockTrail(i, stainedclay_gray, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.gray");
		new BlockTrail(i, stainedclay_green, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.green");
		new BlockTrail(i, stainedclay_light_blue, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.light_blue");
		new BlockTrail(i, stainedclay_light_gray, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.light_gray");
		new BlockTrail(i, stainedclay_lime, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.lime");
		new BlockTrail(i, stainedclay_magenta, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.magenta");
		new Hat(i, stainedclay_orange, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.orange");
		new BlockTrail(i, stainedclay_pink, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.pink");
		new BlockTrail(i, stainedclay_purple, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.purple");
		new BlockTrail(i, stainedclay_red, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.red");
		new BlockTrail(i, stainedclay_yellow, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedclay.yellow");
	}

	private static void stainedglassB(int i) {
		ItemStack stainedclay = new ItemStack(Material.STAINED_GLASS);
		ItemStack stainedclay_orange = new ItemStack(Material.STAINED_GLASS, 1, (short) 1);
		ItemStack stainedclay_magenta = new ItemStack(Material.STAINED_GLASS, 1, (short) 2);
		ItemStack stainedclay_light_blue = new ItemStack(Material.STAINED_GLASS, 1, (short) 3);
		ItemStack stainedclay_yellow = new ItemStack(Material.STAINED_GLASS, 1, (short) 4);
		ItemStack stainedclay_lime = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
		ItemStack stainedclay_pink = new ItemStack(Material.STAINED_GLASS, 1, (short) 6);
		ItemStack stainedclay_gray = new ItemStack(Material.STAINED_GLASS, 1, (short) 7);
		ItemStack stainedclay_light_gray = new ItemStack(Material.STAINED_GLASS, 1, (short) 8);
		ItemStack stainedclay_cyan = new ItemStack(Material.STAINED_GLASS, 1, (short) 9);
		ItemStack stainedclay_purple = new ItemStack(Material.STAINED_GLASS, 1, (short) 10);
		ItemStack stainedclay_blue = new ItemStack(Material.STAINED_GLASS, 1, (short) 11);
		ItemStack stainedclay_brown = new ItemStack(Material.STAINED_GLASS, 1, (short) 12);
		ItemStack stainedclay_green = new ItemStack(Material.STAINED_GLASS, 1, (short) 13);
		ItemStack stainedclay_red = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);
		ItemStack stainedclay_black = new ItemStack(Material.STAINED_GLASS, 1, (short) 15);
		new BlockTrail(i, stainedclay, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass");
		new BlockTrail(i, stainedclay_black, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.black");
		new BlockTrail(i, stainedclay_blue, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.blue");
		new BlockTrail(i, stainedclay_brown, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.brown");
		new BlockTrail(i, stainedclay_cyan, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.cyan");
		new BlockTrail(i, stainedclay_gray, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.gray");
		new BlockTrail(i, stainedclay_green, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.green");
		new BlockTrail(i, stainedclay_light_blue, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.light_blue");
		new BlockTrail(i, stainedclay_light_gray, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.light_gray");
		new BlockTrail(i, stainedclay_lime, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.lime");
		new BlockTrail(i, stainedclay_magenta, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.magenta");
		new Hat(i, stainedclay_orange, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.orange");
		new BlockTrail(i, stainedclay_pink, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.pink");
		new BlockTrail(i, stainedclay_purple, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.purple");
		new BlockTrail(i, stainedclay_red, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.red");
		new BlockTrail(i, stainedclay_yellow, Rank.SUPERHERO, "cosmetics.cursedblocks.stainedglass.yellow");
	}
}