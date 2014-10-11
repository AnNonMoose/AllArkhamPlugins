package me.gtacraft.plugins.gtarespawn;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 6/25/14. Designed for the GTA-Respawn project.
 */

public class GTARespawn extends JavaPlugin implements Listener {

    private static List<Location> respawn_locations = Lists.newArrayList();

    public void onEnable() {
        saveDefaultConfig();

        for (String loc : getConfig().getStringList("hospitals")) {
            String[] split = loc.split(",");
            World world = Bukkit.getWorld(split[0]);
            double x = Double.parseDouble(split[1]);
            double y = Double.parseDouble(split[2]);
            double z = Double.parseDouble(split[3]);
            float yaw = Float.parseFloat(split[4]);
            float pitch = Float.parseFloat(split[5]);

            respawn_locations.add(new Location(world, x, y, z, yaw, pitch));
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender.isOp())) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Error: &cYou do not have permission to use this command!"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Error: &cYou must be a player to use this command!"));
            return true;
        }

        Player player = (Player)sender;
        Location at = player.getLocation();
        String loc = at.getWorld().getName()+","+at.getX()+","+at.getY()+","+at.getZ()+","+at.getYaw()+","+at.getPitch();

        List<String> config = getConfig().getStringList("hospitals");
        config.add(loc);
        getConfig().set("hospitals", config);

        respawn_locations.add(at);

        saveConfig();

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou added a hospital respawn location at: &7"+loc+"&e!"));
        return true;
    }

    public static Location getClosest(Location other) {
        Location closest = null;
        for (Location hospital : respawn_locations) {
            if(closest == null)
                closest = hospital;

            if (closest.distance(other) > hospital.distance(other))
                closest = hospital;
        }

        return closest.clone().add(0, 0.5, 0);
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location died = event.getEntity().getLocation();
        respawn.put(event.getEntity(), getClosest(died));
    }

    private HashMap<Player, Location> respawn = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        Runnable respawn = new Runnable() {
            @Override
            public void run() {
                event.getPlayer().teleport(GTARespawn.this.respawn.remove(event.getPlayer()));
            }
        };
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, respawn, 1l);
    }

    public void onDisable() {
        saveDefaultConfig();
    }

    public static double distanceToClosest(Location location) {
        Location closest = null;
        for (Location hospital : respawn_locations) {
            if(closest == null)
                closest = hospital;

            if (closest.distance(location) > hospital.distance(location))
                closest = hospital;
        }

        return location.distance(closest);
    }
}
