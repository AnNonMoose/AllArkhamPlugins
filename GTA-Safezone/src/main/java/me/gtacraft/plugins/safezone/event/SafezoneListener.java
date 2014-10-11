package me.gtacraft.plugins.safezone.event;

import com.oracle.jrockit.jfr.EventDefinition;
import me.gtacraft.event.BulletHitBlockEvent;
import me.gtacraft.event.PreFireEvent;
import me.gtacraft.event.WeaponDamageEntityEvent;
import me.gtacraft.plugins.safezone.ParticleEffects;
import me.gtacraft.plugins.safezone.Safezone;
import me.gtacraft.plugins.safezone.util.SafezoneUtil;
import me.vaqxine.GTAShops.GTAShops;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Connor on 6/27/14. Designed for the GTA-Safezone project.
 */

public class SafezoneListener implements Listener {

    public SafezoneListener() {
        Bukkit.getPluginManager().registerEvents(this, Safezone.getInstance());
    }

    @EventHandler
    public void onPreFire(PreFireEvent event) {
        Player shooter = (Player)event.getShooter().getHolder();
        int time = SafezoneUtil.getSubscribedTime(shooter.getName());
        if (time != -1) {
            shooter.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING: &fYou cannot shoot anyone for &e"+time+" &fseconds!"));
            event.setCancelled(true);
        }
        if (SafezoneUtil.isInSafeZone(event.getFiredFrom())) {
            event.getShooter().getHolder().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING: &fYou cannot shoot guns in a safe zone!"));
            event.setCancelled(true);
        }
        if (GTAShops.isShopNearby(event.getFiredFrom()) && event.isGrenade())
            event.setCancelled(true);
    }

    @EventHandler
    public void onWeaponDamageEntity(WeaponDamageEntityEvent event) {
        if (event.getEntity() instanceof Player && (SafezoneUtil.getSubscribedTime(((Player)event.getEntity()).getName())!= -1))
            event.setCancelled(true);

        if (SafezoneUtil.isInSafeZone(event.getEntity().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onWeaponHitBlock(BulletHitBlockEvent event) {
        if (SafezoneUtil.isInSafeZone(event.getLocationHit()))
            event.setCancelled(true);
        if (GTAShops.isShopNearby(event.getLocationHit()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (GTAShops.isShopNearby(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (SafezoneUtil.isInSafeZone(event.getLocation()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Chest || event.getInventory().getHolder() instanceof DoubleChest) {
            if (SafezoneUtil.isInSafeZone(event.getPlayer().getLocation())) {
                ((Player)event.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING: &fYou cannot open chests in safe zones!"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {
        if (SafezoneUtil.isInSafeZone(event.getEntity().getLocation())) {
            event.setDamage(0);
            event.setCancelled(true);
        } else {
            if (!(event.getEntity() instanceof Player))
                return;

            Player p = (Player)event.getEntity();
            if (SafezoneUtil.getSubscribedTime(p.getName()) != -1) {
                event.setDamage(0);
                event.setCancelled(true);
                return;
            }

            SafezoneUtil.subscribe(p.getName()+"_combat", 7);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && SafezoneUtil.getSubscribedTime(((Player)event.getEntity()).getName()) != -1) {
            int time = SafezoneUtil.getSubscribedTime(((Player)event.getEntity()).getName());
            if (event.getDamager() instanceof Player)
                ((Player)event.getDamager()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING: &f"+((Player) event.getEntity()).getName()+"&f cannot be hurt for another &e"+time+" &fseconds!"));
            event.setCancelled(true);
            return;
        }
        else if (event.getDamager() instanceof Player) {
            int time = SafezoneUtil.getSubscribedTime(((Player) event.getDamager()).getName());
            if (time != -1) {
                event.setCancelled(true);
                ((Player) event.getDamager()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING: &fYou cannot hurt anyone for &e"+time+" &fseconds!"));
            }
        }

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player hurt = (Player)event.getEntity();
            Player damager = (Player)event.getDamager();

            if (SafezoneUtil.isInSafeZone(hurt.getLocation())) {
                damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING: &f"+hurt.getName()+"&f is in a safe zone!"));
                event.setCancelled(true);
            }
        }
        else {
            Entity damager = event.getDamager();
            if (damager instanceof Player)
                return;

            if (SafezoneUtil.isInSafeZone(damager.getLocation())) {
                ParticleEffects.sendToLocation(ParticleEffects.FIREWORKS_SPARK, damager.getLocation().add(0.5, 0.5, 0.5), 0, 0, 0, (float) 0.3, 30);
                damager.remove();
            }
        }
    }

    @EventHandler
    public void onBlockDamageEvent(BlockBreakEvent event) {
        if (SafezoneUtil.isInSafeZone(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    //async player
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Safezone.async_player_map.put(event.getPlayer(), 0);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Safezone.async_player_map.remove(event.getPlayer());
    }
}
