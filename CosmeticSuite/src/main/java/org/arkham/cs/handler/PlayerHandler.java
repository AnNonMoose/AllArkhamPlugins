package org.arkham.cs.handler;

import org.arkham.cs.utils.Rank;
import org.bukkit.entity.Player;

public class PlayerHandler {
	
	public static Rank getRank(Player player){
		if(player.isOp()){
			return Rank.SUPERHERO;
		}
		if(player.hasPermission("cosmetics.hero")){
			return Rank.HERO;
		}
		if(player.hasPermission("cosmetics.superhero")){
			return Rank.SUPERHERO;
		}
		return Rank.DEFAULT;
	}
	
	public static boolean isSuperHero(Player player){
		return getRank(player) == Rank.SUPERHERO;
	}
	
	public static boolean isHero(Player player){
		return getRank(player) ==  Rank.HERO;
	}
	
	public static boolean isNothingSpecial(Player player){
		return getRank(player) == Rank.DEFAULT;
	}

}
