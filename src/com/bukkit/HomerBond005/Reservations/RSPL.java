package com.bukkit.HomerBond005.Reservations;

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
		if(plugin.canJoin(event.getPlayer())){
			return;
		}
		else{
			System.out.println(event.getPlayer().getDisplayName() + " wants to join but it was disabled, because some slots for VIPs have to be free.");
			event.disallow(Result.KICK_FULL, plugin.getMessage());
		}
	}
}