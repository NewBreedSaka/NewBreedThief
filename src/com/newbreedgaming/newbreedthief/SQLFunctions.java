package com.newbreedgaming.newbreedthief;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

public class SQLFunctions {
	Main plugin;
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://http://76.74.200.77/playerdata";
	//  Database credentials
	static final String USER = "root";
	static final String PASS = "w6m5Ox90Sv";
	Statement stmt;
	Connection conn;
	public SQLFunctions(Main plugin){
		this.plugin = plugin;
		conn = null;
		stmt = null;
	}
	public void createPlayer(Player p){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			DatabaseMetaData dbm = conn.getMetaData();
			// check if "employee" table is there
		
			stmt = conn.createStatement();
			String sql = "CREATE TABLE " +  p.getName() + "( Kills int, Deaths int, TPoints int , UTPoints int, Wins int, Losses int, UUID varchar(255))"; 
			String asql = "INSERT INTO " + p.getName() + "(UUID) VALUES ('"+ p.getUniqueId() +"');";
			stmt.executeUpdate(asql);
			stmt.executeUpdate(sql);
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					conn.close();
			}catch(SQLException se){
			}// do nothing
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
	}
	
	public void IncrementDeaths(Player p){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		
			stmt = conn.createStatement();
			
			
			String sql = "UPDATE " + p.getName() + "SET Kills=Kills+1 WHERE UUID='"+ p.getUniqueId() + "'";
			
			
			
			stmt.executeUpdate(sql);
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					conn.close();
			}catch(SQLException se){
			}// do nothing
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void getCurrentDeaths(String p, int deaths){
		
		
	}


}
