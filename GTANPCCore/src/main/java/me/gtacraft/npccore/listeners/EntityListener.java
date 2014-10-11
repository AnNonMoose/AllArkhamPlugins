package me.gtacraft.npccore.listeners;

import me.gtacraft.npccore.GTANPCCore;
import me.gtacraft.npccore.struct.Gang;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.UUID;

/**
 * Created by devan_000 on 4/28/2014.
 */
public class EntityListener implements Listener {

    private final GTANPCCore plugin = GTANPCCore.get();

    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled = true)
    public void onCombust(EntityCombustEvent event) {
        if (event.getEntity() == null) {
            return;
        }

        if (plugin.entityController.entityUUIDs.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() == null || event.getDamager() == null) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!plugin.entityController.entityUUIDs.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (plugin.entityController.gangs.get(event.getEntity().getUniqueId()) == null) {
            return;
        }

        if (!plugin.entityController.gangs.get(event.getEntity().getUniqueId()).getAngryAtPlayers().contains(event.getDamager().getUniqueId())) {
            plugin.entityController.gangs.get(event.getEntity().getUniqueId()).angryAtPlayers.add(event.getDamager().getUniqueId());
        }

        event.setCancelled(false);

        for (UUID gangMemberUUID : plugin.entityController.gangs.get(event.getEntity().getUniqueId()).getMembers()) {
            if (event.getEntity().getUniqueId() == gangMemberUUID) {
                continue;
            }

            for (Entity entity : event.getDamager().getWorld().getEntities()) {
                if (entity.getUniqueId().equals(gangMemberUUID)) {
                    ((Skeleton)entity).setTarget((Player)event.getDamager());
                    break;
                }
            }
        }
      }


    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled = true)
    public void onTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        if (!plugin.entityController.entityUUIDs.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        Gang gang = plugin.entityController.gangs.get(event.getEntity().getUniqueId());

        if (gang == null) {
            return;
        }

        if (!gang.angryAtPlayers.contains(event.getTarget().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
    }
}

