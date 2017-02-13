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
			
			String name = player.player.getName();
			conn.createStatement().executeUpdate("UPDATE pvpstats SET Username = '"+name+"', Kills = "+player.kills+", Deaths = "+player.deaths+", Damage = "+player.damage+" WHERE Username = '"+name+"'");
			
		} catch (SQLException e) {
			main.getLogger().info(ExceptionUtils.getStackTrace(e));
		}
	}
	
	public void load(Player player){
		
		SPlayer splayer = new SPlayer(player);
		main.players.put(player, splayer);
		
		try {
			
			double[] stats = getStats(player.getName());
		
			if(stats != null){
				splayer.kills = (int)stats[0];
				splayer.deaths = (int)stats[1];
				splayer.damage = stats[2];
			}
			else
				conn.createStatement().executeUpdate("INSERT INTO pvpstats(Username, Kills, Deaths, Damage) VALUES ('"+player.getName()+"',0,0,0)");
			
		} catch (SQLException e) {
			main.getLogger().info(ExceptionUtils.getStackTrace(e));
		}
	}
	
	public double[] getUnsafeStats(String name) throws SQLException{
			
		ResultSet set = conn.createStatement().executeQuery("SELECT Username, Kills, Deaths, Damage FROM pvpstats WHERE Username = '"+name+"'");
		
		if(set.next()){
			return new double[]{
					set.getInt("Kills"),
					set.getInt("Deaths"),
					set.getDouble("Damage")
			};
		}
		
		return null;
	}
	
	public double[] getStats(String name){
		try {
			return getUnsafeStats(name);
		}
		catch (SQLException e){
			main.getLogger().info(ExceptionUtils.getStackTrace(e));
		}
		
		return null;
	}
	
}
