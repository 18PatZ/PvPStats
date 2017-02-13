package com.patrickzhong.pvpstats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.entity.Player;

public class SQLUtil {


	Connection conn;
	Main main;
	
	
	public SQLUtil(Main main){
		
		this.main = main;
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "password");
			conn.createStatement().execute("USE pvpstats");
		} catch (SQLException | ClassNotFoundException e) {
			main.getLogger().info(ExceptionUtils.getStackTrace(e));
		}
	}
	
	public void update(SPlayer player){
		try {
			
			String uuid = player.player.getUniqueId().toString();
			conn.createStatement().executeUpdate("UPDATE pvpstats SET UUID = '"+uuid+"', Kills = "+player.kills+", Deaths = "+player.deaths+", Damage = "+player.damage+" WHERE UUID = '"+uuid+"'");
			
		} catch (SQLException e) {
			main.getLogger().info(ExceptionUtils.getStackTrace(e));
		}
	}
	
	public void load(Player player){
		
		SPlayer splayer = new SPlayer(player);
		main.players.put(player, splayer);
		
		try {
			
			String uuid =  player.getUniqueId().toString();
			ResultSet set = conn.createStatement().executeQuery("SELECT UUID, Kills, Deaths, Damage FROM pvpstats WHERE UUID = '"+uuid+"'");
			
			if(set.next()){
				splayer.kills = set.getInt("Kills");
				splayer.deaths = set.getInt("Deaths");
				splayer.damage = set.getDouble("Damage");
			}
			else
				conn.createStatement().executeUpdate("INSERT INTO pvpstats(UUID, Kills, Deaths, Damage) VALUES ('"+uuid+"',0,0,0)");
			
		} catch (SQLException e) {
			main.getLogger().info(ExceptionUtils.getStackTrace(e));
		}
	}
	
}
