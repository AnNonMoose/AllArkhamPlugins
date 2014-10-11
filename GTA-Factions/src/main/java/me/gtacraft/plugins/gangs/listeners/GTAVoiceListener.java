package me.gtacraft.plugins.gangs.listeners;

import me.gtacraft.plugins.gangs.GTAGangs;
import me.gtacraft.plugins.gangs.Gang;
import me.gtacraft.plugins.gangs.GangManager;
import me.gtacraft.plugins.gangs.GangMember;
import me.gtacraft.plugins.gangs.util.Formatting;
import me.gtacraft.plugins.gangs.util.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class GTAVoiceListener extends IListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        GangMember member = GangMember.fromPlayer(event.getPlayer());
        if (!(event.getMessage().startsWith("@")) && !member.isGangChat()) {
            if (member.getGang() != null) {
                event.setFormat(ChatColor.translateAlternateColorCodes('&', GTAGangs.CHAT_FORMAT.replace("%GANG%", member.getRole().getPrefix()+member.getGang().getName()))+event.getFormat());
            }
            return;
        }

        event.setCancelled(true);

        Gang gang = member.getGang();
        if (gang == null) {
            event.getPlayer().sendMessage(Formatting.formatMessage(MessageType.ERROR.getPattern()
                    .replace("%m%", "You cannot use group chat if you are not in a gang!")));

            return;
        }

        String message = event.getMessage();
        if (event.getMessage().startsWith("@") && !member.isGangChat()) {
            message = message.substring(1);
        }

        String format = Formatting.formatMessage(MessageType.GROUP_CHAT.getPattern()
                .replace("%r%", member.getRole().toString())
                .replace("%p%", event.getPlayer().getName())
                .replace("%m%", message));

        for (GangMember online : gang.getOnlineMembers()) {
            online.getWrapped().getPlayer().sendMessage(format);
        }
    }
}
