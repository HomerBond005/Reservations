package com.bukkit.HomerBond005.Reservations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bukkit.HomerBond005.Reservations.RSPL;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.yaml.snakeyaml.Yaml;

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
				if(args[0].equalsIgnoreCase("message")){
					try{
						@SuppressWarnings("unused")
						String test = args[1];
					}catch(ArrayIndexOutOfBoundsException e){
						player.sendMessage(ChatColor.WHITE + "Reservations Help Message");
						player.sendMessage(ChatColor.RED + "/res message get               " + ChatColor.BLUE + "Show the kick message.");
						player.sendMessage(ChatColor.RED + "/res message set <message> " + ChatColor.BLUE + "Changes the kick message.");
						return true;
					}
					if(args[1].equalsIgnoreCase("set")){
						try{
							@SuppressWarnings("unused")
							String test = args[2];
						}catch(ArrayIndexOutOfBoundsException e){
							player.sendMessage(ChatColor.RED + "Please enter a message:");
							player.sendMessage(ChatColor.RED + "/res message set <message>");
							return true;
						}
						messageset(player, args[2]);
						return true;
					}
					if(args[1].equalsIgnoreCase("get")){
						messageget(player);
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("list")){
					list(player);
				}
				if(args[0].equalsIgnoreCase("add")){
					FileInputStream input = null;
					try {
						input = new FileInputStream(config);
					} catch (FileNotFoundException e){
						e.printStackTrace();
					}
					@SuppressWarnings("unchecked")
					Map<Object, Object> VIPyml = (Map<Object, Object>)yaml.load(input);
					VIPyml.put(args[1], "");
					bukkitconfig.load();
					bukkitconfig.setProperty(args[1], "");
					bukkitconfig.save();
					player.sendMessage(ChatColor.GREEN + "Successfully added " + args[1] + " to the VIP list.");
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
				bukkitconfig.setProperty("HomerBond005", "");
				bukkitconfig.save();
				System.out.println("[Reservations]: VIP.yml created.");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		try {
			input = new FileInputStream(config);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("[Reservations]: VIP.yml loaded.");
		@SuppressWarnings("unchecked")
		Map<Object, Object> VIPyml = (Map<Object, Object>)yaml.load(input);
		int VIPs = VIPyml.size();
		if(getServer().getMaxPlayers() < VIPs){
			System.err.println("Error while enabling [Reservations]: The max. players size is smaller than the configured VIPs!");
		}
		if(!msgfile.exists()){
			try{
				msgfile.createNewFile();
				msgwriter = new BufferedWriter(new FileWriter(msgfile));
				msgwriter.write("The server is full!");
				msgwriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("[Reservations] is enabled!");
	}
	public void onDisable(){
		System.out.println("[Reservations] is disabled!");
	}
	public String getMessage(){
		String msg;
		try{
			msgreader = new BufferedReader(new FileReader(mainDir + File.separator + "message.txt"));
			msg = msgreader.readLine();
			msgreader.close();
			msg = msg.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		}catch(IOException e){
			System.out.println("[Reservations]: Error while reading message.txt. Using 'The server is full!' for kick message.");
			msg = "The server is full!";
		}
		return msg;
	}
	public boolean canJoin(Player player){
		FileInputStream input = null;
		try {
			input = new FileInputStream(config);
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		Map<Object, Object> VIPyml = (Map<Object, Object>)yaml.load(input);
		int VIPs = VIPyml.size();
		if(getServer().getOnlinePlayers().length >= (getServer().getMaxPlayers() - VIPs)){
			Set<Object> names = VIPyml.keySet();
			if(names.contains(player.getDisplayName())){
				return true;
			}
			return false;
		}else{
			return true;
		}
	}
	//Functions for Commands
	private void help(Player player){
		player.sendMessage(ChatColor.WHITE + "Reservations Help");
		player.sendMessage(ChatColor.RED + "/res list   " + ChatColor.BLUE + "Lists all VIPs.");
		player.sendMessage(ChatColor.RED + "/res add <player>   " + ChatColor.BLUE + "Adds a player to VIPs.");
		player.sendMessage(ChatColor.RED + "/res delete <player>   " + ChatColor.BLUE + "Deletes a player from VIPs");
	}
	private void messageget(Player player){
		player.sendMessage("Message:");
		player.sendMessage(getMessage());
	}
	private void messageset(Player player, String message){
		try {
			new File(mainDir + File.separator + "message.txt").delete();
			new File(mainDir + File.separator + "message.txt").createNewFile();
			msgwriter = new BufferedWriter(new FileWriter(mainDir + File.separator + "message.txt"));
			msgwriter.write(message);
			msgwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.sendMessage(ChatColor.GREEN + "Message set to:");
		player.sendMessage(getMessage());
	}
	private void list (Player player){
		FileInputStream input = null;
		try {
			input = new FileInputStream(config);
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		HashMap<Object, Object> VIPyml = (HashMap<Object, Object>)yaml.load(input);
		player.sendMessage(ChatColor.GREEN + "Following players are VIPs:");
		Object[] VIPlist = VIPyml.keySet().toArray();
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
		FileInputStream input = null;
		try {
			input = new FileInputStream(config);
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		HashMap<Object, Object> VIPyml = (HashMap<Object, Object>)yaml.load(input);
		Object[] VIPlist = VIPyml.keySet().toArray();
		Boolean isVIP = false;
		for(int i = 0; i < VIPlist.length; i++){
			if(name.equalsIgnoreCase(VIPlist[i].toString())){
				isVIP = true;
			}
		}
		if(isVIP == false){
			player.sendMessage(ChatColor.RED + "The player " + name + " isn't a VIP!");
			return;
		}else{
			bukkitconfig.load();
			bukkitconfig.removeProperty(name);
			bukkitconfig.save();
			player.sendMessage(ChatColor.GREEN + "Successfully deleted " + name + " from the VIP list.");
		}
	}
}
