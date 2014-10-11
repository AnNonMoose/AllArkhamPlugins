package me.gtacraft.plugins.gangs;

import lombok.Getter;
import me.gtacraft.plugins.gangs.commands.CommandHandler;
import me.gtacraft.plugins.gangs.commands.GangStaffCommands;
import me.gtacraft.plugins.gangs.commands.UserCommands;
import me.gtacraft.plugins.gangs.database.SQLConnectionThread;
import me.gtacraft.plugins.gangs.database.SQLQueryThread;
import me.gtacraft.plugins.gangs.database.SQLVars;
import me.gtacraft.plugins.gangs.listeners.GTAConnectDisconnectListener;
import me.gtacraft.plugins.gangs.listeners.GTADamageListener;
import me.gtacraft.plugins.gangs.listeners.GTAMovementListener;
import me.gtacraft.plugins.gangs.listeners.GTAVoiceListener;
import me.gtacraft.plugins.gangs.util.LocationUtil;
import me.gtacraft.plugins.gangs.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class GTAGangs extends JavaPlugin {

    public static Logger log = new Logger();

    @Getter
    private static GTAGangs instance;

    public static volatile CopyOnWriteArrayList<String> sql_query = new CopyOnWriteArrayList<String>();
    // All SQL queries to run on ThreadPool.

    public static volatile HashMap<String, Player> async_player_map = new HashMap<String, Player>();
    // Thread safe player map

    public SQLQueryThread sql_worker;

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        loadConfigValues();

        TELEPORT_TIME = getConfig().getInt("TeleportTime");
        AUTO_SAVE_TIME = getConfig().getInt("AutoSaveTime");
        INVITE_DURATION = getConfig().getInt("InviteDuration");
        CHAT_FORMAT = getConfig().getString("ChatFormat");

        sql_worker = new SQLQueryThread();
        sql_worker.start();

        sql_query.add(SQLVars.formatSqlCall(SQLVars.CREATE_DATABASE));
        doSQLWork();
        sql_query.add(SQLVars.formatSqlCall(SQLVars.CREATE_GANGS_TABLE));
        sql_query.add(SQLVars.formatSqlCall(SQLVars.CREATE_PLAYERS_TABLE));
        doSQLWork();

        initListeners();

        loadGangs();
        getCommand("gang").setExecutor(CommandHandler.getInstance());
        new UserCommands();
        new GangStaffCommands();

        for (Player player : Bukkit.getOnlinePlayers()) {
            async_player_map.put(player.getName(), player);
            GTAConnectDisconnectListener.getInstance().onAsyncPlayerPreLogin(new AsyncPlayerPreLoginEvent(player.getName(), player.getAddress().getAddress(), player.getUniqueId()));
        }

        GangInviteManager.zBegin();

        Runnable autoSave = new Runnable() {
            @Override
            public void run() {
                autosave();
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, autoSave, AUTO_SAVE_TIME*20, AUTO_SAVE_TIME*20);
    }

    public static int TELEPORT_TIME;
    public static int AUTO_SAVE_TIME;
    public static int INVITE_DURATION;
    public static String CHAT_FORMAT;

    private void loadGangs() {
        long before = System.currentTimeMillis();
        try {
            PreparedStatement statement = SQLConnectionThread.getConnection().prepareStatement(SQLVars.formatSqlCall(SQLVars.GET_GANGS));

            statement.execute();

            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                String name = rs.getString("name");
                String members = rs.getString("members");
                List<OfflinePlayer> all = Util.fromString(members);
                int friendlyFire = rs.getInt("friendly_fire");
                String hideout = rs.getString("hideout");
                Location loc = null;
                if (hideout != null)
                    loc = LocationUtil.fromString(hideout);

                Gang gang = new Gang(all, name, friendlyFire);
                gang.setHideout(loc);
                GangManager.insertGang(gang);
            }

            long now = System.currentTimeMillis();
            int diff = (int)(now-before);
            log.log("Gangs loaded in "+diff+" millaseconds!", this.getClass());
        } catch (Exception err) {
            err.printStackTrace();
            log.error("Could not load gangs! Disabling plugin!", this.getClass());
            this.setEnabled(false);
        }
    }

    private void loadConfigValues() {
        FileConfiguration f = getConfig();
        SQLVars.SQL_HOST = f.getString("SQL.Host");
        SQLVars.SQL_DB = f.getString("SQL.DB");
        SQLVars.SQL_USER = f.getString("SQL.User");
        SQLVars.SQL_PASS = f.getString("SQL.Pass");
        SQLVars.SQL_PORT = f.getInt("SQL.Port");
        SQLVars.SQL_URL = SQLVars.formatSqlCall(f.getString("SQL.URL"));
    }

    private void initListeners() {
        new GTAConnectDisconnectListener();
        new GTADamageListener();
        new GTAMovementListener();
        new GTAVoiceListener();
    }

    public void doSQLWork() {
        for(String query : sql_query){
            Connection con = null;
            PreparedStatement pst = null;

            try {
                pst = SQLConnectionThread.getConnection().prepareStatement(query);
                pst.executeUpdate();

            } catch (Exception ex) {
                ex.printStackTrace();
                log.error(query, this.getClass());

            } finally {
                try {
                    if (pst != null) {
                        pst.close();
                    }
                    if (con != null) {
                        con.close();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            sql_query.remove(query);
        }
    }

    public void onDisable() {
        autosave();
    }

    public void autosave() {
        log.notice("Autosaving all gangs... Next save time in "+AUTO_SAVE_TIME+" seconds!", this.getClass());
        for (Gang gang : GangManager.getGangs()) {
            String members = Util.fromMembers(gang.getAllMembers());
            if (members.equals("")) {
                sql_query.add(SQLVars.DELETE_GANG.replace("%name%", gang.getName()));
            }

            sql_query.add(SQLVars.UPDATE_GANG
                    .replace("%members%", members)
                    .replace("%friendly_fire%", gang.isFriendlyFire() ? "1" : "0")
                    .replace("%hideout%", (gang.getHideout() == null ? "NULL" : LocationUtil.fromLocation(gang.getHideout())))
                    .replace("%name%", gang.getName()));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            GangMember member = GangMember.fromPlayer(player);
            String gang = (member.getGang() == null ? "NULL" : "'"+member.getGang().getName()+"'");
            String role = (member.getRole() == null ? "NULL" : member.getRole().getPermissionLevel()+"");
            sql_query.add(SQLVars.UPDATE_PLAYER.replace("%uuid", player.getUniqueId().toString()).replace("'%gang%'", gang).replace("%role%", role));
        }
    }
}
