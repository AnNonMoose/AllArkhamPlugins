/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.arkhamnetwork.playersync.PlayerSync;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author devan_000
 */
public class MySQL {

      String user = "";
      String database = "";
      String password = "";
      String port = "";
      String hostname = "";
      Connection c = null;
      public boolean shutdown = false;

      static PlayerSync plugin = PlayerSync.get();

      public MySQL(FileConfiguration config) {
            this.hostname = config.getString("database.host");
            this.port = config.getString("database.port");
            this.database = config.getString("database.database");
            this.user = config.getString("database.username");
            this.password = config.getString("database.password");
      }

      public Connection open() {
            try {
                  Class.forName("com.mysql.jdbc.Driver");
                  this.c = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
                  return c;
            } catch (SQLException e) {
                  plugin.log("Could not connect to MySQL server! Plugin shutting down. Error: " + e.getMessage());
                  shutdown = true;
            } catch (ClassNotFoundException e) {
                  plugin.log("JDBC Driver not found!");
                  shutdown = true;
            }
            return this.c;
      }

      public boolean checkConnection() {
            return this.c != null;
      }

      public Connection getConn() {
            return this.c;
      }

      public void closeConnection(Connection c) {
            try {
                  if (c != null && !c.isClosed()) {
                        c.close();
                  }
            } catch (SQLException ex) {
            }
      }
}
