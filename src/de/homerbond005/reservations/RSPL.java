/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.homerbond005.reservations;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerCommandEvent;

public class RSPL implements Listener {
	private final Reservations plugin;

	public RSPL(Reservations reservations) {
		plugin = reservations;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;
		String command = event.getMessage().substring(1).split(" ")[0];
		if (command.equalsIgnoreCase("res")) {
			if (Bukkit.getServer().getPluginCommand("res") == null) {
				event.setMessage(event.getMessage().replaceFirst("res", "reservations"));
				return;
			}
		}
		if (command.equalsIgnoreCase("reser")) {
			if (Bukkit.getServer().getPluginCommand("reser") == null) {
				event.setMessage(event.getMessage().replaceFirst("reser", "reservations"));
				return;
			}
		}
		if (command.split(" ")[0].equalsIgnoreCase("reserv")) {
			if (Bukkit.getServer().getPluginCommand("reserv") == null) {
				event.setMessage(event.getMessage().replaceFirst("reserv", "reservations"));
				return;
			}
		}
		if (command.split(" ")[0].equalsIgnoreCase("reserve")) {
			if (Bukkit.getServer().getPluginCommand("reserve") == null) {
				event.setMessage(event.getMessage().replaceFirst("reserve", "reservations"));
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerCommand(ServerCommandEvent event) {
		if (event.getCommand().trim().length() == 0)
			return;
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("res")) {
			if (Bukkit.getServer().getPluginCommand("res") == null) {
				event.setCommand(event.getCommand().replaceFirst("res", "reservations"));
				return;
			}
		}
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("reser")) {
			if (Bukkit.getServer().getPluginCommand("reser") == null) {
				event.setCommand(event.getCommand().replaceFirst("reser", "reservations"));
				return;
			}
		}
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("reserv")) {
			if (Bukkit.getServer().getPluginCommand("reserv") == null) {
				event.setCommand(event.getCommand().replaceFirst("reserv", "reservations"));
				return;
			}
		}
		if (event.getCommand().split(" ")[0].equalsIgnoreCase("reserve")) {
			if (Bukkit.getServer().getPluginCommand("reserve") == null) {
				event.setCommand(event.getCommand().replaceFirst("reserve", "reservations"));
				return;
			}
		}
	}

	/**
	 * Handle the player login process and kick if necessary another player
	 * 
	 * @param event The applicable PlayerLoginEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult() == Result.ALLOWED) {
			if (plugin.getPreventKickFromAnotherLocationLogin()) {
				if (event.getPlayer().isOnline()) {
					event.setResult(Result.KICK_OTHER);
					event.setKickMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLoginFromAnotherLocationMessage()));
				}
			}
			return;
		}
		if (event.getResult() != Result.KICK_FULL)
			return;
		Player player = event.getPlayer();
		if (player.isBanned())
			return;
		if (plugin.getServer().getOnlinePlayers().size() >= plugin.getServer().getMaxPlayers()) {
			if (plugin.isVIP(player)) {
				event.allow();
				return;
			} else {
				final Player kick = plugin.generateKickPlayer(player);
				if (kick != null) {
					kick.kickPlayer(ChatColor.translateAlternateColorCodes('&', plugin.getKickMsg()));
					if (plugin.getBroadcastMsg().trim().length() != 0)
						Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getBroadcastMsg().replaceAll("%lowerrank%", kick.getDisplayName()).replaceAll("%higherrank%", player.getDisplayName())));
					event.allow();
					return;
				} else {
					plugin.getLogger().log(Level.INFO, event.getPlayer().getDisplayName() + " wants to join but it was disabled, because " + event.getPlayer().getDisplayName() + "'s rank is too low or the server is full with VIPs.");
					event.disallow(Result.KICK_FULL, ChatColor.translateAlternateColorCodes('&', plugin.getSorryMsg()));
				}
			}
		}
	}
}