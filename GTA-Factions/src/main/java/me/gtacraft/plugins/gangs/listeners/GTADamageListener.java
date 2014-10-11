package me.gtacraft.plugins.gangs.listeners;

import me.gtacraft.plugins.gangs.GangMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Created by Connor on 7/1/14. Designed for the GTA-Factions project.
 */

public class GTADamageListener extends IListener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player))
            return;

        Player hurt = (Player)event.getEntity();
        Player damager = (Player)event.getDamager();

        GangMember hMem = GangMember.fromPlayer(hurt);
        GangMember dMem = GangMember.fromPlayer(damager);

        if (hMem.getGang() == null || dMem.getGang() == null)
            return;
        else if (!(hMem.getGang().equals(dMem.getGang())))
            return;

        //same gang, check ff

        if (!(hMem.getGang().isFriendlyFire())) {
            //deny
            event.setCancelled(true);
        }
    }
}
