package me.vaqxine.NetworkManager.tasks;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.arkhamnetwork.permissions.ArkhamPermissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.vaqxine.NetworkManager.NetworkManager;
import me.vaqxine.NetworkManager.struct.QueryRequest;
import me.vaqxine.NetworkManager.utils.LogUtils;
import me.vaqxine.NetworkManager.utils.PacketUtils;
import me.vaqxine.NetworkManager.utils.StatusUtils;

import com.google.gson.Gson;

public class PacketProcessQueue {

      private static volatile HashSet<String> download_queue = new HashSet<>();
      private static volatile Gson gson = new Gson();

      @SuppressWarnings("deprecation")
	public synchronized static void process(final QueryRequest querie, final NetworkManager plugin) {
            if (querie == null || querie.getSocket() == null) {
                  return;
            }

            String[] data_iteration;
            // We will use @net_split@ in the future for cross-server communications to send multiple independent packets in 1 socket. 
            if (querie.getQuery().length == 1 && querie.getQuery()[0].contains("@net_split@")) {
                  data_iteration = querie.getQuery()[0].split("@net_split@");
            } else {
                  data_iteration = querie.getQuery();
            }

            for (String data : data_iteration) {
                  try {
                        if (!data.startsWith("ARK-")) {
                              // We don't care, wrong packet format.
                              NetworkManager.log.debug("Bad packet format: '" + data + "'", PacketProcessQueue.class);
                              continue;
                        }

                        String[] dataSplit = data.split("-", 3);

                        if (dataSplit.length < 3) {
                              NetworkManager.log.debug("Bad packet length: '" + data + "'", PacketProcessQueue.class);
                              continue;
                        }

                        //Password validation.
                        if (!dataSplit[1].equalsIgnoreCase(plugin.password)) {
                              NetworkManager.log.debug("Invalid password supplied: '" + data + "'", PacketProcessQueue.class);
                              continue;
                        }

                        data = dataSplit[2];

                        if (data == null) {
                              continue;
                        }

                        if (data.startsWith("reloadpermissions")) {
                            if(!Bukkit.getPluginManager().isPluginEnabled("ArkhamPermissions")) continue; // We can't do anything lol.
                              data = data.replace("reloadpermissions", "");
                              data = data.replace("{", "").replace("}", "");
                              // struct; {uuid1,uuid2,uuid3}
                              if(data.contains(",")){
                                  for(String uuid : data.split(",")){
                                      if(NetworkManager.async_player_map.containsKey(uuid)){
                                          // Relevant to us, we should reload him!
                                          Player pl = NetworkManager.async_player_map.get(uuid);
                                          ArkhamPermissions.getInstance().getPermissionManager().reloadPlayer(pl);
                                          Bukkit.getLogger().info("Reloaded permissions for " + pl.getName() + "! (" + uuid + ")");
                                      }
                                  }
                              } else {
                                  String uuid = data;
                                  if(NetworkManager.async_player_map.containsKey(uuid)){
                                      // Relevant to us, we should reload him!
                                      Player pl = NetworkManager.async_player_map.get(uuid);
                                      ArkhamPermissions.getInstance().getPermissionManager().reloadPlayer(pl);
                                      Bukkit.getLogger().info("Reloaded permissions for " + pl.getName() + "! (" + uuid + ")");
                                  }
                              }
                              
                              continue;
                        }
                        
                        if (data.startsWith("runcmd->")) {
                              data = data.replace("runcmd->", "");
                              plugin.getCommandSender().addCmd(data);
                              continue;
                        }

                        if (data.startsWith("reboot")) {
                              if (download_queue.size() > 0) {
                                    NetworkManager.log.debug("Ignoring reboot packet due to " + download_queue.size() + " active downloads.", PacketProcessQueue.class);
                                    continue;
                              }
                              plugin.getServer().getScheduler().runTaskTimer(plugin, new ServerRebootTask(false), 20L, 20L);
                              continue;
                        }

                        if (data.startsWith("status")) {
                              try (DataOutputStream out = new DataOutputStream(querie.getSocket().getOutputStream())) {
                                    out.writeBytes(StatusUtils.getStatus());
                                    out.flush();
                              }
                              continue;
                        }

                        if (data.startsWith("basicstatus")) {
                              try (DataOutputStream out = new DataOutputStream(querie.getSocket().getOutputStream())) {
                                    out.writeBytes(StatusUtils.getBasicStatus());
                                    out.flush();
                              }
                              continue;
                        }

                        if (data.startsWith("consolelog")) {
                              try (DataOutputStream out = new DataOutputStream(querie.getSocket().getOutputStream())) {
                                    out.writeBytes(LogUtils.getAndClearLogLines());
                                    out.flush();
                              }
                              continue;
                        }

                        if (data.startsWith("lastreboots")) {
                              try (DataOutputStream out = new DataOutputStream(querie.getSocket().getOutputStream())) {
                                    out.writeBytes(gson.toJson(plugin.lastReboots));
                                    out.flush();
                              }
                              continue;
                        }

                        if (data.startsWith("forcereboot")) {
                              plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new ServerRebootTask(true));
                              continue;
                        }

                        if (data.startsWith("download")) {
                              // Download any file.
                              data = data.replace("download@", "");
                              final String url = data.split("->")[0];
                              final String file = data.split("->")[1];

                              // Run a new thread to for file / network IO so we don't hold up other sockets.
                              download_queue.add(url + "->" + file);
                              new Thread() {
                                    @Override
                                    public void run() {
                                          new File(plugin.getServer().getWorldContainer() + file).delete();
                                          try {
                                                PacketUtils.saveUrl(plugin.getServer().getWorldContainer() + file, url);
                                          } catch (IOException err) {
                                                err.printStackTrace();
                                          }
                                          download_queue.remove(url + "->" + file);
                                          interrupt();
                                    }
                              }.start();
                        }

                  } catch (Exception ex) {
                        ex.printStackTrace();
                  } finally {
                        try {
                              if (querie != null && querie.getSocket() != null) {
                                    querie.getSocket().close();
                              }
                        } catch (IOException ex) {
                              ex.printStackTrace();
                        }
                  }
            }
      }
}
