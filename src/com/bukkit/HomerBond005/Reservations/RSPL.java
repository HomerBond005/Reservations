package com.bukkit.HomerBond005.Reservations;

import java.util.Random;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.bukkit.HomerBond005.Reservations.Reservations;

public class RSPL implements Listener{
	public static Reservations plugin;
	Random randomgen;
	public RSPL(Reservations reservations) {
		plugin = reservations;
		randomgen = new Random();
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event){
		if(event.getPlayer().isBanned())
			return;
		if(plugin.getServer().getOnlinePlayers().length == plugin.getServer().getMaxPlayers()){
			if(plugin.isVIP(event.getPlayer())){
				System.out.println("VIP");
				Player kick = choseKick(plugin.getServer().getOnlinePlayers());
				if(kick != null){
			        kick.kickPlayer(Reservations.bukkitconfig.getString("KickMsg", "A VIP joined and you were randomly selected for kicking."));
			        event.allow();
			        return;
				}
				System.out.println("[Reservations]: " + event.getPlayer().getDisplayName() + " wants to join but it was disabled, because there isn't a non-VIP to be kicked.");
				event.disallow(Result.KICK_FULL, Reservations.bukkitconfig.getString("SorryVIPMsg"));
			}else{
				System.out.println("[Reservations]: " + event.getPlayer().getDisplayName() + " wants to join but it was disabled, because " + event.getPlayer().getDisplayName() + " isn't a VIP.");
				event.disallow(Result.KICK_FULL, Reservations.bukkitconfig.getString("ServerFullMsg"));
			}
		}
	}
	private Player choseKick(Player[] array){
		int index = 0;
		boolean[] taken = new boolean[array.length];
		for(int i = 0; i < taken.length; i++)
			taken[i] = false;
		boolean abort = false;
		while(!abort){
			if(index == array.length){
				abort = true;
			}else{
				int random = randomgen.nextInt(array.length);
				if(!taken[random]){
					if(!plugin.isVIP(array[random]))
						return array[random];
					index++;
				}
			}
		}
		return null;
	}
}