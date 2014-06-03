package com.newbreedgaming.newbreedthief;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.entity.Player;
public class MySQL {


	private Connection connection;

	public MySQL(String ip, String userName, String password, String db) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + db + "?user=" + userName + "&password=" + password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
