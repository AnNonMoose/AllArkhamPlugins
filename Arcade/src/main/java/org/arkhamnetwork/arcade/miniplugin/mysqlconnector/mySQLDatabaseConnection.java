/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.arcade.miniplugin.mysqlconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import org.arkhamnetwork.arcade.core.configuration.MySQLCredentials;

/**
 *
 * @author devan_000
 */
public class mySQLDatabaseConnection {

    private static Connection connection = null;
    private static String connectionURL;
    private static String user;
    private static String password;

    public mySQLDatabaseConnection(MySQLCredentials credentials) {
        this.connectionURL = "jdbc:mysql://" + credentials.getHOSTNAME() + ":"
                + credentials.getPORT() + "/" + credentials.getDATABASENAME();
        this.user = credentials.getUSER();
        this.password = credentials.getPASSWORD();
    }

    public static Connection getConnection() throws Exception {
        if (connection == null || connection.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionURL, user,
                    password);
        }

        return connection;
    }

    public static void closeConnection() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

}
