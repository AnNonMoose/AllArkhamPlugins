package me.gtacraft.economy.database;

import me.gtacraft.economy.GTAEconomy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Connor on 6/29/14. Designed for the GTA-Factions project.
 */

public class SQLVars {

    public static String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS `%db%`;";

    public static String CREATE_ECO_TABLE = "CREATE TABLE IF NOT EXISTS `%db%`.`gta_economy` (" +
            "`id` INT(128) UNSIGNED NOT NULL AUTO_INCREMENT, " +
            "`uuid` VARCHAR(64) NOT NULL, " +
            "`balance` DOUBLE NOT NULL, " +
            "PRIMARY KEY (`id`), UNIQUE KEY `uuid` (`uuid`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;";
    public static String SELECT_PLAYER = "SELECT balance FROM `gta_economy` WHERE uuid = '%uuid%';";
    public static String INSERT_PLAYER = "INSERT INTO gta_economy(uuid, balance) VALUES('%uuid%',0.0);";
    public static String UPDATE_PLAYER = "UPDATE `gta_economy` SET balance=%balance% WHERE uuid = '%uuid%';";

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
}
