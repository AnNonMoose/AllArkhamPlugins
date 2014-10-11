package me.gtacraft.plugins.gangs.listeners;

import com.google.common.collect.Lists;
import me.gtacraft.plugins.gangs.util.Formatting;
import me.gtacraft.plugins.gangs.util.MessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/**
 * Created by Connor on 6/30/14. Designed for the GTA-Factions project.
 */

public class GTAMovementListener extends IListener {

    public static List<Player> going_home = Lists.newArrayList();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!(event.getFrom().getBlock().equals(event.getTo().getBlock()))) {
            if (going_home.remove(event.getPlayer())) {
                event.getPlayer().sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Teleportation cancelled! You cannot move when about to teleport")));
            }
        }
    }
}
