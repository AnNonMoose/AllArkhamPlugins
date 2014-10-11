package me.gtacraft.plugins.gangs.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;

import me.gtacraft.plugins.gangs.util.Formatting;
import me.gtacraft.plugins.gangs.util.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor
{
    private HashMap< String, SubCommand >		cmds 		= new HashMap< String, SubCommand >();
    private HashMap< String, String	>   		aliases 	= new HashMap< String, String >();

    public CommandHandler()
    {
        instance = this;
    }
    private static CommandHandler instance;
    public static CommandHandler getInstance()
    {
        return instance == null ? new CommandHandler() : instance;
    }

    public void registerCommandChannel(Object object)
    {
        for (Method m : object.getClass().getMethods())
        {
            Class<?>[] params = m.getParameterTypes();
            if (params.length == 2 && Player.class.isAssignableFrom(params[0]) && String[].class.equals(params[1]))
            {
                if (m.getAnnotation(CommandContext.class) == null)
                    continue;

                CommandContext anno = m.getAnnotation(CommandContext.class);
                if (anno.name().equals(""))
                    continue;

                SubCommand cmd = new SubCommand(object, m, anno.help());
                cmds.put(anno.name().toLowerCase(), cmd);

                if (!anno.aliases().equals(new String[]{""}))
                {
                    for (String a : anno.aliases())
                    {
                        aliases.put(a.toLowerCase(), anno.name().toLowerCase());
                    }
                }
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface CommandContext
    {
        String help() default "";
        String name() default "";
        String[] aliases() default {""};
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "A player is required to run this command!")));
            return true;
        }

        Player player = (Player)sender;
        if (args.length == 0 || args[0].equalsIgnoreCase("help"))
        {
            for (SubCommand value : cmds.values())
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', value.getHelp()));
            }
            return true;
        }

        String[] subArgs = new String[args.length - 1];
        for(int i = 1; i < args.length; i++)
            subArgs[i - 1] = args[i];

        String key = args[0].toLowerCase();
        if (!(cmds.containsKey(key)))
        {
            if (aliases.containsKey(key))
            {
                String actual = aliases.get(key);
                if (cmds.containsKey(actual))
                {
                    cmds.get(actual).run(player, subArgs);
                    return true;
                }
            }
        }
        else
        {
            cmds.get(key).run(player, subArgs);
            return true;
        }

        player.sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Unknown command! Type '/"+label+" help' for a list of commands!")));
        return true;
    }

    private class SubCommand
    {
        private Object invoke;
        private Method runnableMethod;
        private String help;

        public SubCommand(Object invoke, Method method, String help)
        {
            this.invoke = invoke;
            this.runnableMethod = method;
            this.help = help;
        }

        public String getHelp()
        {
            return help;
        }

        public void run(Player player, String[] args)
        {
            try { runnableMethod.invoke(invoke, player, args); }

            catch (Exception ex)
            {
                player.sendMessage(ChatColor.DARK_RED + "!!! "+ChatColor.RED + "An error occured while running this command" + ChatColor.DARK_RED + " !!!");
                ex.printStackTrace();
            }
        }
    }
}