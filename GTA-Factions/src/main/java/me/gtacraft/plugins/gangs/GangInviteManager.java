package me.gtacraft.plugins.gangs;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 6/30/14. Designed for the GTA-Factions project.
 */

public class GangInviteManager {

    private static HashMap<Player, List<Gang>> invited = new HashMap<>();
    private static HashMap<Player, Integer> countdown = new HashMap<>();

    public static void zBegin() {
        Runnable sync = new Runnable() {
            public void run() {
                HashMap<Player, Integer> clone = new HashMap<>();
                for (Player key : countdown.keySet())
                    clone.put(key, countdown.get(key));

                for (Player key : clone.keySet()) {
                    int left = countdown.remove(key);
                    --left;
                    if (left < 0) {
                        countdown.remove(key);
                        invited.remove(key);
                    }

                    countdown.put(key, left);
                }
            }
        };
        Bukkit.getScheduler().scheduleSyncRepeatingTask(GTAGangs.getInstance(), sync, 20l, 20l);
    }

    public static void removeFromInvites(Player player) {
        invited.remove(player);
        countdown.remove(player);
    }

    public static void addPlayerToInvites(Player player, Gang gang) {
        if (invited.containsKey(player)) {
            List<Gang> in = invited.remove(player);
            in.add(gang);
            invited.put(player, in);
        } else {
            List<Gang> in = Lists.newArrayList();
            in.add(gang);
            invited.put(player, in);
        }
        countdown.put(player, GTAGangs.INVITE_DURATION);
    }

    public static List<Gang> findInvites(Player player) {
        return invited.get(player);
    }
}
