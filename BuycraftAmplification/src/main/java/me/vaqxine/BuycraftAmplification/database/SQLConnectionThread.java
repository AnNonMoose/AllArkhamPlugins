package me.vaqxine.BuycraftAmplification.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * Multithreaded SQL "pooling" for running queries to and from the database server.
 * Reset connection every 1,000 connections or on timeout, whichever happens first.
 */

public class SQLConnectionThread {
    private static Connection con = null;  
    public static int query_count = 0;
    public static Connection getConnection(){  
        try{  
            if(query_count >= 1000){
                if(con != null){
                    con.close();
                }

                con = DriverManager.getConnection(Authentication.sql_url, Authentication.sql_user, Authentication.sql_pass);  
                query_count = 0;
            }
            if(con==null || con.isClosed()){  
                Class.forName("com.mysql.jdbc.Driver");  
                con = DriverManager.getConnection(Authentication.sql_url, Authentication.sql_user, Authentication.sql_pass);
            }  
        } catch(Exception e){
            e.printStackTrace();
            try{
                con = DriverManager.getConnection(Authentication.sql_url, Authentication.sql_user, Authentication.sql_pass);
            } catch(Exception err){
                err.printStackTrace();
            }
        }

        query_count++;
        return con;  
    } 

    public static ResultSet getResultSet(String query){
        PreparedStatement pst = null;

        try{
            pst = SQLConnectionThread.getConnection().prepareStatement(query);
            pst.execute();

            ResultSet rs = pst.getResultSet();
            return rs;
        } catch(Exception err){
            err.printStackTrace();
            return null;
        } /*finally {
			try {
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}*/
    }
}
