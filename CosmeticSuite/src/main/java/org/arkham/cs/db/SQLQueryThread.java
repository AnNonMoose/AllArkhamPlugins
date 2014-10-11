package org.arkham.cs.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

public class SQLQueryThread extends Thread {
	
	public static volatile CopyOnWriteArrayList<String> sql_query = new CopyOnWriteArrayList<>();
	
	public SQLQueryThread(){
		start();
		setName("Cosmetics-SQL");
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
			for (String query : sql_query) {
				Connection con = null;
				PreparedStatement pst = null;
				try {
					pst = SQLConnectionThread.getConnection().prepareStatement(query);
					pst.executeUpdate();
				} catch (Exception ex) {
					ex.printStackTrace();
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
				sql_query.remove(query);
			}
		}
	}
	
	public static void addQuery(String query){
		sql_query.add(query);
	}
}
