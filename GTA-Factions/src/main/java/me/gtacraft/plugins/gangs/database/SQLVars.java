package me.gtacraft.plugins.gangs.database;

import me.gtacraft.plugins.gangs.GTAGangs;
import me.gtacraft.plugins.gangs.Gang;
import me.gtacraft.plugins.gangs.GangManager;
import me.gtacraft.plugins.gangs.util.LocationUtil;
import me.gtacraft.plugins.gangs.util.Util;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class SQLVars {

    public static String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS `%db%`;";
    //uuid - gang - role
    public static String CREATE_PLAYERS_TABLE = "CREATE TABLE IF NOT EXISTS `%db%`.`userdata` (" +
            "`uuid` varchar(48) NOT NULL, " +
            "`gang` varchar(255), " +
            "`role` INTEGER, " +
            "PRIMARY KEY (`uuid`)"+
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;";

    public static String CREATE_GANGS_TABLE = "CREATE TABLE IF NOT EXISTS `%db%`.`gangs` (" +
            "`id` int(11) NOT NULL AUTO_INCREMENT, " +
            "`name` varchar(10) NOT NULL, " +
            "`members` longtext NOT NULL, " +
            "`friendly_fire` tinyint(3) NOT NULL, " +
            "`hideout` varchar(255), " +
            "PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;";

    public static String GET_GANGS = "SELECT * FROM gangs;";
    public static String GET_GANG = "SELECT members, friendly_fire, hideout FROM gangs WHERE name LIKE '%name%';";
    public static String INSERT_NEW_PLAYER = "INSERT INTO userdata(uuid, gang, role) VALUES('%uuid%',NULL,NULL);";
    public static String SELECT_PLAYER = "SELECT gang, role FROM userdata WHERE uuid LIKE '%uuid%';";
    public static String INSERT_NEW_GANG = "INSERT INTO gangs(name, members, friendly_fire) VALUES('%name%', '%members%', %friendly_fire%);";
    public static String UPDATE_PLAYER = "UPDATE userdata SET gang='%gang%', role=%role% WHERE uuid LIKE '%uuid%';";
    public static String REMOVE_PLAYER_FROM_GANG = "UPDATE userdata SET gang=NULL, role=NULL WHERE uuid='%uuid%';";
    public static String DELETE_GANG = "DELETE FROM gangs WHERE name LIKE '%name%';";
    public static String SET_GANG_HIDEOUT = "UPDATE gangs SET hideout='%hideout%' WHERE name LIKE '%name%';";
    public static String UPDATE_GANG = "UPDATE gangs SET members='%members%', friendly_fire=%friendly_fire%, hideout='%hideout%' WHERE name LIKE '%name%';";
    public static String RENAME_GANG = "UPDATE gangs SET name='%newname%' WHERE name LIKE '%oldname%';";

    //loaded from config
    public static String SQL_HOST;
    public static String SQL_DB;
    public static String SQL_USER;
    public static String SQL_PASS;
    public static int SQL_PORT;
    public static String SQL_URL;

    public static String formatSqlCall(String call) {
        return call.replace("%host%", SQL_HOST).replace("%db%", SQL_DB)
                .replace("%user%", SQL_USER).replace("%pass%", SQL_PASS)
                .replace("%port%", SQL_PORT + "");
    }

    public static int getPermissionLevel(OfflinePlayer player) {
        try {
            PreparedStatement statement = SQLConnectionThread.getConnection().prepareStatement(SELECT_PLAYER.replace("%uuid%", player.getUniqueId().toString()));

            statement.execute();

            ResultSet rs = statement.getResultSet();

            if (rs.next()) {
                int role = rs.getInt("role");

                return role;
            }

            return 0;
        } catch (Exception err) {
            err.printStackTrace();
            return 0;
        }
    }

    public static void loadGang(String name) {
        if (GangManager.getGang(name) != null)
            return;

        try {
            PreparedStatement statement = SQLConnectionThread.getConnection().prepareStatement(SQLVars.GET_GANG.replace("%name%", name));

            statement.execute();

            ResultSet rs = statement.getResultSet();

            if (!(rs.next()))
                return;

            String members = rs.getString("members");
            int friendlyFire = rs.getInt("friendly_fire");
            String hideout = rs.getString("hideout");

            //parse

            List<OfflinePlayer> memberList = Util.fromString(members);
            Location hide = LocationUtil.fromString(hideout);

            Gang load = new Gang(memberList, name, friendlyFire);
            load.setHideout(hide);

            GangManager.insertGang(load);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static String getGang(OfflinePlayer player) {
        try {
            PreparedStatement statement = SQLConnectionThread.getConnection().prepareStatement(SELECT_PLAYER.replace("%uuid%", player.getUniqueId().toString()));

            statement.execute();

            ResultSet rs = statement.getResultSet();

            if (rs.next()) {
                String gang = rs.getString("gang");

                return gang;
            }

            return null;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }
}
