/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.commons.bungee;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.arkhamnetwork.arcade.core.Arcade;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author devan_000
 */
public class BungeecordHook {

    public static void sendPluginMessage(Player player, List<String> messages)
            throws IOException {
        Arcade.getInstance().log(
                ChatColor.DARK_GREEN
                + "[BungeeHook] Attempting to send plugin message "
                + messages.toString());

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        for (String message : messages) {
            out.writeUTF(message);
        }

        player.sendPluginMessage(Arcade.getInstance(), "BungeeCord",
                b.toByteArray());
        out.flush();
    }

}
