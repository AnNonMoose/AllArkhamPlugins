package me.vaqxine.NetworkManager.tasks;

import me.vaqxine.NetworkManager.NetworkManager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerRebootTask extends BukkitRunnable {

      volatile int iteration = 60;

      public ServerRebootTask(boolean force) {
            NetworkManager.getPlugin().rebooting = true;

            if (force) {
                  iteration = 0;
            }
      }

      @Override
      public synchronized void run() {
            if (iteration < 0) {
                  return;
            }
            switch (iteration) {
                  case 60:
                        doBroadcast();
                        break;
                  case 45:
                        doBroadcast();
                        break;
                  case 30:
                        doBroadcast();
                        break;
                  case 15:
                        doBroadcast();
                        break;
                  case 10:
                        doBroadcast();
                        break;
                  case 5:
                        doBroadcast();
                        break;
                  case 4:
                        doBroadcast();
                        break;
                  case 3:
                        doBroadcast();
                        break;
                  case 2:
                        doBroadcast();
                        break;
                  case 1:
                        doBroadcast();
                        break;
                  case 0:
                        NetworkManager.log.debug("Saving all players...", this.getClass());
                        Bukkit.savePlayers();

                        NetworkManager.log.debug("Saving all worlds...", this.getClass());
                        for (World world : Bukkit.getWorlds()) {
                              world.save();
                        }

                        NetworkManager.log.debug("Stopping the server...", this.getClass());
                        Bukkit.shutdown();
                        cancel();
                        break;
            }

            iteration--;
      }

      private void doBroadcast() {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(NetworkManager.restartWarnMessage.replace("[time]", String.valueOf(iteration)));
            Bukkit.broadcastMessage("");
      }
}
