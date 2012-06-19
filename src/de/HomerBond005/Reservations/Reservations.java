/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.Reservations;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Reservations extends JavaPlugin{
	private RSPL playerlistener;
	private boolean usePEXRanks;
	private PermissionsChecker pc;
	private Metrics metrics;
	private Logger log;
	private Updater updater;
	
	@Override
	public void onEnable() {
		log = getLogger();
		playerlistener = new RSPL(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerlistener, this);
		getConfig().addDefault("KickMsg", "Someone with a higher rank joined you were randomly selected for kicking.");
		getConfig().addDefault("ServerFullMsg", "The server is full!");
		getConfig().addDefault("SorryMsg", "No one was found with a lower rank. :(");
		getConfig().addDefault("Permissions", true);
		getConfig().addDefault("PEXRankSystem", false);
		getConfig().addDefault("defaultRank", 100);
		HashMap<String, Object> defaultRanks = new HashMap<String, Object>();
		defaultRanks.put("HomerBond005", 1);
		getConfig().addDefault("Ranks", defaultRanks);
		if(!getConfig().isSet("Broadcast")){
			getConfig().set("Broadcast", "[Reservations]: %lowerrank% have been kicked because %higherrank% joined.");
			log.log(Level.INFO, "Saved new config. Please check the messages!");
		}
		HashMap<String, Object> defaultVIPS = new HashMap<String, Object>();
		defaultVIPS.put("Admin", "");
		getConfig().addDefault("VIPs", defaultVIPS);
		getConfig().options().copyDefaults(true);
		saveConfig();
		pc = new PermissionsChecker(this, getConfig().getBoolean("Permissions", false));
		if(getConfig().getBoolean("PEXRankSystem", false)){
			if(pm.isPluginEnabled("PermissionsEx")){
				if(pc.pexmanager == null)
					pc.pexmanager = PermissionsEx.getPermissionManager();
				usePEXRanks = true;
				log.log(Level.INFO, "Using PEX based rank system!");
			}else{
				log.log(Level.WARNING, "Please enable PermissionsEx to use the PEX rank system!");
				usePEXRanks = false;
			}
		}else{
			usePEXRanks = false;
		}
		try{
			metrics = new Metrics(this);
			String submit;
			if(usePEXRanks)
				submit = "PEX ranks";
			else
				submit = "Config/Permission ranks";
			metrics.addCustomData(new Metrics.Plotter(submit) {
				@Override
				public int getValue() {
					return 1;
				}
			});
			metrics.start();
		}catch(IOException e){
			log.log(Level.WARNING, "Error while enabling Metrics.");
		}
		updater = new Updater(this);
		getServer().getPluginManager().registerEvents(updater, this);
		log.log(Level.INFO, "is enabled!");
	}
	
	@Override
	public void onDisable(){
		log.log(Level.INFO, "is disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		if(command.getName().toLowerCase().equals("reservations")){
			if(args.length == 0)
				args = new String[]{"help"};
			String cmdchar = "";
			Player player = null;
			if(sender instanceof Player){
				player = (Player) sender;
				cmdchar = "/";
			}
			if(args[0].equalsIgnoreCase("help")){
				sender.sendMessage(ChatColor.WHITE + "Reservations Help");
				sender.sendMessage(ChatColor.RED+cmdchar+"reser list   "+ChatColor.BLUE+"Lists all VIPs.");
				sender.sendMessage(ChatColor.RED+cmdchar+"reser add <player>   "+ChatColor.BLUE+"Adds a player to VIPs.");
				sender.sendMessage(ChatColor.RED+cmdchar+"reser delete <player>   "+ChatColor.BLUE+"Deletes a player from VIPs");
				sender.sendMessage(ChatColor.RED+cmdchar+"reser set kickmsg <message>   "+ChatColor.BLUE+"Changes the kick-message");
				sender.sendMessage(ChatColor.RED+cmdchar+"reser set serverfullmsg <message>   "+ChatColor.BLUE+"Changes the message if the server is full");
				sender.sendMessage(ChatColor.RED+cmdchar+"reser set sorrymsg <message> "+ChatColor.BLUE+"Changes the message if a someone can't join.");
			}else if(args[0].equalsIgnoreCase("set")){
				if(args.length < 2){
					sender.sendMessage(ChatColor.WHITE+"Reservations Help Message");
					sender.sendMessage(ChatColor.RED+cmdchar+"reser set kickmsg <message> "+ChatColor.BLUE+"Changes the kick message.");
					sender.sendMessage(ChatColor.RED+cmdchar+"reser set serverfullmsg <message> "+ChatColor.BLUE+"Changes the message if the server is full.");
					sender.sendMessage(ChatColor.RED+cmdchar+"reser set sorrymsg <message> "+ChatColor.BLUE+"Changes the message if someone can't join.");
				}else if(args[1].equalsIgnoreCase("kickmsg")){
					if(!has(sender, "Reservations.set.kickmsg")){
						pc.sendNoPermMsg(player);
						return true;
					}
					if(args.length < 3){
						sender.sendMessage(ChatColor.RED+"Please enter a message:");
						sender.sendMessage(ChatColor.RED+cmdchar+"reser set kickmsg <message>");
						return true;
					}
					setKickMsg(getLastString(args, 2));
					sender.sendMessage(ChatColor.GREEN+"Kick-Message set to:");
					sender.sendMessage(getLastString(args, 2));
					return true;
				}else if(args[1].equalsIgnoreCase("sorrymsg")){
					if(!has(sender, "Reservations.set.sorrymsg")){
						pc.sendNoPermMsg(player);
						return true;
					}
					if(args.length < 3){
						sender.sendMessage(ChatColor.RED+"Please enter a message:");
						sender.sendMessage(ChatColor.RED+cmdchar+"reser set sorrymsg <message>");
						return true;
					}
					setSorryMsg(getLastString(args, 2));
					sender.sendMessage(ChatColor.GREEN+"Sorry-Message set to:");
					sender.sendMessage(getLastString(args, 2));
					return true;
				}else if(args[1].equalsIgnoreCase("serverfullmsg")){
					if(!has(sender, "Reservations.set.serverfullmsg")){
						pc.sendNoPermMsg(player);
						return true;
					}
					if(args.length < 3){
						sender.sendMessage(ChatColor.RED+"Please enter a message:");
						sender.sendMessage(ChatColor.RED+cmdchar+"reser set serverfullmsg <message>");
						return true;
					}
					setServerFull(getLastString(args, 2));
					sender.sendMessage(ChatColor.GREEN+"Server-Full-Message set to:");
					sender.sendMessage(getLastString(args, 2));
					return true;
				}
			}else if(args[0].equalsIgnoreCase("list")){
				if(!has(sender, "Reservations.list")){
					pc.sendNoPermMsg(player);
					return true;
				}
				sender.sendMessage(ChatColor.GREEN+"Following players are VIPs: (Defined in config.yml)");
				sender.sendMessage(ChatColor.GRAY+list());
			}else if(args[0].equalsIgnoreCase("add")){
				if(!has(sender, "Reservations.add")){
					pc.sendNoPermMsg(player);
					return true;
				}
				if(args.length < 2){
					sender.sendMessage(ChatColor.RED+"Please enter a player:");
					sender.sendMessage(ChatColor.RED+cmdchar+"reser add <player>");
					return true;
				}
				getConfig().set("VIPs."+args[1], "");
				saveConfig();
				sender.sendMessage(ChatColor.GREEN+"Successfully added "+ChatColor.GOLD+args[1]+ChatColor.GREEN+" to the VIP list.");
				return true;
			}else if(args[0].equalsIgnoreCase("delete")){
				if(!has(sender, "Reservations.delete")){
					pc.sendNoPermMsg(player);
					return true;
				}
				if(args.length < 2){
					sender.sendMessage(ChatColor.RED+"Please enter a player:");
					sender.sendMessage(ChatColor.RED+cmdchar+"reser delete <player>");
					return true;
				}
				if(delete(args[1])){
					sender.sendMessage(ChatColor.GREEN+"Successfully deleted "+ChatColor.GOLD+args[1]+ChatColor.GREEN+" from the VIP list.");
				}else{
					sender.sendMessage(ChatColor.RED+"The player "+ChatColor.GOLD+args[1]+ChatColor.RED+" isn't a VIP!");
					sender.sendMessage(ChatColor.RED+"If the player has the permission,you have to delete it manually");
				}
			}
		}
		return true;
	}
	
	public boolean isVIP(Player player){
		if(pc.has(player, "Reservations.VIP")){
			return true;
		}else{
			reloadConfig();
			return getConfig().isSet("VIPs." + player.getName());
		}
	}
	
	private boolean isVIPDefined(String name){
		return getConfig().isSet("VIPs." + name);
	}
	
	private String getLastString(String[] arr, int start){
		String temp = "";
		for(int i = start; i < arr.length;i++){
			temp += " "+arr[i];
		}
		if(temp.length() != 0)
			temp = temp.substring(1, temp.length());
		return temp;
	}
	
	private void setServerFull(String message){
		reloadConfig();
		getConfig().set("ServerFullMsg", message);
		saveConfig();
	}
	
	private void setKickMsg(String message){
		reloadConfig();
		getConfig().set("KickMsg", message);
		saveConfig();
	}
	
	private void setSorryMsg(String message){
		reloadConfig();
		getConfig().set("SorryMsg", message);
		saveConfig();
	}
	
	private String list(){
		reloadConfig();
		String[] viplist;
		try{
			viplist = getConfig().getConfigurationSection("VIPs").getKeys(false).toArray(new String[0]);
		}catch(NullPointerException e){
			return "No VIPs in config.yml";
		}
		if(viplist.length == 0){
			return "No VIPs in config.yml";
		}
		String VIPString = "";
		for(int i = 0; i < viplist.length; i++){
			if(viplist.length == i + 1){
				VIPString += viplist[i];
			}else{
				VIPString += viplist[i] + ", ";
			}
		}
		return VIPString;
	}
	
	private boolean delete(String name){
		if(!isVIPDefined(name)){
			return false;
		}else{
			try{
				reloadConfig();
				getConfig().set("VIPs." + name, null);
				saveConfig();
			}catch(Exception e){
				return false;
			}
			return true;
		}
	}
	
	Player generateKickPlayer(Player joining){
		Map<String, Integer> unsortedmap = new HashMap<String, Integer>();
		Player[] players = getServer().getOnlinePlayers();
		for(Player player : players){
			if(!isVIP(player))
				unsortedmap.put(player.getName(), getRank(player));
		}
		if(unsortedmap.size() == 0){
			return null;
		}
		ValueComparator bvc =  new ValueComparator(unsortedmap);
        @SuppressWarnings("unchecked")
		TreeMap<String, Integer> sortedmap = new TreeMap<String, Integer>(bvc);
        sortedmap.putAll(unsortedmap);
        int ownrank = getRank(joining);
        List<String> possiblekickplayers = new ArrayList<String>();
        for(Entry<String, Integer> entry : sortedmap.entrySet()){
        	if(entry.getValue() > ownrank){
        		possiblekickplayers.add(entry.getKey());
        	}
        }
        if(sortedmap.size() == 0){
			return null;
		}
        String[] playerarray = possiblekickplayers.toArray(new String[0]);
        if(playerarray.length == 0){
        	return null;
        }
        return getServer().getPlayer(playerarray[(int) (Math.random()*playerarray.length)]);
	}
	
	public int getRank(Player player){
		if(usePEXRanks){
			return pc.pexmanager.getUser(player).getOptionInteger("rank", null, getConfig().getInt("defaultRank", 100));
		}
		if(pc.has(player, "Reservations.rank.1")){
			return 1;
		}if(pc.has(player, "Reservations.rank.2"))
			return 2;
		if(pc.has(player, "Reservations.rank.3"))
			return 3;
		if(pc.has(player, "Reservations.rank.4"))
			return 4;
		if(pc.has(player, "Reservations.rank.5"))
			return 5;
		reloadConfig();
		return getConfig().getInt("Ranks." + player, getConfig().getInt("defaultRank", 100));
	}
	
	private boolean has(CommandSender sender, String perm){
		if(sender instanceof Player){
			Player player = (Player)sender;
			return pc.has(player, perm);
		}else
			return true;
	}
	
}