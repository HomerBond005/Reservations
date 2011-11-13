package com.bukkit.HomerBond005.Reservations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("deprecation")
public class Reservations extends JavaPlugin{
	private final RSPL playerlistener = new RSPL(this);
	static String mainDir = "plugins/Reservations";
	static File config = new File (mainDir + File.separator + "VIP.yml");
	static Configuration bukkitconfig = new Configuration(config);
	Yaml yaml = new Yaml();
	InputStream input = null;
	BufferedReader msgreader;
	BufferedWriter msgwriter;
	File msgfile = new File(mainDir + File.separator + "message.txt");
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		Player player = (Player) sender;
		if(player.isOp()){
			if(command.getName().toLowerCase().equals("reservations")||command.getName().toLowerCase().equals("res")){
				try{
					@SuppressWarnings("unused")
					String test = args[0];
				}catch(ArrayIndexOutOfBoundsException e){
					help(player);
					return true;
				}
				if(args[0].equalsIgnoreCase("set")){
					try{
						@SuppressWarnings("unused")
						String test = args[1];
					}catch(ArrayIndexOutOfBoundsException e){
						player.sendMessage(ChatColor.WHITE + "Reservations Help Message");
						player.sendMessage(ChatColor.RED + "/res set kickmsg <message> " + ChatColor.BLUE + "Changes the kick message.");
						player.sendMessage(ChatColor.RED + "/res set serverfullmsg <message> " + ChatColor.BLUE + "Changes the message if the server is full.");
						return true;
					}
					if(args[1].equalsIgnoreCase("kickmsg")){
						try{
							@SuppressWarnings("unused")
							String test = args[2];
						}catch(ArrayIndexOutOfBoundsException e){
							player.sendMessage(ChatColor.RED + "Please enter a message:");
							player.sendMessage(ChatColor.RED + "/res set kickmsg <message>");
							return true;
						}
						setKickMsg(player, args[2]);
						return true;
					}
					if(args[1].equalsIgnoreCase("serverfullmsg")){
						try{
							@SuppressWarnings("unused")
							String test = args[2];
						}catch(ArrayIndexOutOfBoundsException e){
							player.sendMessage(ChatColor.RED + "Please enter a message:");
							player.sendMessage(ChatColor.RED + "/res set serverfullmsg <message>");
							return true;
						}
						setServerFull(player, args[2]);
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("list")){
					list(player);
				}
				if(args[0].equalsIgnoreCase("add")){
					bukkitconfig.load();
					bukkitconfig.setProperty("VIPs." + args[1], "");
					bukkitconfig.save();
					player.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to the VIP list.");
					return true;
				}
				if(args[0].equalsIgnoreCase("delete")){
					delete(player, args[1]);
				}
			}
		}else{
			player.sendMessage(ChatColor.RED + "You have to be OP to use this command!");
		}
		return true;
	}
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerlistener,
				Event.Priority.High, this);
		new File (mainDir).mkdir();
		if(!(config.exists())){
			try{
				config.createNewFile();
				bukkitconfig.setProperty("VIPs.HomerBond005", "");
				bukkitconfig.setProperty("KickMsg", "A VIP joined and you were randomly selected for kicking.");
				bukkitconfig.setProperty("ServerFullMsg", "The server is full!");
				bukkitconfig.save();
				System.out.println("[Reservations]: VIP.yml created.");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		System.out.println("[Reservations] is enabled!");
	}
	public void onDisable(){
		System.out.println("[Reservations] is disabled!");
	}
	public boolean isVIP(Player player){
		if(player.hasPermission("Reservations.VIP")){
			return true;
		}else{
			bukkitconfig.load();
			if(bukkitconfig.getString("VIPs." + player.getDisplayName(), "$%") != "$%"){
				return true;
			}else{
				return false;
			}
		}
	}
	public boolean isVIP(String name){
		bukkitconfig.load();
		if(bukkitconfig.getString("VIPs." + name, "$%") != "$%"){
			return true;
		}else{
			return false;
		}
	}
	//Functions for Commands
	private void help(Player player){
		player.sendMessage(ChatColor.WHITE + "Reservations Help");
		player.sendMessage(ChatColor.RED + "/res list   " + ChatColor.BLUE + "Lists all VIPs.");
		player.sendMessage(ChatColor.RED + "/res add <player>   " + ChatColor.BLUE + "Adds a player to VIPs.");
		player.sendMessage(ChatColor.RED + "/res delete <player>   " + ChatColor.BLUE + "Deletes a player from VIPs");
		player.sendMessage(ChatColor.RED + "/res set kickmsg <message>   " + ChatColor.BLUE + "Changes the kick-message");
		player.sendMessage(ChatColor.RED + "/res set serverfullmsg <message>   " + ChatColor.BLUE + "Changes the message if the server is full");
	}
	private void setServerFull(Player player, String message){
		bukkitconfig.load();
		bukkitconfig.setProperty("ServerFullMsg", message);
		bukkitconfig.save();
		player.sendMessage(ChatColor.GREEN + "Server-Full-Message set to:");
		player.sendMessage(message);
	}
	private void setKickMsg(Player player, String message){
		bukkitconfig.load();
		bukkitconfig.setProperty("KickMsg", message);
		bukkitconfig.save();
		player.sendMessage(ChatColor.GREEN + "Kick-Message set to:");
		player.sendMessage(message);
	}
	private void list (Player player){
		player.sendMessage(ChatColor.GREEN + "Following players are VIPs: (Defined in VIP.yml)");
		bukkitconfig.load();
		Object[] VIPlist;
		try{
			VIPlist = bukkitconfig.getKeys("VIPs").toArray();
		}catch(NullPointerException e){
			player.sendMessage(ChatColor.GRAY + "No VIPs in VIP.yml");
			return;
		}
		if(VIPlist.length == 0){
			player.sendMessage(ChatColor.GRAY + "No VIPs in VIP.yml");
			return;
		}
		String VIPString = "";
		for(int i = 0; i < VIPlist.length; i++){
			if(VIPlist.length == i + 1){
				VIPString += VIPlist[i];
			}else{
				VIPString += VIPlist[i] + ", ";
			}
		}
		player.sendMessage(ChatColor.GOLD + "" + VIPString);
	}
	private void delete(Player player, String name){
		boolean isVIP = isVIP(name);
		if(isVIP == false){
			player.sendMessage(ChatColor.RED + "The player " + ChatColor.GOLD + name + ChatColor.RED + " isn't a VIP!");
			player.sendMessage(ChatColor.RED + "If the player has the permission,you have to delete it manually");
			return;
		}else{
			bukkitconfig.load();
			bukkitconfig.removeProperty("VIPs." + name);
			bukkitconfig.save();
			player.sendMessage(ChatColor.GREEN + "Successfully deleted " + ChatColor.GOLD + name + ChatColor.GREEN + " from the VIP list.");
		}
	}
}
