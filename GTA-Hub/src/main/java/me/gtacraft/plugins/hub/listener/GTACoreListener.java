package me.gtacraft.plugins.hub.listener;

import com.google.common.collect.Lists;
import me.gtacraft.plugins.hub.GTAHub;
import me.gtacraft.plugins.hub.special.GroundSmashController;
import me.gtacraft.plugins.hub.util.GTAItems;
import me.gtacraft.plugins.hub.util.GTAUtil;
import me.gtacraft.plugins.hub.util.ParticleEffects;
import me.vaqxine.WorldRegeneration.RegenerationAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Connor on 7/8/14. Designed for the GTA-Hub project.
 */

public class GTACoreListener implements Listener {

    public GTACoreListener() {
        Bukkit.getPluginManager().registerEvents(this, GTAHub.get());
    }

    private List<Player> hidePlayers = Lists.newArrayList();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack inHand = player.getItemInHand();
        if (inHand == null || inHand.getType().equals(Material.AIR))
            return;

        //verify type and parse stack
        if (inHand.equals(GTAItems.SERVER_SELECTOR)) {
            player.performCommand("instance"); //open server selector
        } else if (inHand.equals(GTAItems.SHOW_PLAYERS)) {
            if (GTAUtil.getTimeLeft(player.getName()) != 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot use this feature for another &e"+GTAUtil.getTimeLeft(player.getName())+" seconds&c!"));
                event.setCancelled(true);
                return;
            }
            hidePlayers.remove(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "» &aPlayers are now visible!"));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(player))
                    continue;

                if (p.hasPermission("hub.bypass.hide"))
                    continue;

                player.showPlayer(p);
                ParticleEffects.FIREWORKS_SPARK.sendToPlayer(player, p.getLocation(), 0f, 0f, 0f, 0.1f, 50);
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 2);
            player.setItemInHand(GTAItems.HIDE_PLAYERS);
            player.updateInventory();
            event.setCancelled(true);
            GTAUtil.addCountdownTask(player.getName(), 5);
        } else if (inHand.equals(GTAItems.HIDE_PLAYERS)) {
            if (GTAUtil.getTimeLeft(player.getName()) != 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot use this feature for another &e"+GTAUtil.getTimeLeft(player.getName())+" seconds&c!"));
                event.setCancelled(true);
                return;
            }
            hidePlayers.add(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "» &cPlayers are now hidden!"));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(player))
                    continue;

                if (p.hasPermission("hub.bypass.hide"))
                    continue;
                player.hidePlayer(p);
                ParticleEffects.FIREWORKS_SPARK.sendToPlayer(player, p.getLocation(), 0f, 1f, 0f, 0.1f, 50);
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1.7f);
            player.setItemInHand(GTAItems.SHOW_PLAYERS);
            player.updateInventory();
            event.setCancelled(true);
            GTAUtil.addCountdownTask(player.getName(), 5);
            return;
        } else if (inHand.equals(GTAItems.GROUND_SMASHER)) {
            if (event.getClickedBlock() != null)
                //GroundSmashController.handleSmash(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        /*if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) && GroundSmashController.port_locations.contains(event.getEntity().getLocation().getBlock())) {
            Player player = (Player)event.getEntity();
            player.teleport(GTAHub.spawn.clone().add(0, 75, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
            player.setFireTicks(0);
        }*/

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (GTAHub.water_fountain == null)
            return;

        if (event.getEntity() instanceof FallingBlock) {
            FallingBlock fb = (FallingBlock)event.getEntity();
            if (GTAHub.water_fountain.handleFallingBlock(fb))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!(event.getWorld().isThundering()))
            event.setCancelled(true);
    }
}
