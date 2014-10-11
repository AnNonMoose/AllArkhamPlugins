package org.arkham.cs.commands;

import java.util.List;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.BlockTrail;
import org.arkham.cs.cosmetics.CustomEffect;
import org.arkham.cs.cosmetics.Hat;
import org.arkham.cs.cosmetics.HeroKit;
import org.arkham.cs.cosmetics.SuperHeroKit;
import org.arkham.cs.gui.Category;
import org.arkham.cs.gui.GUIManager;
import org.arkham.cs.gui.GUIPage;
import org.arkham.cs.handler.PlayerHandler;
import org.arkham.cs.handler.PurchaseHandler;
import org.arkham.cs.utils.PlayerMetaDataUtil;
import org.arkham.cs.utils.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CosmeticCommand implements CommandExecutor {

	public CosmeticCommand(){
		CosmeticSuite cs = CosmeticSuite.getInstance();
		cs.getCommand("cosmetics").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			player.openInventory(CosmeticSuite.getInstance().getGuiManager().getMain(player));
			PlayerMetaDataUtil.setInGUI(player);
			return true;
		}
		if (args.length == 1) {
			switch (args[0].toLowerCase()) {
			case "-e":
				openEffects(player);
				break;
			case "-h":
				openHats(player);
				break;
			case "-bt":
				openWalkingBlocks(player);
				break;
			case "help":
				sendHelp(player);
				break;
			default:
				player.openInventory(CosmeticSuite.getInstance().getGuiManager().getMain(player));
				break;
			}
			return true;
		}
		return false;
	}

	public void sendHelp(Player player){
		String[] help = {
				ChatColor.GREEN + "==============================================",
				ChatColor.GOLD + "/cosmetics - " + ChatColor.GRAY + " Opens the main GUI.",
				ChatColor.GOLD + "Arguments: ",
				"  " + ChatColor.RED + "-h | " + ChatColor.GRAY + "Open the Hat's GUI",
				"  " + ChatColor.RED + "-e | " + ChatColor.GRAY + "Open the Effects's GUI",
				"  " + ChatColor.RED + "-bt | " + ChatColor.GRAY + "Open the Block Trail's GUI",
				ChatColor.GREEN + "=============================================="
		};
		player.sendMessage(help);
	}

	public void openEffects(Player player) {
		PlayerMetaDataUtil.setInGUI(player);
		GUIManager manager = CosmeticSuite.getInstance().getGuiManager();
		List<GUIPage> pages = manager.getPages(Category.EFFECTS);
		GUIPage page = pages.get(0);
		if(page == null){
			return;
		}
		if(PlayerHandler.isNothingSpecial(player)){
		    player.sendMessage(CosmeticSuite.PREFIX + "Purchase HERO or SUPERHERO at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org" + ChatColor.YELLOW + " to unlock effects!");
		    player.closeInventory();
			// player.openInventory(page.getInv());
			return;
		}
		Rank rank = PlayerHandler.getRank(player);
		for(CustomEffect hat : CustomEffect.getEffects(rank)){
			if(!PurchaseHandler.hasPurchased(player, hat)){
				PurchaseHandler.addPurchase(player, hat);
			}
			GUIPage.addButton(hat, Category.EFFECTS, player);
		}
		player.openInventory(page.getInv());
		return;
	}


	public void openHats(Player player) {
		PlayerMetaDataUtil.setInGUI(player);
		GUIManager manager = CosmeticSuite.getInstance().getGuiManager();
		List<GUIPage> pages = manager.getPages(Category.HATS);
		GUIPage page = pages.get(0);
		if(page == null){
			return;
		}
		if(PlayerHandler.isNothingSpecial(player)){
		    player.sendMessage(CosmeticSuite.PREFIX + "Purchase HERO or SUPERHERO at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org" + ChatColor.YELLOW + " to unlock hats!");
		    player.closeInventory();
			// player.openInventory(page.getInv());
			return;
		}
		Rank rank = PlayerHandler.getRank(player);
		for(Hat hat : Hat.getHats(rank)){
			if(!PurchaseHandler.hasPurchased(player, hat)){
				PurchaseHandler.addPurchase(player, hat);
			}
			GUIPage.addButton(hat, Category.HATS, player);
		}
		player.openInventory(page.getInv());
	}

	public void openWalkingBlocks(Player player) {
		PlayerMetaDataUtil.setInGUI(player);
		GUIManager manager = CosmeticSuite.getInstance().getGuiManager();
		List<GUIPage> pages = manager.getPages(Category.CURSE_BLOCKS);
		GUIPage page = pages.get(0);
		if(page == null){
			return;
		}
		
		if(PlayerHandler.isNothingSpecial(player)){
			// player.openInventory(page.getInv());
			player.sendMessage(CosmeticSuite.PREFIX + "Purchase HERO or SUPERHERO at " + ChatColor.UNDERLINE + "buy.arkhamnetwork.org" + ChatColor.YELLOW + " to unlock block trails!");
			player.closeInventory();
			return;
		}
		
		Rank rank = PlayerHandler.getRank(player);
		for(BlockTrail hat : BlockTrail.getByRank(rank)){
			if(!PurchaseHandler.hasPurchased(player, hat)){
				PurchaseHandler.addPurchase(player, hat);
			}
			GUIPage.addButton(hat, Category.CURSE_BLOCKS, player);
		}
		player.openInventory(page.getInv());
	}

	public void openKits(Player player){
		PlayerMetaDataUtil.setInGUI(player);
		GUIManager manager = CosmeticSuite.getInstance().getGuiManager();
		List<GUIPage> pages = manager.getPages(Category.KITS);
		GUIPage page = pages.get(0);
		if(page == null){
			return;
		}
		SuperHeroKit shk = manager.getSuperHeroKit();
		HeroKit hk = manager.getHeroKit();
		GUIPage.addButton(hk, Category.KITS, player);
		GUIPage.addButton(shk, Category.KITS, player);
		player.openInventory(page.getInv());
	}

}
