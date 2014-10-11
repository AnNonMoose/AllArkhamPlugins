package me.gtacraft.plugins.donorperks.commands;

import me.gtacraft.plugins.donorperks.util.MSG;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Connor on 7/16/14. Designed for the GTA-Donorperks project.
 */

public class CommandHandler implements CommandExecutor {

    private static HashMap<String, SubCommand> commands = new HashMap<String, SubCommand>();

    public CommandHandler() {
        commands.put("boom", new BoomCommand());
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(MSG.f("&cYou must be a player to use this command!"));
            return true;
        }

        Player player = (Player)commandSender;

        String cmdName = command.getName();
        if (commands.containsKey(cmdName.toLowerCase())) {
            //is command

        }
        return true;
    }
}
