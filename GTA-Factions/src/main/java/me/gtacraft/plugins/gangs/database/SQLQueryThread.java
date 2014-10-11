package me.gtacraft.plugins.gangs.database;

import me.gtacraft.plugins.gangs.GTAGangs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * SQL Pool to execute multi-threaded and thread-safe insert queries.
 *
 */

public class SQLQueryThread extends Thread {
    @Override
    public void run(){
        while(true){
            try {Thread.sleep(250);} catch (InterruptedException e) {}
            for(String query : GTAGangs.sql_query){
                Connection con = null;
                PreparedStatement pst = null;

                try {
                    pst = SQLConnectionThread.getConnection().prepareStatement(query);
                    pst.executeUpdate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    GTAGangs.log.error(query, this.getClass());

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

                GTAGangs.sql_query.remove(query);
            }
        }
    }

    public void doWork(){
        for(String query : GTAGangs.sql_query){
            Connection con = null;
            PreparedStatement pst = null;

            try {
                pst = SQLConnectionThread.getConnection().prepareStatement(query);
                pst.executeUpdate();

            } catch (Exception ex) {
                ex.printStackTrace();
                GTAGangs.log.error(query, this.getClass());

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

            GTAGangs.sql_query.remove(query);
        }
    }
}
