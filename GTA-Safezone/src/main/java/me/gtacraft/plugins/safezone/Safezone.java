package me.gtacraft.plugins.safezone;

import com.google.common.collect.Lists;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.gtacraft.economy.EconomyAPI;
import me.gtacraft.plugins.gtarespawn.GTARespawn;
import me.gtacraft.plugins.safezone.event.SafezoneListener;
import me.gtacraft.plugins.safezone.hook.WorldHooks;
import me.gtacraft.plugins.safezone.task.ScanLocationTask;
import me.gtacraft.plugins.safezone.util.SafezoneUtil;
import me.vaqxine.GTAWS.WantedAPI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Connor on 6/27/14. Designed for the GTA-Safezone project.
 */

public class Safezone extends JavaPlugin {

    @Getter
    private static Safezone instance;
    @Getter
    private List<ProtectedRegion> safeZoneRegions = Lists.newArrayList();
    @Getter
    private World world;

    public static volatile ConcurrentHashMap<Player, Integer> async_player_map = new ConcurrentHashMap<Player, Integer>();

    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        WorldHooks.init();

        world = Bukkit.getWorld(getConfig().getString("world"));

        for (String region : getConfig().getStringList("zones")) {
            ProtectedRegion pr = WorldHooks.getRegion(world, region);
            if (pr == null)
                continue;

            safeZoneRegions.add(pr);
        }
        new SafezoneListener();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new ScanLocationTask(), 5l, 5l);
        SafezoneUtil.tick();
    }

    public static HashMap<UUID, Integer> safezone_teleport_task_ids = new HashMap<UUID, Integer>();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("safezone")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED+"You must be a player to use this command!");
                return true;
            }

            final Player pl = (Player)sender;
            int wantedLevel = WantedAPI.getWantedLevel(pl.getName());

            final int taxiTime = (wantedLevel == 0 ? 6 : wantedLevel*10);
            if (taxiTime > 20) {
                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) &cYou cannot hail a taxi with your current wanted level!"));
                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7... No one wants to drive a criminal :(..."));
                return true;
            }

            if (SafezoneUtil.isInSafeZone(pl.getLocation())) {
                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) &cYou are already in a safezone!"));
                return true;
            }

            if (pl.getVehicle() != null && pl.getVehicle() instanceof Minecart) {
                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) &cYou cannot hail a taxi while in a car!"));
                return true;
            }

            pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l(!) &7A taxi will drive you to the nearest safezone!"));
            int taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                int i = 0;
                final Location cast_loc = pl.getLocation();

                public void run() {
                    if (pl == null || !pl.isOnline() || !safezone_teleport_task_ids.containsKey(pl.getUniqueId()))
                        return; // Cancelled soon enough.

                    if (SafezoneUtil.getSubscribedTime(pl.getName() + "_combat") != -1) {
                        // They're in combat, cancel task.
                        pl.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "(!)" + ChatColor.RED + " You took damage, the taxi driver was scared off!");
                        if (safezone_teleport_task_ids.containsKey(pl.getUniqueId())) {
                            int taskid = safezone_teleport_task_ids.get(pl.getUniqueId());
                            safezone_teleport_task_ids.remove(pl.getUniqueId());
                            Bukkit.getScheduler().cancelTask(taskid);
                            return;
                        }
                    }

                    if (pl.getLocation().distanceSquared(cast_loc) > 4.0D) {
                        // Too much movement.
                        pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l(!) &cYou moved, the taxi can't find you!"));
                        if (safezone_teleport_task_ids.containsKey(pl.getUniqueId())) {
                            // Cancel task.
                            int taskid = safezone_teleport_task_ids.get(pl.getUniqueId());
                            safezone_teleport_task_ids.remove(pl.getUniqueId());
                            Bukkit.getScheduler().cancelTask(taskid);
                            return;
                        }
                    }

                    i++;
                    if (i >= taxiTime + 1) {
                        // Teleport! Yay!
                        Bukkit.getServer().getScheduler().runTask(Safezone.this, new Runnable() {
                            public void run() {
                                pl.teleport(GTARespawn.getClosest(pl.getLocation()));
                                double cash = EconomyAPI.getUserBalance(pl.getUniqueId());
                                boolean charge = (cash >= 2);
                                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l(!) &aThe taxi dropped you off at the closest safe zone!"));
                                if (charge) {
                                    pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You paid &a&l$2&r&7 to the taxi driver!"));
                                    EconomyAPI.setUserBalance(pl.getUniqueId(), cash - 2);
                                }
                                pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                            }
                        });

                        int taskid = safezone_teleport_task_ids.get(pl.getUniqueId());
                        safezone_teleport_task_ids.remove(pl.getUniqueId());
                        Bukkit.getScheduler().cancelTask(taskid);
                        return;
                    } else {
                        pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7The taxi will arrive in &e" + (taxiTime - i) + " &7second(s)!"));
                        pl.playSound(pl.getLocation(), Sound.CLICK, 1F, 2F);
                    }

                }
            }, 20L, 20L).getTaskId();
            safezone_teleport_task_ids.put(pl.getUniqueId(), taskID);

            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"You must be a player to use this command!");
            return true;
        }

        if (!(sender.hasPermission("safezone.addsafezone"))) {
            sender.sendMessage(ChatColor.RED+"You do not have permission to use this command!");
            return true;
        }

        Player player = (Player)sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED+"Please specify a region to add as a safe zone!");
            return true;
        }

        String zoneName = args[0];
        for (ProtectedRegion possibleMatch : WorldHooks.getAllRegions(player.getWorld())) {
            if (possibleMatch.getId().equalsIgnoreCase(zoneName)) {
                String saveConf = possibleMatch.getId();

                List<String> config = getConfig().getStringList("zones");
                config.add(saveConf);
                getConfig().set("zones", config);
                saveConfig();
                reloadConfig();

                safeZoneRegions.add(possibleMatch);

                player.sendMessage(ChatColor.GREEN+"Safe zone location saved! Region ID: "+ChatColor.YELLOW+saveConf+ChatColor.GREEN+"!");
                return true;
            }
        }

        player.sendMessage(ChatColor.RED+"No such region in this world!");
        return true;
    }

    public void onDisable() {
        saveDefaultConfig();
    }
}
