package me.gtacraft;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 4/28/14. Designed for the GTA-NametagEdit project.
 */

public class GTANametag extends JavaPlugin implements Listener {

    private List<Player> addingTags = new ArrayList<Player>();
    private List<Player> removingTags = new ArrayList<Player>();

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        MinecartName.setHider(new EntityHider(this, EntityHider.Policy.BLACKLIST));
    }

    @EventHandler
    public void minecartPlace(PlayerInteractEvent e) {
        if (e.getPlayer().getItemInHand().getType().equals(Material.MINECART)) {
            if (e.getClickedBlock() != null && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                e.setCancelled(true);

                Minecart spawn = (Minecart) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.MINECART);
                getServer().getPluginManager().registerEvents(new MinecartName(spawn, "&c&lFerrari 458!"), this);
            }
        }
    }
}
