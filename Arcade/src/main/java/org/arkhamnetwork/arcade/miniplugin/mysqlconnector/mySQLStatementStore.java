/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.miniplugin.mysqlconnector;

/**
 *
 * @author devan_000
 */
public class mySQLStatementStore {

    public static String tableCreateStatement = "CREATE TABLE IF NOT EXISTS `player_data` (`uuid` varchar(255) NOT NULL, `credits` bigint(50) NOT NULL, `score` bigint(50) NOT NULL, `ranks` longtext NOT NULL, `last_seen` varchar(155) NOT NULL, `games_played` int(50) NOT NULL,  PRIMARY KEY (`uuid`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    public static String getUserStatement = "SELECT * FROM `player_data` WHERE `uuid`=?";

    public static String createUserDataStatement = "INSERT INTO `player_data` (`uuid`, `credits`, `score`, `ranks`, `last_seen`, `games_played`) VALUES (?, ?, ?, ?, ?, ?)";

    public static String updateUserStatement = "UPDATE `player_data` SET `uuid`=?, `credits`=?, `score`=?, `ranks`=?, `last_seen`=?, `games_played`=? WHERE `uuid`=?";
}
