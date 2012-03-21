package de.HomerBond005.Reservations;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerCommandEvent;

import de.HomerBond005.Reservations.Reservations;

public class RSPL implements Listener{
	public Reservations plugin;
	Random randomgen;
	public RSPL(Reservations reservations) {
		plugin = reservations;
		randomgen = new Random();
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		String command = event.getMessage().substring(1).split(" ")[0];
		if(command.equalsIgnoreCase("res")){
			if(Bukkit.getServer().getPluginCommand("res") == null){
				event.setMessage(event.getMessage().replaceFirst("res", "reservations"));
				return;
			}
		}if(command.equalsIgnoreCase("reser")){
			if(Bukkit.getServer().getPluginCommand("reser") == null){
				event.setMessage(event.getMessage().replaceFirst("reser", "reservations"));
				return;
			}
		}if(command.split(" ")[0].equalsIgnoreCase("reserv")){
			if(Bukkit.getServer().getPluginCommand("reserv") == null){
				event.setMessage(event.getMessage().replaceFirst("reserv", "reservations"));
				return;
			}
		}if(command.split(" ")[0].equalsIgnoreCase("reserve")){
			if(Bukkit.getServer().getPluginCommand("reserve") == null){
				event.setMessage(event.getMessage().replaceFirst("reserve", "reservations"));
				return;
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerCommand(ServerCommandEvent event){
		if(event.getCommand().split(" ")[0].equalsIgnoreCase("res")){
			if(Bukkit.getServer().getPluginCommand("res") == null){
				event.setCommand(event.getCommand().replaceFirst("res", "reservations"));
				return;
			}
		}if(event.getCommand().split(" ")[0].equalsIgnoreCase("reser")){
			if(Bukkit.getServer().getPluginCommand("reser") == null){
				event.setCommand(event.getCommand().replaceFirst("reser", "reservations"));
				return;
			}
		}if(event.getCommand().split(" ")[0].equalsIgnoreCase("reserv")){
			if(Bukkit.getServer().getPluginCommand("reserv") == null){
				event.setCommand(event.getCommand().replaceFirst("reserv", "reservations"));
				return;
			}
		}if(event.getCommand().split(" ")[0].equalsIgnoreCase("reserve")){
			if(Bukkit.getServer().getPluginCommand("reserve") == null){
				event.setCommand(event.getCommand().replaceFirst("reserve", "reservations"));
				return;
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(final PlayerLoginEvent event){
		Player player = event.getPlayer();
		if(player.isBanned())
			return;
		if(plugin.getServer().getOnlinePlayers().length >= plugin.getServer().getMaxPlayers()){
			if(plugin.isVIP(player)){
				event.allow();
				return;
			}else{
				final Player kick = plugin.generateKickPlayer(player);
				if(kick != null){
					try{
						plugin.bukkitconfig.load(plugin.config);
					}catch(Exception e){}
					kick.kickPlayer(plugin.bukkitconfig.getString("KickMsg", "A VIP joined and you were randomly selected for kicking."));
					if(plugin.bukkitconfig.getString("Broadcast").trim().length() != 0)
						Bukkit.getServer().broadcastMessage(plugin.bukkitconfig.getString("Broadcast").replaceAll("%lowerrank%", kick.getDisplayName()).replaceAll("%higherrank%", player.getDisplayName()));
					event.allow();
					return;
				}else{
					System.out.println("[Reservations]: " + event.getPlayer().getDisplayName() + " wants to join but it was disabled, because " + event.getPlayer().getDisplayName() + "'s rank is too low or the server is full with VIPs.");
					event.disallow(Result.KICK_FULL, plugin.bukkitconfig.getString("SorryMsg"));
				}
			}
		}
	}
}