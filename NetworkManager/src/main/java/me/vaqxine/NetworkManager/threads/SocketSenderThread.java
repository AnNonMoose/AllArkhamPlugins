package me.vaqxine.NetworkManager.threads;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import me.vaqxine.NetworkManager.NetworkManager;
import me.vaqxine.NetworkManager.lib.PacketControl;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;

public class SocketSenderThread extends Thread {

    public static NetworkManager plugin = null;
    public static List<String> socket_pool_to_remove = new ArrayList<String>();

    boolean running = false;

    public SocketSenderThread(NetworkManager plugi){
        plugin = plugi;
    }

    public static String getPrefixFromIP(String ip){
        for(Entry<String, String> data : NetworkManager.bungee_server_map.entrySet()){
            if(data.getValue().equalsIgnoreCase(ip))
                return data.getKey();
        }

        return null;
    }

    @Override
    public synchronized void run(){
        while(true){
            try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
            PacketControl pc = new PacketControl();

            for(String query_data : NetworkManager.socket_pool){
                // prison@ARk-432423432-^ImAPacket^
                String query = query_data.split("@")[1];
                String IP = query_data.split("@")[0];
                List<String> target_list = new ArrayList<String>();

                if(IP.equalsIgnoreCase("*")){
                    // Group these queries together.				    
                    for(String s : NetworkManager.bungee_server_map.values()){
                        if(s.equalsIgnoreCase(Bukkit.getIp() + ":" + Bukkit.getPort()))
                            continue; // Don't send to yourself.

                        target_list.add(s);
                    }
                }
                else{ // Singular target
                    target_list.add(IP);
                }

                for(String ip_and_port : target_list){
                    pc.addPacket(getPrefixFromIP(ip_and_port), query);
                }

                NetworkManager.socket_pool.remove(query_data);
            }

            int sent_count = 0;
            for(String server_prefix : pc.getServers()){
                String ip_and_port = NetworkManager.bungee_server_map.get(server_prefix);

                if(NetworkManager.crashed_servers.contains(server_prefix)){
                    Bukkit.getLogger().info("sendSocketAsync() Skipping down server, " + ip_and_port + " (" + server_prefix + ")");
                    continue; // Down'd server.
                }

                Socket kkSocket = null;
                PrintWriter out = null;

                try {
                    String ip = ip_and_port;
                    int port = 10010; // default, will be set bellow.

                    if(ip_and_port.contains(":")){
                        // Port specified, grab it.
                        ip = ip_and_port.split(":")[0];
                        port = Integer.parseInt(ip_and_port.split(":")[1]);
                    }

                    kkSocket = new Socket();
                    kkSocket.connect(new InetSocketAddress(ip, (port+1000)), 100);
                    out = new PrintWriter(kkSocket.getOutputStream(), false);
                    
                    String constructed_packet = "";
                    for(String packet : pc.getPackets(server_prefix)){
                        constructed_packet += packet + "@net_split@";
                        sent_count++;
                        // NetworkManager.log.debug("Sent '" + packet + "' to " + ip + ":" + (port+1000), this.getClass());
                    }
                    
                    out.print(constructed_packet + "\r\n");
                    out.flush();
                    out.close();
                    
                } catch (Exception err) {
                    if(kkSocket != null){
                        try {kkSocket.close();} catch (IOException ioerr) {}
                    }

                    if(out != null){
                        out.close();
                    }

                    // NetworkManager.log.info("[NetworkManager] Problematic packet - " + query + " @ " + ip_and_port);
                    // NetworkManager.socket_pool.remove(query);

                    if(err instanceof SocketTimeoutException){
                        Bukkit.getLogger().info("(DE) Set " + server_prefix + " to OFFLINE due to SocketTimeout!");
                        NetworkManager.crashed_servers.add(server_prefix);
                    }
                    
                    if(!(err instanceof ConnectException)){
                        err.printStackTrace();
                    }
                   
                    continue;
                } finally {
                    if(out != null){
                        out.flush();
                        out.close();
                    }

                    if(kkSocket != null){
                        try {kkSocket.close();} catch (IOException ioerr) {ioerr.printStackTrace();}
                    }
                }
            }
            
            if(sent_count > 0 && NetworkManager.verbose){
                System.out.println("(DE) Sent " + sent_count + " individual data vectors.");
            }
            
            pc.destroy();
        }
    }
    
    
    public static void queueSocket(String packet, String remote_server){
        if(remote_server.equals("*")){
            // Everybody up in the club getting meow meow
            NetworkManager.socket_pool.add("*@" + packet);
            return;
        }
        
        if(NetworkManager.bungee_server_map.containsKey(remote_server)){
            // Convert to raw ip:port!
            remote_server = NetworkManager.bungee_server_map.get(remote_server);
            NetworkManager.socket_pool.add(remote_server + "@" + packet);
        }
        
        Bukkit.getLogger().warning("Did not queue packet due to no server resolution for '" + remote_server + "'!");
    }
}
