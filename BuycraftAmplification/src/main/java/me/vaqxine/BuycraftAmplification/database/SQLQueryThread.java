package me.vaqxine.BuycraftAmplification.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.vaqxine.BuycraftAmplification.BuycraftAmplification;

/**
 * 
 * SQL Pool to execute multi-threaded and thread-safe insert queries.
 *
 */

public class SQLQueryThread extends Thread {
	@Override
	public void run(){
		while(true){
			try {Thread.sleep(500);} catch (InterruptedException e) {}
			for(String query : BuycraftAmplification.sql_query){
				Connection con = null;
				PreparedStatement pst = null;

				try {
					pst = SQLConnectionThread.getConnection().prepareStatement(query);
					pst.executeUpdate();
					
				} catch (Exception ex) {
					ex.printStackTrace();
					BuycraftAmplification.log.error(query, this.getClass());

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
				
				BuycraftAmplification.sql_query.remove(query);
			}
		}
	}
	
	public void doWork(){
        for(String query : BuycraftAmplification.sql_query){
            Connection con = null;
            PreparedStatement pst = null;

            try {
                pst = SQLConnectionThread.getConnection().prepareStatement(query);
                pst.executeUpdate();
                
            } catch (Exception ex) {
                ex.printStackTrace();
                BuycraftAmplification.log.error(query, this.getClass());

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
            
            BuycraftAmplification.sql_query.remove(query);
        }
	}
}
