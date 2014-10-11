package org.arkham.cs.cosmetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.effects.EffectManager;
import org.arkham.cs.gui.Category;
import org.arkham.cs.handler.ParticleLibManager.FancyEffect;
import org.arkham.cs.interfaces.Button;
import org.arkham.cs.utils.PlayerMetaDataUtil;
import org.arkham.cs.utils.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomEffect extends Button {

	private FancyEffect effect;
	private int amount;
	private String name;
	private static ArrayList<CustomEffect> effects = new ArrayList<>();
	private static HashMap<Rank, ArrayList<CustomEffect>> effectsByRank = new HashMap<>();

	/**
	 * @param slot
	 * @param cat
	 * @param effect
	 * @param permission
	 * @param display
	 * @param amount
	 */
	public CustomEffect(int slot, Category cat, FancyEffect effect, String permission, ItemStack item, int amount, Rank rank, String name) {
		super(slot, cat, permission, item);
		this.effect = effect;
		this.amount = amount;
		this.name = name;
		effects.add(this);
		ArrayList<CustomEffect> ces = effectsByRank.get(rank);
		if(ces == null){
			ces = new ArrayList<>();
		}
		ces.add(this);
		effectsByRank.put(rank, ces);
	}
	
	public static void addSuperHeroToHero(){
		ArrayList<CustomEffect> ces = effectsByRank.get(Rank.HERO);
		if(ces == null){
			return;
		}
		ArrayList<CustomEffect> sces = effectsByRank.get(Rank.SUPERHERO);
		if(sces == null){
			return;
		}
		sces.addAll(ces);
		return;
	}
	
	public String getName(){
		return name;
	}

	public int getAmount(){
		return amount;
	}

	@Override
	public void onClick(Player player) {
		player.sendMessage(CosmeticSuite.PREFIX + "Your trail effect is now " + ChatColor.UNDERLINE +  getName());
		PlayerMetaDataUtil.removeFromSwitching(player);
		EffectManager manager = CosmeticSuite.getInstance().getEffectManager();
		manager.setEffect(player, this);
		player.closeInventory();
	}

	public FancyEffect getEffect(){
		return effect;
	}
	
	public static List<CustomEffect> getEffects(Rank rank){
		return effectsByRank.get(rank);
	}

}