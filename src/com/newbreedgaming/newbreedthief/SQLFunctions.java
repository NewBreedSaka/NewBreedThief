package com.newbreedgaming.newbreedthief;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.entity.Player;

public class SQLFunctions {
	Main plugin;
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://76.74.200.77/playerdata";
	//  Database credentials
	static final String USER = "newbreed";
	static final String PASS = "newbreedservertesting";
	Statement stmt;
	Connection conn;
	int Deaths;
	int Kills;
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
			UUID uuid = p.getUniqueId();
			String sql = "INSERT INTO Players(TalentsSurefooted, TalentsFocused, TalentsSteadyHand, TalentsConcealed, Deaths, Kills, Losses, Talents, UnusedTalents, Username,  UUID, Wins )  VALUES ( 0, 0, 0, 0, 0, 0, 0, 0, 0,  '" + p.getName() + "' , '" + p.getUniqueId() + "' ,0 )"; 


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

	
	
	public void addDeath(Player p){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			DatabaseMetaData dbm = conn.getMetaData();
			// check if "employee" table is there




			stmt = conn.createStatement();
			UUID uuid = p.getUniqueId();
			String sql = "UPDATE Players SET Deaths=Deaths+1   WHERE UUID='" + p.getUniqueId() + "';"; 
			
			
			
			
			ResultSet set = stmt.executeQuery("SELECT Deaths FROM Players WHERE UUID='" + p.getUniqueId() + "';");
			if (set.next()){
			 Deaths = set.getInt("Deaths");
			}


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
		}//en
	}


	public void addKill(Player p){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			DatabaseMetaData dbm = conn.getMetaData();
			// check if "employee" table is there




			stmt = conn.createStatement();
			
			String sql = "UPDATE Players SET Kills=Kills+1   WHERE UUID='" + p.getUniqueId() + "';"; 
			ResultSet set = stmt.executeQuery("SELECT Kills FROM Players WHERE UUID='" + p.getUniqueId() + "';");
			if (set.next()){
			 Kills = set.getInt("Kills");
			}


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
		}//en
	}


	public void addWin(Player p){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			DatabaseMetaData dbm = conn.getMetaData();
			// check if "employee" table is there




			stmt = conn.createStatement();
			
			String sql = "UPDATE Players SET Wins=Wins+1   WHERE UUID='" + p.getUniqueId() + "';"; 


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
		}//en
	}

	public void addLost(Player p){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			DatabaseMetaData dbm = conn.getMetaData();
			// check if "employee" table is there




			stmt = conn.createStatement();
			
			String sql = "UPDATE Players SET Losses=Losses+1   WHERE UUID='" + p.getUniqueId() + "';"; 


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
		}//en
	}
	
	
	
	public void updatePlayer(Player p){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			DatabaseMetaData dbm = conn.getMetaData();
			// check if "employee" table is there




			stmt = conn.createStatement();
			UUID uuid = p.getUniqueId();
			String sql = "UPDATE Players SET Username='" + p.getName() +"' WHERE UUID='" + p.getUniqueId()+"';"; 


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
		}//end tr
		
		
	}



	public int getCurrentDeaths(){
		return Deaths+1;
	}
	public int getCurrentKills(){
		return Kills+1;
	}





}
