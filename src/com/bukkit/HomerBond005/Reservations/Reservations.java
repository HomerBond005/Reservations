package com.bukkit.HomerBond005.Reservations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

public class Reservations extends JavaPlugin{
	private final RSPL playerlistener = new RSPL(this);
	static String mainDir = "plugins/Reservations";
	static File config = new File (mainDir + File.separator + "config.yml");
	static FileConfiguration bukkitconfig;
	Yaml yaml = new Yaml();
	InputStream input = null;
	BufferedReader msgreader;
	BufferedWriter msgwriter;
	boolean usePermissions = false;
	File msgfile = new File(mainDir + File.separator + "message.txt");
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		Player player = (Player) sender;
		if(player.isOp()||usePermissions){
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
						player.sendMessage(ChatColor.RED + "/res set vipsorrymsg <message> " + ChatColor.BLUE + "Changes the message if a VIP can't join.");
						return true;
					}
					try{
						if(args[1].equalsIgnoreCase("kickmsg")){
							if(usePermissions&&!player.hasPermission("Reservations.set.kickmsg")&&!player.hasPermission("Reservations.set.*")&&!player.hasPermission("Reservations.*")){
								player.sendMessage(ChatColor.RED + "You don't have the permission!");
								return true;
							}
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
						if(args[1].equalsIgnoreCase("vipsorrymsg")){
							if(usePermissions&&!player.hasPermission("Reservations.set.vipsorrymsg")&&!player.hasPermission("Reservations.set.*")&&!player.hasPermission("Reservations.*")){
								player.sendMessage(ChatColor.RED + "You don't have the permission!");
								return true;
							}
							try{
								@SuppressWarnings("unused")
								String test = args[2];
							}catch(ArrayIndexOutOfBoundsException e){
								player.sendMessage(ChatColor.RED + "Please enter a message:");
								player.sendMessage(ChatColor.RED + "/res set vipsorrymsg <message>");
								return true;
							}
							setVIPSorryMsg(player, args[2]);
							return true;
						}
						if(args[1].equalsIgnoreCase("serverfullmsg")){
							if(usePermissions&&!player.hasPermission("Reservations.set.serverfullmsg")&&!player.hasPermission("Reservations.set.*")&&!player.hasPermission("Reservations.*")){
								player.sendMessage(ChatColor.RED + "You don't have the permission!");
								return true;
							}
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
					}catch(Exception e){}
				}
				if(args[0].equalsIgnoreCase("list")){
					if(usePermissions&&!player.hasPermission("Reservations.list")&&!player.hasPermission("Reservations.*")){
						player.sendMessage(ChatColor.RED + "You don't have the permission!");
						return true;
					}
					list(player);
				}
				if(args[0].equalsIgnoreCase("add")){
					if(usePermissions&&!player.hasPermission("Reservations.add")&&!player.hasPermission("Reservations.*")){
						player.sendMessage(ChatColor.RED + "You don't have the permission!");
						return true;
					}
					try{
						bukkitconfig.load(config);
						bukkitconfig.set("VIPs." + args[1], "");
						bukkitconfig.save(config);
					}catch(Exception e){}
					player.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to the VIP list.");
					return true;
				}
				if(args[0].equalsIgnoreCase("delete")){
					if(usePermissions&&!player.hasPermission("Reservations.delete")&&!player.hasPermission("Reservations.*")){
						player.sendMessage(ChatColor.RED + "You don't have the permission!");
						return true;
					}
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
		pm.registerEvents(playerlistener, this);
		new File (mainDir).mkdir();
		File configold = new File(mainDir + File.separator + "VIP.yml");
		if(configold.exists()){
			configold.renameTo(config);
			System.out.println("[Reservations]: Renamed VIP.yml to config.yml!");
			bukkitconfig = YamlConfiguration.loadConfiguration(config);
			bukkitconfig.set("Permissions", true);
			try{
				bukkitconfig.save(config);
			}catch(IOException e){}
		}
		bukkitconfig = YamlConfiguration.loadConfiguration(config);
		if(!(config.exists())){
			try{
				config.createNewFile();
				bukkitconfig.set("VIPs.HomerBond005", "");
				bukkitconfig.set("KickMsg", "A VIP joined and you were randomly selected for kicking.");
				bukkitconfig.set("ServerFullMsg", "The server is full!");
				bukkitconfig.set("VIPSorryMsg", "Bukkit is so sorry, but there are no non-VIPs that could be kicked.");
				bukkitconfig.set("Permissions", true);
				bukkitconfig.save(config);
				System.out.println("[Reservations]: VIP.yml created.");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		try{
			bukkitconfig.load(config);
		}catch(Exception e){}
		usePermissions = bukkitconfig.getBoolean("Permissions", false);
		System.out.println("[Reservations] using Permissions: " + usePermissions);
		System.out.println("[Reservations] is enabled!");
	}
	public void onDisable(){
		System.out.println("[Reservations] is disabled!");
	}
	public boolean isVIP(Player player){
		if(player.hasPermission("Reservations.VIP")){
			return true;
		}else{
			try{
				bukkitconfig.load(config);
			}catch(Exception e){}
			if(bukkitconfig.getString("VIPs." + player.getDisplayName(), "$%") != "$%"){
				return true;
			}else{
				return false;
			}
		}
	}
	public boolean isVIP(String name){
		try{
			bukkitconfig.load(config);
		}catch(Exception e){}
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
		player.sendMessage(ChatColor.RED + "/res set vipsorrymsg <message> " + ChatColor.BLUE + "Changes the message if a VIP can't join.");
	}
	private void setServerFull(Player player, String message) throws FileNotFoundException, IOException, InvalidConfigurationException{
		bukkitconfig.load(config);
		bukkitconfig.set("ServerFullMsg", message);
		bukkitconfig.save(config);
		player.sendMessage(ChatColor.GREEN + "Server-Full-Message set to:");
		player.sendMessage(message);
	}
	private void setKickMsg(Player player, String message) throws FileNotFoundException, IOException, InvalidConfigurationException{
		bukkitconfig.load(config);
		bukkitconfig.set("KickMsg", message);
		bukkitconfig.save(config);
		player.sendMessage(ChatColor.GREEN + "Kick-Message set to:");
		player.sendMessage(message);
	}
	private void setVIPSorryMsg(Player player, String message) throws FileNotFoundException, IOException, InvalidConfigurationException{
		bukkitconfig.load(config);
		bukkitconfig.set("VIPSorryMsg", message);
		bukkitconfig.save(config);
		player.sendMessage(ChatColor.GREEN + "VIP-Sorry-Message set to:");
		player.sendMessage(message);
	}
	private void list (Player player){
		player.sendMessage(ChatColor.GREEN + "Following players are VIPs: (Defined in VIP.yml)");
		try{
			bukkitconfig.load(config);
		}catch (Exception e){}
		Object[] VIPlist;
		try{
			VIPlist = bukkitconfig.getConfigurationSection("VIPs").getKeys(false).toArray();
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
		if(!isVIP(name)){
			player.sendMessage(ChatColor.RED + "The player " + ChatColor.GOLD + name + ChatColor.RED + " isn't a VIP!");
			player.sendMessage(ChatColor.RED + "If the player has the permission,you have to delete it manually");
			return;
		}else{
			try{
				bukkitconfig.load(config);
				bukkitconfig.set("VIPs." + name, null);
				bukkitconfig.save(config);
			}catch(Exception e){}
			player.sendMessage(ChatColor.GREEN + "Successfully deleted " + ChatColor.GOLD + name + ChatColor.GREEN + " from the VIP list.");
		}
	}
}
