/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author devan_000
 */
public class Database {

      private Connection c = null;
      private MySQL mysql = null;

      public Database(Connection c, MySQL mysql) {
            this.c = c;
            this.mysql = mysql;
      }

      public boolean databaseContainsUUID(String UUID) throws SQLException {
            if (!mysql.checkConnection()) {
                  c = mysql.open();
            }

            Statement statement = null;
            ResultSet rs = null;

            try {
                  statement = c.createStatement();

                  // Select * is unnecessarily intensive.
                  statement.executeQuery("SELECT player_uuid FROM player_storage WHERE player_uuid='" + UUID + "'");
                  rs = statement.getResultSet();

                  while (rs.next()) {
                        return true;
                  }
            } finally {
                  if (statement != null && !statement.isClosed()) {
                        statement.close();
                  }

                  if (rs != null && !rs.isClosed()) {
                        rs.close();
                  }
            }

            return false;
      }

      public void addUserToDatabase(String UUID) throws SQLException {
            if (!mysql.checkConnection()) {
                  c = mysql.open();
            }

            Statement statement = null;

            try {
                  statement = c.createStatement();
                  statement.execute("INSERT INTO player_storage (`player_uuid`, `player_inventory`, `player_enderchest`, `player_potioneffects`, `player_location`, `is_online`, `online_server_address`) VALUES ('" + UUID + "', '', '', '', '', 'false', '');");
            } finally {
                  if (statement != null && !statement.isClosed()) {
                        statement.close();
                  }
            }
      }

      public byte[] getBytes(String coloum, String UUID) throws SQLException {
            if (!mysql.checkConnection()) {
                  c = mysql.open();
            }

            Statement statement = null;
            ResultSet rs = null;

            try {
                  statement = c.createStatement();
                  rs = statement.executeQuery("SELECT " + coloum + " FROM player_storage WHERE player_uuid='" + UUID + "'");

                  while (rs.next()) {
                        return rs.getBytes(coloum);
                  }
            } finally {
                  if (statement != null && !statement.isClosed()) {
                        statement.close();
                  }

                  if (rs != null && !rs.isClosed()) {
                        rs.close();
                  }
            }
            return null;
      }

      public double getDouble(String coloum, String UUID) throws SQLException {
            if (!mysql.checkConnection()) {
                  c = mysql.open();
            }

            Statement statement = null;
            ResultSet rs = null;

            try {
                  statement = c.createStatement();
                  rs = statement.executeQuery("SELECT " + coloum + " FROM player_storage WHERE player_uuid='" + UUID + "'");

                  while (rs.next()) {
                        return rs.getDouble(coloum);
                  }
            } finally {
                  if (statement != null && !statement.isClosed()) {
                        statement.close();
                  }

                  if (rs != null && !rs.isClosed()) {
                        rs.close();
                  }
            }
            return 0.0;
      }

      public String getString(String coloum, String UUID) throws SQLException {
            if (!mysql.checkConnection()) {
                  c = mysql.open();
            }

            Statement statement = null;
            ResultSet rs = null;

            try {
                  statement = c.createStatement();
                  rs = statement.executeQuery("SELECT " + coloum + " FROM player_storage WHERE player_uuid='" + UUID + "'");

                  while (rs.next()) {
                        return rs.getString(coloum);
                  }
            } finally {
                  if (statement != null && !statement.isClosed()) {
                        statement.close();
                  }

                  if (rs != null && !rs.isClosed()) {
                        rs.close();
                  }
            }
            return null;
      }

      public void saveBytes(String coloum, String UUID, byte[] bytes) throws SQLException {
            if (!mysql.checkConnection()) {
                  c = mysql.open();
            }

            PreparedStatement statement = null;

            try {
                  statement = c.prepareStatement("UPDATE player_storage SET " + coloum + "=? WHERE player_uuid='" + UUID + "'");
                  statement.setBytes(1, bytes);
                  statement.execute();

            } finally {
                  if (statement != null && !statement.isClosed()) {
                        statement.close();
                  }
            }
      }

      public void saveDouble(String coloum, String UUID, double amount) throws SQLException {
            if (!mysql.checkConnection()) {
                  c = mysql.open();
            }

            PreparedStatement statement = null;

            try {
                  statement = c.prepareStatement("UPDATE player_storage SET " + coloum + "=? WHERE player_uuid='" + UUID + "'");
                  statement.setDouble(1, amount);
                  statement.execute();

            } finally {
                  if (statement != null && !statement.isClosed()) {
                        statement.close();
                  }
            }
      }

      public void saveString(String coloum, String UUID, String s) throws SQLException {
            if (!mysql.checkConnection()) {
                  c = mysql.open();
            }

            PreparedStatement statement = null;

            try {
                  statement = c.prepareStatement("UPDATE player_storage SET " + coloum + "=? WHERE player_uuid='" + UUID + "'");
                  statement.setString(1, s);
                  statement.execute();

            } finally {
                  if (statement != null && !statement.isClosed()) {
                        statement.close();
                  }
            }
      }
}
