package com.bukkit.HomerBond005.Reservations;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.bukkit.HomerBond005.Reservations.Reservations;

public class RSPL extends PlayerListener{
	public static Reservations plugin;
	public RSPL(Reservations reservations) {
		plugin = reservations;
	}
	public void onPlayerLogin(PlayerLoginEvent event){
		if(plugin.getServer().getOnlinePlayers().length == plugin.getServer().getMaxPlayers()){
			if(plugin.isVIP(event.getPlayer())){
				Player[] players = plugin.getServer().getOnlinePlayers();
				boolean kicked = false;
				while(kicked == false){
					int random = (int) (Math.random() * players.length);
					if(!plugin.isVIP(players[random])){
				        players[random].kickPlayer(Reservations.bukkitconfig.getString("KickMsg", "A VIP joined and you were randomly selected for kicking."));
				        kicked = true;
				        event.allow();
				        return;
					}
				}
				System.out.println("[Reservations]: " + event.getPlayer().getDisplayName() + " wants to join but it was disabled, because there isn't a non-VIP to be kicked.");
				event.disallow(Result.KICK_FULL, Reservations.bukkitconfig.getString("SorryVIPMsg"));
			}else{
				System.out.println("[Reservations]: " + event.getPlayer().getDisplayName() + " wants to join but it was disabled, because " + event.getPlayer().getDisplayName() + " isn't a VIP.");
				event.disallow(Result.KICK_FULL, Reservations.bukkitconfig.getString("ServerFullMsg"));
			}
		}
	}
}