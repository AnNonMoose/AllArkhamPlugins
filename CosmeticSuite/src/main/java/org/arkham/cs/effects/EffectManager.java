package org.arkham.cs.effects;

import java.util.HashMap;
import java.util.UUID;

import org.arkham.cs.CosmeticSuite;
import org.arkham.cs.cosmetics.CustomEffect;
import org.arkham.cs.handler.ParticleLibManager.FancyEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EffectManager implements Listener{
	
	private HashMap<UUID, CustomEffect> effects = new HashMap<>();
	
	public EffectManager(){
		CosmeticSuite cs = CosmeticSuite.getInstance();
		cs.getServer().getPluginManager().registerEvents(this, cs);
	}
	
	public CustomEffect getEffect(Player player){
		return effects.get(player.getUniqueId());
	}
	
	public void setEffect(Player player, CustomEffect effect){
	    if(effect == null) {
	    	if(effects.containsKey(player.getUniqueId())){
	    		effects.get(player.getUniqueId()).getEffect().stop(player);
	    	}
	        effects.remove(player.getUniqueId());
	        return;
	    }
	    
		effects.put(player.getUniqueId(), effect);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()){
			return;
		}
		if(player.hasMetadata("effected")){
			return;
		}
		CustomEffect effect = getEffect(player);
		if(effect == null){
			return;
		}
		FancyEffect playedEffect = effect.getEffect();
		playedEffect.display(player);
		player.setMetadata("effected", new FixedMetadataValue(CosmeticSuite.getInstance(), ""));
	}
}