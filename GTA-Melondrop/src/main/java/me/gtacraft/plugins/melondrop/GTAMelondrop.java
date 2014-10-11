package me.gtacraft.plugins.melondrop;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.gtacraft.plugins.melondrop.item.ItemData;
import me.gtacraft.plugins.melondrop.listener.GTAMelonListener;
import me.gtacraft.plugins.melondrop.task.MelonDropTask;
import me.gtacraft.plugins.melondrop.util.FireworkEffectPlayer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Created by Connor on 7/7/14. Designed for the GTA-Melondrop project.
 */

public class GTAMelondrop extends JavaPlugin {

    private static GTAMelondrop instance;
    @Getter
    private static List<ItemData> drops = Lists.newArrayList();

    public static Location min;
    public static Location max;

    public static int run_interval;

    public static FireworkEffectPlayer fireworks = new FireworkEffectPlayer();

    public static GTAMelondrop get() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        World world = Bukkit.getWorld(getConfig().getString("worldBounds.name"));
        min = new Location(world, getConfig().getInt("worldBounds.xMin"), 100, getConfig().getInt("worldBounds.zMin"));
        max = new Location(world, getConfig().getInt("worldBounds.xMax"), 100, getConfig().getInt("worldBounds.zMax"));

        for (String item : getConfig().getStringList("items")) {
            drops.add(new ItemData(item));
        }

        run_interval = getConfig().getInt("runInterval");

        Runnable task = new MelonDropTask();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, task, 20l, 20l);

        getServer().getPluginManager().registerEvents(new GTAMelonListener(), this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandSender player = sender;
        if (args.length == 0) {
            Location at = (MelonDropTask.wrapped == null ? null : MelonDropTask.wrapped.getLocation());
            if (at == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) &aThe &a&lGiant Magical Melon &ais nowhere to be found."));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l(!) &aThe &a&lGiant Magical Melon &ais at &e&n(X: "+at.getBlockX()+", Y: "+at.getBlockY()+", Z: "+at.getBlockZ()+")&r&a!"));
            }
            return true;
        } else {
            if (!(sender.isOp())) {
                sender.sendMessage(ChatColor.RED+"You do not have permission to use this command!");
                return true;
            }

            if (MelonDropTask.wrapped != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe &a&lGiant Magical Melon&r &cis still out in the wild! Destroy it before spawning another one!"));
                return true;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l(!) &aThe melon is dropping!"));
            MelonDropTask.seconds = getConfig().getInt("runInterval");

            return true;
        }
    }

    public void onDisable() {
        saveDefaultConfig();

        if (MelonDropTask.wrapped != null)
            MelonDropTask.wrapped.setType(Material.AIR);

        MelonDropTask.wrapped = null;
    }
}
