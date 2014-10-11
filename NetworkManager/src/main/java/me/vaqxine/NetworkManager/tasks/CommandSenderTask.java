package me.vaqxine.NetworkManager.tasks;

import java.util.concurrent.CopyOnWriteArrayList;
import me.vaqxine.NetworkManager.NetworkManager;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandSenderTask extends BukkitRunnable {

      public volatile CopyOnWriteArrayList<String> commands_to_execute = new CopyOnWriteArrayList<>();
      public NetworkManager plugin;

      public CommandSenderTask(NetworkManager nm) {
            plugin = nm;
      }

      @Override
      public void run() {
            if (commands_to_execute.size() < 0) {
                  return;
            }
            for (String cmd : commands_to_execute) {
                  try {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
                  } catch (Exception err) {
                        err.printStackTrace();
                        continue;
                  }
            }

            commands_to_execute.clear();
      }

      public void addCmd(String cmd) {
            commands_to_execute.add(cmd);
      }
}
