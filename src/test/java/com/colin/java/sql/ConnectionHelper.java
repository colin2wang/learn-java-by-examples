package com.colin.java.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
	
	public static Connection openSQLServer(String serverIPandPort, String databaseName, String userName, String password) {
		
		//String connect to SQL server
		String url = "jdbc:sqlserver://" + serverIPandPort + ";DatabaseName=" + databaseName;
		Connection connection = null;

		try {
			//Loading the driver..
			Class.forName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
		}
		catch( java.lang.ClassNotFoundException e ) {
			e.printStackTrace();
		    return null;
		} 

		try {
			//Building a Connection
			connection = DriverManager.getConnection(url, userName, password); 
		}
		catch(SQLException e) {
			e.printStackTrace();
		} 
		return connection;
    }
	
	public static void main(String[] args) {
		// For test
		ConnectionHelper.openSQLServer("localhost:1433", "tempdb", "sa", "19840516");
	}

}