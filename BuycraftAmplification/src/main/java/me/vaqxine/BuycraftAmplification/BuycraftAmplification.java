package me.vaqxine.BuycraftAmplification;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import me.vaqxine.BuycraftAmplification.database.SQLQueryThread;
import me.vaqxine.BuycraftAmplification.libs.UUIDLibrary;
import me.vaqxine.BuycraftAmplification.listeners.LoginListener;
import me.vaqxine.BuycraftAmplification.tasks.PendingCommandTask;
import me.vaqxine.NetworkManager.NetworkManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BuycraftAmplification extends JavaPlugin {
    private static BuycraftAmplification plugin;

    public static Logger log = new Logger();
    public static boolean spoof_uuid = true;
    
    public static volatile CopyOnWriteArrayList<String> sql_query = new CopyOnWriteArrayList<String>();
    // All SQL queries to run on ThreadPool.
    
    public static String local_server_prefix = null;

    private static SQLQueryThread sql_worker;

    public void onEnable(){
        plugin = this; 
        sql_worker = new SQLQueryThread();
        sql_worker.start();

        if(Bukkit.getPluginManager().isPluginEnabled("NetworkManager")){
            // local_server_prefix swag
            String ip_and_port = Bukkit.getIp() + ":" + Bukkit.getPort();
            if(NetworkManager.bungee_server_map.containsValue(ip_and_port)){
                for(Entry<String, String> server_data : NetworkManager.bungee_server_map.entrySet()){
                    if(server_data.getValue().equals(ip_and_port)){
                        local_server_prefix = server_data.getKey();
                        break;
                    }
                }
            }
        }
        
        this.getServer().getPluginManager().registerEvents(new LoginListener(), this);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new PendingCommandTask(), 60 * 20L, 60 * 20L);
        sql_query.add("CREATE TABLE IF NOT EXISTS buycraft_amplification(uuid varchar(64) PRIMARY KEY NOT NULL, commands_to_run LONGTEXT, player_commands_to_run LONGTEXT)");
    }

    public void onDisable(){
        sql_worker.doWork();
        sql_worker.interrupt();
        sql_worker = null;
        // TODO: Run rest of sql_query SYNC.
    }

    public static BuycraftAmplification getPlugin(){
        return plugin;
    }

    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(cmd.getName().equalsIgnoreCase("logincommand")){
            if(sender instanceof Player && !((Player)sender).isOp()) return true;
            final Player pl = sender instanceof Player ? ((Player)sender) : null;

            // /logincommand Vaquxine eco give Vaquxine 5000
            if(args.length < 1){
                if(pl != null){
                    pl.sendMessage("Syntax: /logincommand (server) <player_name> <full command syntax>");
                    pl.sendMessage("I.E. /logincommand prison3 Vaquxine eco give Vaquxine 500");
                } else {
                    log.debug("Syntax: /logincommand (server) <player_name> <full command syntax>", this.getClass());
                    log.debug("I.E. /logincommand prison3 Vaquxine eco give Vaquxine 500", this.getClass());
                }
                return true;
            }

            if(args.length == 1){
                // logincommand forcecheck
                final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                if(args[0].equalsIgnoreCase("forcecheck")){
                    Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable(){
                        public void run(){
                            for(Player pl : players){
                                // Check if they have any pending commands_to_run.
                                UUID uuid = spoof_uuid ? UUIDLibrary.getSpoofedUUIDFromName(pl.getName()) : pl.getUniqueId();
                                PendingCommandTask.runLoginCommands(uuid, pl.getName());
                            }
                        }
                    });
                } else {
                    if(pl != null){
                        pl.sendMessage("Syntax: /logincommand (server) <player_name> <full command syntax>");
                        pl.sendMessage("I.E. /logincommand prison3 Vaquxine eco give Vaquxine 500");
                    } else {
                        log.debug("Syntax: /logincommand (server) <player_name> <full command syntax>", this.getClass());
                        log.debug("I.E. /logincommand prison3 Vaquxine eco give Vaquxine 500", this.getClass());
                    }
                }
                
                return true;
            }
           // Not Used int index = 0;
            String server = args[0];
            // We need to make sure this is really a server param and not just omit.
            
            final String player_name = args[1];

            String c = "";
            for(int i = 2; i < args.length; i++){
                c += args[i] + " ";
            }

            if(c.endsWith(" ")) c = c.substring(0, c.length() - 1);
            
            if(Bukkit.getPlayer(player_name) != null){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
                return true;
            }
            
            BuycraftAmplificationAPI.queueLoginCommand(player_name, c, false, server.equals("*") ? null : server);
        }

        return true;
    }
}
