package com.patrickzhong.pvpstats;

import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

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
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageByEntityEvent ev){
		if(!ev.isCancelled() && ev.getEntity() instanceof Player){
			Player damager = get(ev.getDamager());
			if(damager != null){
				SPlayer splayer = players.get(damager);
				splayer.damage += ev.getFinalDamage();
				util.update(splayer);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent ev){
		SPlayer sVictim = players.get(ev.getEntity());
		sVictim.deaths++;
		util.update(sVictim);
		
		if(ev.getEntity().getKiller() != null){
			SPlayer sKiller = players.get(ev.getEntity().getKiller());
			sKiller.kills++;
			util.update(sKiller);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent ev){
		players.remove(ev.getPlayer());
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		String name;
		
		if(args.length == 0)
			name = sender.getName();
		else
			name = args[0];
		
		double[] stats = util.getStats(name);
		
		if(stats == null){
			sender.sendMessage(ChatColor.DARK_RED+"Could not find any stats for "+ChatColor.RED+name);
			return true;
		}
		
		sender.sendMessage(ChatColor.GRAY+"Stats for "+ChatColor.AQUA+name);
		sender.sendMessage(ChatColor.GOLD+"Kills"+ChatColor.GRAY+": "+ChatColor.YELLOW+(int)stats[0]);
		sender.sendMessage(ChatColor.GOLD+"Deaths"+ChatColor.GRAY+": "+ChatColor.YELLOW+(int)stats[1]);
		sender.sendMessage(ChatColor.GOLD+"Damage"+ChatColor.GRAY+": "+ChatColor.YELLOW+(int)stats[2]);
		
		return true;
	}
	
	private Player get(Entity ent){
		if(ent instanceof Player)
			return (Player) ent;
		if(ent instanceof Projectile){
			ProjectileSource shooter = ((Projectile)ent).getShooter();
			if(shooter instanceof Player)
				return (Player) shooter;
		}
		
		return null;
	}

}
