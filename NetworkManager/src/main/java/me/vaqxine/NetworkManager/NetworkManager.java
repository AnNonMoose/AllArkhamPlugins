package me.vaqxine.NetworkManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import me.vaqxine.NetworkManager.lib.ServerMapper;
import me.vaqxine.NetworkManager.listeners.LoginListener;
import me.vaqxine.NetworkManager.tasks.CommandSenderTask;
import me.vaqxine.NetworkManager.tasks.NetworkListenerTask;
import me.vaqxine.NetworkManager.threads.SocketSenderThread;
import me.vaqxine.NetworkManager.utils.MessageUtils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NetworkManager extends JavaPlugin {

      public volatile NetworkAPI api;
      public volatile CommandSenderTask cst;
      public volatile SocketSenderThread sst;
      
      public volatile static NetworkManager plugin;

      public volatile int network_port = Bukkit.getPort() + 1000;
      public volatile boolean rebooting = false;

      public volatile static me.vaqxine.NetworkManager.Logger log = new me.vaqxine.NetworkManager.Logger();

      public volatile static String restartMessage = null;
      public volatile static String restartWarnMessage = null;
      public volatile String password = null;

      public volatile HashSet<String> loggedConsoleLines = new HashSet<>();
      private volatile DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
      public volatile List<String> lastReboots = new ArrayList<>();
      private Thread networkListenerTask = null;
      
      public static ServerMapper bungee_server_map = new ServerMapper();
      // List of server prefixes and the IP/port's they point to.
      // Contains player counts, max players, and status of every server.
      
      public static volatile HashSet<String> crashed_servers = new HashSet<String>();
      // List of all crashed servers, they will be ignored when getRandomServer() is fired.
      
      public static volatile CopyOnWriteArrayList<String> socket_pool = new CopyOnWriteArrayList<String>();
      // Packet data, address to send to
      
      public static boolean verbose = true;
      public static HashMap<String, Player> async_player_map = new HashMap<String, Player>();
      
      @Override
      public void onEnable() {
            plugin = this;

            getServer().getScheduler().cancelTasks(this);

            saveDefaultConfig();
            new File("plugins/NetworkManager/bungee-config.yml").delete();
            saveResource("bungee-config.yml", "plugins/NetworkManager/bungee-config.yml");
            loadBungeeConfig();
            
            password = getConfig().getString("password");
            restartMessage = MessageUtils.translateToColorCode(getConfig().getString("restartMessage"));
            restartWarnMessage = MessageUtils.translateToColorCode(getConfig().getString("restartWarnMessage"));

            api = new NetworkAPI(this);
            cst = new CommandSenderTask(this);
            
            sst = new SocketSenderThread(this);
            sst.start();
            
            getServer().getPluginManager().registerEvents(new LoginListener(this), this);
            getServer().getScheduler().runTaskTimer(this, cst, 20L, 20L);

            networkListenerTask = new NetworkListenerTask(this);
            networkListenerTask.start();

            ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new Filter() {
                  @Override
                  public Result filter(LogEvent event) {
                        if (loggedConsoleLines.size() >= 500) {
                              loggedConsoleLines.clear();
                        }
                        
                        loggedConsoleLines.add(df.format(new Date(event.getMillis())) + " " + event.getMessage().getFormattedMessage());
                        return Result.ACCEPT;
                  }

                  @Override
                  public Result getOnMatch() {
                        return Result.ACCEPT;
                  }

                  @Override
                  public Result getOnMismatch() {
                        return Result.ACCEPT;
                  }

                  @Override
                  public Filter.Result filter(Logger logger, Level level, Marker marker, String string, Object... os) {
                        return Result.ACCEPT;
                  }

                  @Override
                  public Filter.Result filter(Logger logger, Level level, Marker marker, Object o, Throwable thrwbl) {
                        return Result.ACCEPT;
                  }

                  @Override
                  public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable thrwbl) {
                        return Result.ACCEPT;
                  }
            });

            if (getConfig().getStringList("lastReboots") != null) {
                  lastReboots.addAll(getConfig().getStringList("lastReboots"));
            }

            lastReboots.add(String.valueOf(System.currentTimeMillis() / 1000L));
            getConfig().set("lastReboots", lastReboots);
            saveConfig();
      }

      @Override
      public void onDisable() {
            networkListenerTask.interrupt();
            
            cst = null;
            api = null;

            getServer().getScheduler().cancelTasks(this);
      }

      public CommandSenderTask getCommandSender() {
            return cst;
      }

      public NetworkAPI getAPI() {
            return api;
      }

      public static NetworkManager getPlugin() {
            return plugin;
      }
      
      public void loadBungeeConfig(){
          // Load our bungee config.
          FileConfiguration config = getConfig("bungee-config.yml");

          for(Entry<String, Object> data : config.getValues(true).entrySet()){
              String s = data.getKey();
              if(s.startsWith("servers.") && s.endsWith(".address")){
                  // It's a server.
                  // servers.scb1.address=127.0.0.1:1000
                  String server_prefix = s.split("\\.")[1];
                  String ip_and_port = (String)data.getValue();
                  bungee_server_map.put(server_prefix, ip_and_port);
                  System.out.println(server_prefix + "=" + ip_and_port);
              }
          }
      }
      
      public FileConfiguration getConfig(String configName) {
          return YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + configName));
      }
      
      public void saveResource(String resource_name, String output_path){
          InputStream stream = NetworkManager.class.getResourceAsStream("/" + resource_name);
          if (stream == null) {
              //send your exception or warning
          }
          OutputStream resStreamOut = null;
          int readBytes;
          byte[] buffer = new byte[4096];
          try {
              resStreamOut = new FileOutputStream(new File(output_path));
              while ((readBytes = stream.read(buffer)) > 0) {
                  resStreamOut.write(buffer, 0, readBytes);
              }
          } catch (IOException e1) {
              e1.printStackTrace();
          } finally {
              try {
                stream.close();
                resStreamOut.close();
            } catch (IOException e) {
                // Hypothetically impossible.
                e.printStackTrace();
            }
          }
      }
}
