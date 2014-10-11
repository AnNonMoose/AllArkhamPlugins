/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync.managers;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.arkhamnetwork.playersync.PlayerSync;
import org.arkhamnetwork.playersync.sql.Database;
import org.arkhamnetwork.playersync.sql.MySQL;

/**
 *
 * @author devan_000
 */
public class SQLManager {

      private static final PlayerSync plugin = PlayerSync.get();

      public static MySQL MySQL = null;
      public static Connection C = null;
      public static Database DB = null;

      public static boolean onEnable() {
            MySQL = new MySQL(plugin.getConfig());
            C = (Connection) MySQL.open();

            if (MySQL.shutdown) {
                  return false;
            }

            DB = new Database(C, MySQL);

            try {
                  setupSQLTables();
            } catch (IOException | SQLException e) {
                  plugin.log("Error with SQL connection");
                  return false;
            }

            return true;
      }

      private static void setupSQLTables() throws IOException, SQLException {
            URL resource = Resources.getResource(PlayerSync.class, "/tables.sql");
            String[] databaseStructure = Resources.toString(resource, Charsets.UTF_8).split(";");

            if (databaseStructure.length == 0) {
                  return;
            }
            Statement statement = null;
            try {
                  C.setAutoCommit(false);
                  statement = C.createStatement();

                  for (String query : databaseStructure) {
                        query = query.trim();

                        if (query.isEmpty()) {
                              continue;
                        }

                        statement.execute(query);
                  }
                  C.commit();
            } finally {
                  C.setAutoCommit(true);
                  if (statement != null && !statement.isClosed()) {
                        statement.close();
                  }
            }
      }

}
