package com.patrickzhong.pvpstats;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	HashMap<Player, SPlayer> players = new HashMap<Player, SPlayer>();
	SQLUtil util;
	
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this, this);
		
		util = new SQLUtil(this);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent ev){
		util.load(ev.getPlayer());
	}

}
