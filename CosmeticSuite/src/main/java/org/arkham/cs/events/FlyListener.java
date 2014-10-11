package org.arkham.cs.events;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.TrailingBlock;
import org.arkham.cs.handler.PlayerHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FlyListener implements Listener  {

	public FlyListener(){
		CosmeticSuite cs = CosmeticSuite.getInstance();
		cs.getServer().getPluginManager().registerEvents(this, cs);
	}

	@EventHandler
	public void onFly(PlayerToggleFlightEvent event){
		Player player = event.getPlayer();
		if(event.isFlying()){
			TrailingBlock tb = TrailingBlock.get(player);
			if(tb == null){
				tb = new TrailingBlock(player);
				return;
			}
			if(!tb.isInUse()){
				return;
			}
			tb.setInUse(false);
			return;
		} else {
			TrailingBlock tb = TrailingBlock.get(player);
			if(tb == null){
				return;
			}
			if(tb.isInUse()){
				return;
			}
			tb.setInUse(true);
		}
		if(player.isOp()){
			return;
		}
		if(player.hasPermission("cosmetics.*")){
			return;
		}
		boolean serverIsInFly = CosmeticSuite.getInstance().getConfig().getBoolean("flyable", false);
		if(serverIsInFly){
			if(PlayerHandler.isHero(player) || PlayerHandler.isSuperHero(player)){
				return;
			}
			if(PlayerHandler.isNothingSpecial(player)){
				if(event.isFlying()){
					event.setCancelled(true);
				}
			}
			return;
		}
		if(player.hasMetadata("cosmetics-fly")){
			return;
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onChange(EntityChangeBlockEvent event){
		if(event.getEntityType() != EntityType.FALLING_BLOCK){
			return;
		}
		FallingBlock block = (FallingBlock) event.getEntity();
		if(block.hasMetadata("flying")){
			event.setCancelled(true);
			block.remove();
			TrailingBlock.blocks.remove(block);
			return;
		}
	}
}
