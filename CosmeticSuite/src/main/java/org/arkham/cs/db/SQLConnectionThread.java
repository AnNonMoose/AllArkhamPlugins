package org.arkham.cs.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLConnectionThread {
    private static Connection con = null;
    public static int query_count = 0;

    public static Connection getConnection() {  
        try {  
            if (query_count >= 1000) {
                if(con != null){
                    con.close();
                }
                con = DriverManager.getConnection(Authentication.sqlurl, Authentication.sqluser, Authentication.sqlpass); 
                query_count = 0;
            }
            if (con == null || con.isClosed()) {  
                Class.forName("com.mysql.jdbc.Driver");  
                con = DriverManager.getConnection(Authentication.sqlurl, Authentication.sqluser, Authentication.sqlpass);
            }  
        } catch(Exception e) {
            e.printStackTrace();
            try{
                con = DriverManager.getConnection(Authentication.sqlurl, Authentication.sqluser, Authentication.sqlpass);
            } catch(Exception err){
                err.printStackTrace();
            }
        }
 
        query_count++;
        return con;  
    }

    public static ResultSet getResultSet(String query) {
        PreparedStatement pst = null;
        try {
            pst = getConnection().prepareStatement(query);
            pst.execute();
            ResultSet rs = pst.getResultSet();
            return rs;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        } 
    }
}
