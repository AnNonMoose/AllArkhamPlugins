package me.gtacraft;

import com.google.common.collect.Lists;
import me.gtacraft.api.GTAGunsAPI;
import me.gtacraft.config.ConfigFile;
import me.gtacraft.config.ConfigFolder;
import me.gtacraft.gun.Gun;
import me.gtacraft.gun.GunData;
import me.gtacraft.gun.GunFactory;
import me.gtacraft.listener.GunListener;
import me.gtacraft.listener.PlayerListener;
import me.gtacraft.player.GunHolder;
import me.gtacraft.util.ExplosionUtil;
import me.gtacraft.util.GunUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;

/**
 * Created by Connor on 4/27/14. Designed for the GTA-Guns project.
 */

public class GTAGuns extends JavaPlugin {

    private static GTAGuns instance;
    private ConfigFolder gunsFolder;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        gunsFolder = new ConfigFolder(this, new File(getDataFolder(), File.separator+"guns"));

        reloadGuns();

        Bukkit.getPluginManager().registerEvents(new GunListener(), this);

        PlayerListener l = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(l, this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            l.onPlayerJoin(new PlayerJoinEvent(p, ""));
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new PlayerUpdateTimer(), GunHolder.TICK_INTERVAL, GunHolder.TICK_INTERVAL);
    }

    private void initConfig() {
        reloadConfig();

        GUN_NAME_TEXT = getConfig().getString("gunItemDisplay").replace("{->}", "»")
                .replace("{<-}", "«")
                .replace("{*}", "▪")
                .replace("{|>}", "▶")
                .replace("{<|}", "◀");

        RELOAD_TEXT = ChatColor.translateAlternateColorCodes('&', getConfig().getString("reloadingText")).replace("{->}", "»")
                .replace("{<-}", "«")
                .replace("{*}", "▪")
                .replace("{|>}", "▶")
                .replace("{<|}", "◀");

    }

    public void reloadGuns() {
        initConfig();
        gunsFolder = new ConfigFolder(this, new File(getDataFolder(), File.separator+"guns"));
        for (ConfigFile f : gunsFolder.getConfigFiles()) {
            GunFactory.blueprint(null, f.getFile(), "");
        }
    }

    private class PlayerUpdateTimer extends BukkitRunnable {
        public void run() {
            boolean dirty = false;
            for (GunHolder holder : GunHolder.getAllHolders().values()) {
                if (holder.tick())
                    dirty = true;
            }

            GunHolder.cleanNull();
        }
    }

    public static GTAGuns getInstnace() {
        return instance;
    }

    public void onDisable() {
        saveDefaultConfig();

        for (Item i : ExplosionUtil.getDeniedItems()) {
            i.remove();
        }
   }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender.isOp())) {
            sender.sendMessage(ChatColor.RED+"You do not have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED+"Format: /"+label+" <give,reload> (player) (gun)");
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length >= 2 && (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("*"))) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED+"You must be a player to use this command!");
                    return true;
                }

                Player player = (Player)sender;

                GunHolder holder = GunHolder.getHolder(player);
                if (holder.getCurrentWeapon() == null) {
                    player.sendMessage(ChatColor.RED+"Please hold the gun you wish to give to everyone!");
                    return true;
                }

                player.getInventory().remove(player.getItemInHand());

                GunData give = holder.getCurrentWeapon();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.equals(player))
                        continue;

                    GTAGunsAPI.giveGun(online, give.getModel());
                    online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou were given a &e"+give.getModel().getAttribute("name").getStringValue()+"&c&l!"));
                }

                player.sendMessage(ChatColor.GREEN+"You gave "+ChatColor.YELLOW+ (Bukkit.getOnlinePlayers().length-1)+ChatColor.GREEN+" people the gun in your hand!");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED+"Format: /"+label+" give <player> <gun>");
                return true;
            }

            Player find = Bukkit.getPlayer(args[1]);
            if (find == null) {
                sender.sendMessage(ChatColor.RED+"The given player must be online to use this command!");
                return true;
            }

            Gun g = GunFactory.getGun(args[2].replace("_", " "));
            if (g == null) {
                sender.sendMessage(ChatColor.RED+"The given gun does not exist!");
                return true;
            }

            ItemStack stack = GunUtil.getGunStack(g);
            find.getInventory().addItem(stack);
            find.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou were given a &e"+g.getAttribute("name").getStringValue()+"&c&l!"));
            sender.sendMessage(ChatColor.GREEN+"Gun given to "+find.getName()+"!");
            return true;
        } else {
            GunFactory.reload();
            GunHolder.resetAll();
            sender.sendMessage(ChatColor.GREEN+"Weapons reloaded!");
            return true;
        }
    }

    public static String GUN_NAME_TEXT;
    public static String RELOAD_TEXT;
}
