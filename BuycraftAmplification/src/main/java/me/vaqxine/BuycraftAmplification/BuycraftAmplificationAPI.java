package me.vaqxine.BuycraftAmplification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.vaqxine.BuycraftAmplification.database.SQLConnectionThread;
import me.vaqxine.BuycraftAmplification.libs.UUIDLibrary;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;

@SuppressWarnings("deprecation")
public class BuycraftAmplificationAPI {
	
	
    public static void queueLoginCommand(final String player_name, final String cmd, final boolean perform_command_as_player, String server){     
        if(server != null) server += ";"; else server = "";
        final String f_server = server;
        if(Bukkit.isPrimaryThread()){
            Bukkit.getScheduler().runTaskAsynchronously(BuycraftAmplification.getPlugin(), new Runnable(){
				public void run(){
                    UUID uuid = BuycraftAmplification.spoof_uuid ? UUIDLibrary.getSpoofedUUIDFromName(player_name) :  Bukkit.getOfflinePlayer(player_name).getUniqueId(); // UUID.nameUUIDFromBytes(("OfflinePlayer:" + pl.getName()).getBytes(Charsets.UTF_8));

                    List<String> queries = new ArrayList<String>();
                    // Make sure it exists.
                    queries.add("INSERT IGNORE INTO buycraft_amplification(uuid, commands_to_run, player_commands_to_run) VALUES('" + uuid + "', '', '')");

                    if(!perform_command_as_player){
                        // Update column.
                        queries.add("UPDATE buycraft_amplification SET commands_to_run=concat(commands_to_run,'@CMDSPLIT@" + f_server + StringEscapeUtils.escapeSql(cmd) + "') WHERE uuid='" + uuid + "'");
                    } else {
                        // Update column.
                        queries.add("UPDATE buycraft_amplification SET player_commands_to_run=concat(player_commands_to_run,'@CMDSPLIT@" + f_server + StringEscapeUtils.escapeSql(cmd) + "') WHERE uuid='" + uuid + "'");
                    }
                    
                    Connection con = null;
                    PreparedStatement pst = null;

                    for(String query : queries){
                        try {
                            pst = SQLConnectionThread.getConnection().prepareStatement(query);
                            pst.executeUpdate();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            BuycraftAmplification.log.error(query, this.getClass());

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

                    Bukkit.getLogger().info("When " + player_name + " (" + uuid + ") logs in on " + (f_server.length() > 0 ? f_server : "anywhere") + ", we will run '" + cmd + "'!");
                }
            });
        } else {
            UUID uuid = BuycraftAmplification.spoof_uuid ? UUIDLibrary.getSpoofedUUIDFromName(player_name) : Bukkit.getOfflinePlayer(player_name).getUniqueId(); 
            
            List<String> queries = new ArrayList<String>();
            // Make sure it exists.
            queries.add("INSERT IGNORE INTO buycraft_amplification(uuid, commands_to_run, player_commands_to_run) VALUES('" + uuid + "', '', '')");

            if(!perform_command_as_player){
                // Update column.
                queries.add("UPDATE buycraft_amplification SET commands_to_run=concat(commands_to_run,'@CMDSPLIT@" + f_server + StringEscapeUtils.escapeSql(cmd) + "') WHERE uuid='" + uuid + "'");
            } else {
                // Update column.
                queries.add("UPDATE buycraft_amplification SET player_commands_to_run=concat(player_commands_to_run,'@CMDSPLIT@" + f_server + StringEscapeUtils.escapeSql(cmd) + "') WHERE uuid='" + uuid + "'");
            }
            
            Connection con = null;
            PreparedStatement pst = null;

            for(String query : queries){
                try {
                    pst = SQLConnectionThread.getConnection().prepareStatement(query);
                    pst.executeUpdate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    BuycraftAmplification.log.error(query, BuycraftAmplificationAPI.class);

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

            Bukkit.getLogger().info("When " + player_name + " (" + uuid + ") logs in on " + (f_server.length() > 0 ? f_server : "anywhere") + ", we will run '" + cmd + "'!");
        }
    }
    
  @Deprecated
  public static void queueLoginCommand(final String player_name, final String cmd, final boolean perform_command_as_player){     
        
        if(Bukkit.isPrimaryThread()){
            Bukkit.getScheduler().runTaskAsynchronously(BuycraftAmplification.getPlugin(), new Runnable(){
                public void run(){
                    UUID uuid = BuycraftAmplification.spoof_uuid ? UUIDLibrary.getSpoofedUUIDFromName(player_name) :  Bukkit.getOfflinePlayer(player_name).getUniqueId(); // UUID.nameUUIDFromBytes(("OfflinePlayer:" + pl.getName()).getBytes(Charsets.UTF_8));

                    List<String> queries = new ArrayList<String>();
                    // Make sure it exists.
                    queries.add("INSERT IGNORE INTO buycraft_amplification(uuid, commands_to_run, player_commands_to_run) VALUES('" + uuid + "', '', '')");

                    if(!perform_command_as_player){
                        // Update column.
                        queries.add("UPDATE buycraft_amplification SET commands_to_run=concat(commands_to_run,'@CMDSPLIT@" + StringEscapeUtils.escapeSql(cmd) + "') WHERE uuid='" + uuid + "'");
                    } else {
                        // Update column.
                        queries.add("UPDATE buycraft_amplification SET player_commands_to_run=concat(player_commands_to_run,'@CMDSPLIT@" + StringEscapeUtils.escapeSql(cmd) + "') WHERE uuid='" + uuid + "'");
                    }
                    
                    Connection con = null;
                    PreparedStatement pst = null;

                    for(String query : queries){
                        try {
                            pst = SQLConnectionThread.getConnection().prepareStatement(query);
                            pst.executeUpdate();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            BuycraftAmplification.log.error(query, this.getClass());

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

                    Bukkit.getLogger().info("When " + player_name + " (" + uuid + ") logs in, we will run '" + cmd + "'!");
                }
            });
        } else {
            UUID uuid = BuycraftAmplification.spoof_uuid ? UUIDLibrary.getSpoofedUUIDFromName(player_name) : Bukkit.getOfflinePlayer(player_name).getUniqueId(); 
            
            List<String> queries = new ArrayList<String>();
            // Make sure it exists.
            queries.add("INSERT IGNORE INTO buycraft_amplification(uuid, commands_to_run, player_commands_to_run) VALUES('" + uuid + "', '', '')");

            if(!perform_command_as_player){
                // Update column.
                queries.add("UPDATE buycraft_amplification SET commands_to_run=concat(commands_to_run,'@CMDSPLIT@" + StringEscapeUtils.escapeSql(cmd) + "') WHERE uuid='" + uuid + "'");
            } else {
                // Update column.
                queries.add("UPDATE buycraft_amplification SET player_commands_to_run=concat(player_commands_to_run,'@CMDSPLIT@" + StringEscapeUtils.escapeSql(cmd) + "') WHERE uuid='" + uuid + "'");
            }
            
            Connection con = null;
            PreparedStatement pst = null;

            for(String query : queries){
                try {
                    pst = SQLConnectionThread.getConnection().prepareStatement(query);
                    pst.executeUpdate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    BuycraftAmplification.log.error(query, BuycraftAmplificationAPI.class);

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

            Bukkit.getLogger().info("When " + player_name + " (" + uuid + ") logs in, we will run '" + cmd + "'!");
        }
    }
}
