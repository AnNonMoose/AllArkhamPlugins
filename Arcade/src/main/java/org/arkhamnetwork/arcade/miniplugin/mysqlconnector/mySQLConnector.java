/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.miniplugin.mysqlconnector;

import java.sql.Statement;
import lombok.Getter;
import org.arkhamnetwork.arcade.commons.plugin.ArcadeMiniPlugin;
import org.arkhamnetwork.arcade.commons.utils.StringUtils;
import org.arkhamnetwork.arcade.core.Arcade;
import org.arkhamnetwork.arcade.core.configuration.ArcadeConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Server;

/**
 *
 * @author devan_000
 */
public class mySQLConnector extends ArcadeMiniPlugin {

    @Getter
    private mySQLConnector plugin;

    public mySQLConnector(String name, String version, Server server) {
        super(name, version, server, null);
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Needed - ArcadeMiniPlugin
        super.onEnable();

        log("Connection information:");
        log(ChatColor.WHITE + "Hostname: "
                + ArcadeConfiguration.getMysqlCredentials().getHOSTNAME());
        log(ChatColor.WHITE + "Port: "
                + ArcadeConfiguration.getMysqlCredentials().getPORT());
        log(ChatColor.WHITE + "User: "
                + ArcadeConfiguration.getMysqlCredentials().getUSER());
        log(ChatColor.WHITE
                + "Password: "
                + StringUtils.passwordToHashes(ArcadeConfiguration
                        .getMysqlCredentials().getPASSWORD()));
        log(ChatColor.WHITE + "Database Name: "
                + ArcadeConfiguration.getMysqlCredentials().getDATABASENAME());

        log("Attempting to connect...");
        new mySQLDatabaseConnection(ArcadeConfiguration.getMysqlCredentials());

        try {
            mySQLDatabaseConnection.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            Arcade.getInstance().shutdown(
                    ChatColor.RED + "Unable to connect to mySQL");
            return;
        }

        log("mySQL successfuly connected.");
        log("Attempting to execute table create statement...");

        Statement statement = null;
        try {
            try {
                statement = mySQLDatabaseConnection.getConnection()
                        .createStatement();
                statement.execute(mySQLStatementStore.tableCreateStatement);
            } finally {
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        log("Table created successfuly.");

        // Needed - ArcadeMiniPlugin
        super.postEnable();
    }

    @Override
    public void onDisable() {
        // Needed - ArcadeMiniPlugin
        super.onDisable();

        // Needed - ArcadeMiniPlugin
        super.postDisable();
    }

    @Override
    public void postEnable() {
    }

    @Override
    public void postDisable() {
    }

}
