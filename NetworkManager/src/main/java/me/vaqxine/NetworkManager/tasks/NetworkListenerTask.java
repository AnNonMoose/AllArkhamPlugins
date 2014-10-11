package me.vaqxine.NetworkManager.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import me.vaqxine.NetworkManager.NetworkManager;
import me.vaqxine.NetworkManager.struct.QueryRequest;
import org.bukkit.Bukkit;

public class NetworkListenerTask extends Thread {

      private volatile InetAddress lAddress;
      private volatile NetworkManager plugin = null;
      private volatile BufferedReader in = null;
      private volatile Socket clientSocket = null;
      public volatile static ServerSocket ss = null;

      public NetworkListenerTask(NetworkManager NM) {
            plugin = NM;

            try {
                  lAddress = InetAddress.getByName(Bukkit.getIp());
                  ss = new ServerSocket(plugin.network_port, 500, lAddress);
            } catch (IOException ioerr) {
                  ioerr.printStackTrace();
                  NetworkManager.log.error("Failed to assign port listener on " + plugin.network_port + "!", this.getClass());
                  return;
            }

            NetworkManager.log.debug("LISTENING on port " + plugin.network_port, this.getClass());
      }

      @Override
      public void run() {
            while (true) {
                  try {
                        if (ss != null && !ss.isClosed()) {
                              clientSocket = ss.accept();

                              in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                              String inputLine;

                              HashSet<String> queries_to_process = new HashSet<>();
                              while (queries_to_process.size() <= 0 && (inputLine = in.readLine()) != null) {
                                    NetworkManager.log.debug("Processing packet: " + inputLine, this.getClass());
                                    queries_to_process.add(inputLine);
                              }

                              PacketProcessQueue.process(new QueryRequest(clientSocket, queries_to_process.toArray(new String[queries_to_process.size()])), plugin);

                              queries_to_process.clear();
                              queries_to_process = null;
                              clientSocket = null;
                        }
                  } catch (Exception e) {
                        e.printStackTrace();
                        NetworkManager.log.error("Lethal exception thrown, recovering listener!", this.getClass());
                        continue;
                  } finally {
                        if (in != null) {
                              try {
                                    in.close();
                                    in = null;
                              } catch (IOException ioerr) {
                                    ioerr.printStackTrace();
                              }
                        }
                  }
            }
      }
}
