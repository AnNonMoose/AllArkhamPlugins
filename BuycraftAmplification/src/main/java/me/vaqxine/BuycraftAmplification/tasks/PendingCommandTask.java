package me.vaqxine.BuycraftAmplification.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.UUID;

import me.vaqxine.BuycraftAmplification.BuycraftAmplification;
import me.vaqxine.BuycraftAmplification.database.SQLConnectionThread;
import me.vaqxine.BuycraftAmplification.libs.UUIDLibrary;
import me.vaqxine.NetworkManager.NetworkManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PendingCommandTask implements Runnable {

    private static boolean network_mgr = false;
    public PendingCommandTask(){
        network_mgr = Bukkit.getPluginManager().isPluginEnabled("NetworkManager");
    }

    @Override
    public void run() {
        Collection<? extends Player> players;
        try { players = Bukkit.getOnlinePlayers(); } catch (ConcurrentModificationException cme){
            BuycraftAmplification.log.error("Could not run PendingCommandTask due to CME exception.", this.getClass());
            return;
        }

        for(Player pl : players){
            if(pl.hasMetadata("NPC")) continue;
            // Check if they have any pending commands_to_run.
            UUID uuid = BuycraftAmplification.spoof_uuid ? UUIDLibrary.getSpoofedUUIDFromName(pl.getName()) : pl.getUniqueId();
            runLoginCommands(uuid, pl.getName());
        }
    }

    public static void runLoginCommands(final UUID uuid, String p){
        PreparedStatement pst = null;
        String commands = null;
        String player_commands = null;
        String query = "";

        query = "SELECT commands_to_run, player_commands_to_run from buycraft_amplification WHERE uuid='" + uuid + "'"; 

        try {
            pst = SQLConnectionThread.getConnection().prepareStatement(query);
            pst.execute();
            ResultSet rs = pst.getResultSet();

            while(rs.next()){
                commands = rs.getString("commands_to_run");
                player_commands = rs.getString("player_commands_to_run");
            }

            rs.close();
            pst.close();

        } catch (Exception ex) {
            // TODO Clear the pending commands?
            ex.printStackTrace();
            BuycraftAmplification.log.error(query, PendingCommandTask.class);
        }

        String new_server_command_cache = "";
        String new_player_command_cache = "";
        boolean ran_cmd = false;
        
        if(commands != null && commands.length() > 0){
            if(commands.contains("@CMDSPLIT@")){
                // Multi.
                for(String cmd : commands.split("@CMDSPLIT@")){
                    if(cmd != null && cmd.length() > 0){
                        if(cmd.contains(";")){
                            if(!network_mgr){
                                // We can't determine if this is the correct server, assume it's wrong.
                                new_server_command_cache += cmd + "@CMDSPLIT@";
                                continue;
                            }
                            // Specific server.
                            String server = cmd.split(";")[0];
                            if(NetworkManager.bungee_server_map.containsKey(server)){
                                // It's a legit server.
                                if(!BuycraftAmplification.local_server_prefix.toLowerCase().startsWith(server.toLowerCase())){
                                    // Not where it needs to run.
                                    new_server_command_cache += cmd + "@CMDSPLIT@";
                                    Bukkit.getLogger().info("Skipping command " + cmd + " due to wrong SERVER value!");
                                    continue;
                                } else {
                                    // Clean it up so the local server can run it.
                                    cmd = cmd.split(";")[1]; // cmd.replace(server + ";", "");
                                }
                            }
                        }

                        if(cmd.contains(";")){
                            cmd = cmd.split(";")[1];
                        }
                        
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        BuycraftAmplification.log.debug("Ran command '" + cmd + "' on behalf of " + p + "!", PendingCommandTask.class);
                        ran_cmd = true;
                    }
                }
            } else {
                // Singular.
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands);
                BuycraftAmplification.log.debug("Ran command '" + commands + "' on behalf of " + p + "!", PendingCommandTask.class);
                ran_cmd = true;
            }
        }

        if(player_commands != null && player_commands.length() > 0){
            final String f_player_commands = player_commands;

            if(f_player_commands.contains("@CMDSPLIT@")){
                // Multi.
                for(String cmd : f_player_commands.split("@CMDSPLIT@")){
                    if(cmd != null && cmd.length() > 0){
                        if(cmd.contains(";")){
                            if(!network_mgr){
                                // We can't determine if this is the correct server, assume it's wrong.
                                new_server_command_cache += cmd + "@CMDSPLIT@";
                                continue;
                            }
                            // Specific server.
                            String server = cmd.split(";")[0];
                            if(NetworkManager.bungee_server_map.containsKey(server)){
                                // It's a legit server.
                                if(!BuycraftAmplification.local_server_prefix.toLowerCase().startsWith(server.toLowerCase())){
                                    // Not where it needs to run.
                                    Bukkit.getLogger().info("Skipping command " + cmd + " due to wrong SERVER value!");
                                    new_player_command_cache += cmd + "@CMDSPLIT@";
                                    continue;
                                } else {
                                    // Clean it up so the local server can run it.
                                    cmd = cmd.replace(server + ";", "");
                                    ran_cmd = true;
                                }
                            }
                        }

                        final String f_cmd = cmd;
                        Bukkit.getScheduler().runTaskLater(BuycraftAmplification.getPlugin(), new Runnable(){
                            public void run(){
                                Player pl = Bukkit.getPlayer(uuid);
                                if(pl == null) return; // Can't execute SHIT.

                                pl.performCommand(f_cmd.replace("/", ""));
                                BuycraftAmplification.log.debug("Ran command '" + f_cmd + "' on behalf of " + pl.getName() + "!", PendingCommandTask.class);
                            }
                        }, 20L);
                    }
                }
            } else {
                // Singular.
                Bukkit.getScheduler().runTaskLater(BuycraftAmplification.getPlugin(), new Runnable(){
                    public void run(){
                        Player pl = Bukkit.getPlayer(uuid);
                        if(pl == null) return; // Can't execute SHIT.

                        pl.performCommand(f_player_commands.replace("/", ""));
                        BuycraftAmplification.log.debug("Ran command '" + f_player_commands + "' on behalf of " + pl.getName() + "!", PendingCommandTask.class);
                    }
                }, 20L);
            }

        }

        if(ran_cmd && (commands.length() > 0 || player_commands.length() > 0)){
            // Put the commands we couldn't execute back in SQL.
            updateLoginCommandsSync(uuid, new_server_command_cache, new_player_command_cache);
        }
    }

    public static void updateLoginCommandsSync(UUID uuid, String server_commands, String player_commands){
        Connection con = null;
        PreparedStatement pst = null;
        String query = "UPDATE buycraft_amplification SET commands_to_run='" + server_commands + "', player_commands_to_run='" + player_commands + "' WHERE uuid='" + uuid + "'";

        try {
            pst = SQLConnectionThread.getConnection().prepareStatement(query);
            pst.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            BuycraftAmplification.log.error(query, PendingCommandTask.class);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}
