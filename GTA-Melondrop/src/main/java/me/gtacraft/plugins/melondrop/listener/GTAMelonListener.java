package me.gtacraft.plugins.melondrop.listener;

import me.gtacraft.plugins.melondrop.GTAMelondrop;
import me.gtacraft.plugins.melondrop.task.MelonDropTask;
import me.gtacraft.plugins.safezone.ParticleEffects;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * Created by Connor on 7/12/14. Designed for the GTA-Melondrop project.
 */

public class GTAMelonListener implements Listener {

    public GTAMelonListener() {
        Bukkit.getPluginManager().registerEvents(this, GTAMelondrop.get());
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (MelonDropTask.remove_instantly.contains(event.getEntity())) {
            event.setCancelled(true);
            MelonDropTask.remove_instantly.remove(event.getEntity());
        }
    }

    public static ItemStack melonSpew;
    static {
        melonSpew = new ItemStack(Material.MELON);
        ItemMeta spew = melonSpew.getItemMeta();
        spew.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&l&nMagic Melon Slice"));
        spew.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&aThe blood of the &lGiant Magical Melon&r&a!")));
        melonSpew.setItemMeta(spew);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block hit = event.getBlock();
        if (MelonDropTask.wrapped != null) {
            if (!(MelonDropTask.wrapped.equals(hit)))
                return;

            Player player = event.getPlayer();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l&nDAMAGED&r&f: "+(49-MelonDropTask.breakAttempts)+" hits left!"));
            player.getWorld().playSound(hit.getLocation(), Sound.STEP_LADDER, 1, 2);

            if (Math.random()*100 >= 85) {
                Item yayMelon = hit.getWorld().dropItem(hit.getLocation().clone().add(0.5, 0.6, 0.5), melonSpew);
                yayMelon.setPickupDelay(15);
                yayMelon.setVelocity(new Vector((Math.random()*.5)-.25, (Math.random()*1.1), (Math.random()*.5)-.25));
            }

            hit.setType(Material.MELON_BLOCK);
            ParticleEffects.sendCrackToPlayer(true, Material.MELON.getId(), (byte) 0x0, player, hit.getLocation().clone().add(0.5, 0.5, 0.5), .3f, .3f, .3f, 50);
            MelonDropTask.breakAttempts++;
            MelonDropTask.tick();

            event.setCancelled(true);
        }
    }
}
