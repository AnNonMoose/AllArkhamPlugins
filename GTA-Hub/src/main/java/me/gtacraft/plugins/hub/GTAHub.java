package me.gtacraft.plugins.hub;

import com.google.common.collect.Lists;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.gtacraft.plugins.hub.listener.GTACoreListener;
import me.gtacraft.plugins.hub.listener.GTAJoinLeaveListener;
import me.gtacraft.plugins.hub.special.WaterFountain;
import me.gtacraft.plugins.hub.task.ScanLocationTask;
import me.gtacraft.plugins.hub.util.GTAUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 7/8/14. Designed for the GTA-Hub project.
 */

public class GTAHub extends JavaPlugin {

    public static Location spawn;
    private static GTAHub instance;

    public static WaterFountain water_fountain;
    public static volatile HashMap<String, Player> async_player_map = new HashMap<>();
    public static WorldGuardPlugin wg;

    public static GTAHub get() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        new GTACoreListener();
        GTAJoinLeaveListener temp = new GTAJoinLeaveListener();

        GTAUtil.initCountdowns();

        String[] spawn = getConfig().getString("spawn").split(",");

        World wSpawn = Bukkit.getWorld(spawn[0]);
        double xSpawn = Double.parseDouble(spawn[1]);
        double ySpawn = Double.parseDouble(spawn[2]);
        double zSpawn = Double.parseDouble(spawn[3]);
        float yawSpawn = Float.parseFloat(spawn[4]);
        float pitchSpawn = Float.parseFloat(spawn[5]);

        this.spawn = new Location(wSpawn, xSpawn, ySpawn, zSpawn, yawSpawn, pitchSpawn);

        for (Player p : Bukkit.getOnlinePlayers()) {
            temp.onPlayerJoin(new PlayerJoinEvent(p, null));
        }

        wg = (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");

        ScanLocationTask task = new ScanLocationTask();
        for (String add : getConfig().getStringList("servers")) {
            ScanLocationTask.ports.add(add);
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, task, 20l, 20l);

        if (getConfig().contains("fountains")) {
            List<Location> wfLocs = Lists.newArrayList();
            for (String parse : getConfig().getStringList("fountains")) {
                String[] s = parse.split(",");
                World world = Bukkit.getWorld(s[0]);
                int x = Integer.parseInt(s[1]);
                int y = Integer.parseInt(s[2]);
                int z = Integer.parseInt(s[3]);
                wfLocs.add(new Location(world, x, y, z));
            }

            water_fountain = new WaterFountain(wfLocs);

            Runnable tick = new Runnable() {
                public void run() {
                    water_fountain.tick();
                }
            };
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, tick, 2l, 2l);
        }
    }

    public void onDisable() {
        saveDefaultConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"You must be a player to use this command!");
            return true;
        }

        if (!(sender.isOp())) {
            sender.sendMessage(ChatColor.RED+"You must be opped to run this command!");
            return true;
        }

        Player player = (Player)sender;

        Location standing = player.getLocation();

        String toString = standing.getWorld().getName()+","+standing.getBlockX()+","+standing.getBlockY()+","+standing.getBlockZ();

        List<String> conf = (getConfig().contains("fountains") ? getConfig().getStringList("fountains") : new ArrayList<String>());
        conf.add(toString);

        getConfig().set("fountains", conf);
        saveConfig();

        player.sendMessage(ChatColor.GREEN+"Water fountain location added!");
        return true;
    }
}
