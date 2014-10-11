package me.gtacraft.plugins.gangs.listeners;

import lombok.Getter;
import me.gtacraft.plugins.gangs.GTAGangs;
import me.gtacraft.plugins.gangs.Gang;
import me.gtacraft.plugins.gangs.GangManager;
import me.gtacraft.plugins.gangs.GangMember;
import me.gtacraft.plugins.gangs.database.SQLConnectionThread;
import me.gtacraft.plugins.gangs.database.SQLVars;
import me.gtacraft.plugins.gangs.enumeration.GangRole;
import me.gtacraft.plugins.gangs.util.Formatting;
import me.gtacraft.plugins.gangs.util.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class GTAConnectDisconnectListener extends IListener {

    @Getter
    private static GTAConnectDisconnectListener instance;

    public GTAConnectDisconnectListener() {
        instance = this;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String uuid = event.getUniqueId().toString();
        try {
            PreparedStatement statement = SQLConnectionThread.getConnection().prepareStatement(
                    SQLVars.formatSqlCall(SQLVars.SELECT_PLAYER.replace("%uuid%", uuid)));

            statement.execute();

            ResultSet rs = statement.getResultSet();

            if (rs.next()) {
                String gang = rs.getString("gang");
                int role = rs.getInt("role");

                GangMember member = new GangMember(Bukkit.getOfflinePlayer(event.getUniqueId()), GangRole.fromPermissionLevel(role));
                Gang realGang = GangManager.getGang(gang);
                if (realGang == null && !(gang.equals("NULL"))) {
                    //load gang from database
                    SQLVars.loadGang(gang);
                }
                if (realGang != null) {
                    member.setGang(realGang);
                    realGang.getOnlineMembers().add(member);
                }
            } else {
                GTAGangs.sql_query.add(SQLVars.INSERT_NEW_PLAYER.replace("%uuid%", uuid));
                if (Bukkit.getOfflinePlayer(event.getUniqueId()).hasPlayedBefore())
                    new GangMember(Bukkit.getOfflinePlayer(event.getUniqueId()), GangRole.MEMBER);
            }
        } catch (Exception err) {
            err.printStackTrace();
            event.setKickMessage(Formatting.formatMessage(MessageType.ERROR.getPattern().replace("%m%", "Failed to load playerdata!")));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //load gang member and put in map
        Player player = event.getPlayer();
        GTAGangs.async_player_map.put(player.getName(), player);
        if (GangMember.fromPlayer(player) == null) {
            new GangMember(player, GangRole.MEMBER);
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Runnable async = new Runnable() {
            public void run() {
                GangMember member = GangMember.fromPlayer(event.getPlayer());
                if (member != null) {
                    GangMember.forget(member);
                    Gang gang = GangManager.getGang(member);
                    if (gang == null)
                        return;

                    gang.getOnlineMembers().remove(member);
                }
                GTAGangs.async_player_map.remove(event.getPlayer().getName());
            }
        };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(GTAGangs.getInstance(), async);
    }
}
