package org.arkham.cs.db;

public class Authentication {
	
    public static String sqlhost = "<snip>";
    public static String sqldb = "arkham_cosmetics";
    public static String sqluser = "root"; // TODO: Replace w/ slave account, setup perms.
    public static String sqlpass = "<snip>";
    public static int sqlport = 3306;
    public static String sqlurl = "jdbc:mysql://" + sqlhost + ":" + sqlport + "/" + sqldb;

}
