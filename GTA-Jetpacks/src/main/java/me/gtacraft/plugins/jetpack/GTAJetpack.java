package me.gtacraft.plugins.jetpack;

import me.gtacraft.plugins.jetpack.listener.JetpackListener;
import me.gtacraft.plugins.jetpack.task.JetTask;
import me.gtacraft.plugins.jetpack.util.item.ItemData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Connor on 7/11/14. Designed for the GTA-Jetpacks project.
 */

public class GTAJetpack extends JavaPlugin {

    private static GTAJetpack instance;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        for (String key : getConfig().getConfigurationSection("jetpacks").getKeys(false)) {
            int tier = Integer.parseInt(key);
            ItemData value = new ItemData(getConfig().getString("jetpacks."+key));

            JetPackManager.assignJetpackAndTier(value.getStack(), tier);
        }

        new JetpackListener();

        Runnable constant = new JetTask();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, constant, 1l, 1l);

        for (Player player : Bukkit.getOnlinePlayers()) {
            JetTask.async_player_map.put(player.getName(), player);
        }
    }

    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            JetTask.async_player_map.remove(player.getName());
        }

        saveDefaultConfig();
    }

    public static GTAJetpack getInstance() {
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender.isOp())) {
            sender.sendMessage(ChatColor.RED+"You do not have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED+"Please specify a player to give a jetpack to!");
            return true;
        }

        Player check = Bukkit.getPlayer(args[0]);
        if (check == null) {
            sender.sendMessage(ChatColor.RED+"No such player!");
            return true;
        }

        try {
            int pack = (args.length > 1 ? Integer.parseInt(args[1]) : 1);
            boolean talk = (args.length > 2 && (args[2].equalsIgnoreCase("-v") || args[2].equalsIgnoreCase("-verbose")));

            ItemStack give = JetPackManager.getPack(pack);
            if (give == null) {
                sender.sendMessage(ChatColor.RED+"There is no tier "+pack+" jetpack!");
                return true;
            }
            check.getInventory().addItem(give);
            if (talk)
                check.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l(!) &aYou were given a jetpack item!"));

            sender.sendMessage(ChatColor.GREEN+"Jetpack successfully given to "+check.getName()+"!");
            return true;
        } catch (Exception err) {
            sender.sendMessage(ChatColor.RED+"Please enter a valid number!");
            err.printStackTrace();
        }
        return true;
    }
}
