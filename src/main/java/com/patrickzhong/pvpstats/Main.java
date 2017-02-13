package com.patrickzhong.pvpstats;

import java.util.HashMap;

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
