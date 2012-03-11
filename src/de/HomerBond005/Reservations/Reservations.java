package de.HomerBond005.Reservations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

public class Reservations extends JavaPlugin{
	private final RSPL playerlistener = new RSPL(this);
	private static String mainDir = "plugins/Reservations";
	File config = new File (mainDir + File.separator + "config.yml");
	FileConfiguration bukkitconfig;
	private int permSys = 0;
	private boolean usePEXRanks;
	private PermissionManager pexmanager;
	private Random randomgen = new Random();
	int taskID;
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		Player player = (Player) sender;
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
							if(!hasPermission(player, "Reservations.set.kickmsg")&&!hasPermission(player, "Reservations.set.*")&&!hasPermission(player, "Reservations.*")){
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
						if(args[1].equalsIgnoreCase("sorrymsg")){
							if(!hasPermission(player, "Reservations.set.sorrymsg")&&!hasPermission(player, "Reservations.set.*")&&!hasPermission(player, "Reservations.*")){
								player.sendMessage(ChatColor.RED + "You don't have the permission!");
								return true;
							}
							try{
								@SuppressWarnings("unused")
								String test = args[2];
							}catch(ArrayIndexOutOfBoundsException e){
								player.sendMessage(ChatColor.RED + "Please enter a message:");
								player.sendMessage(ChatColor.RED + "/res set sorrymsg <message>");
								return true;
							}
							setSorryMsg(player, args[2]);
							return true;
						}
						if(args[1].equalsIgnoreCase("serverfullmsg")){
							if(!hasPermission(player, "Reservations.set.serverfullmsg")&&!hasPermission(player, "Reservations.set.*")&&!hasPermission(player, "Reservations.*")){
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
					if(!hasPermission(player, "Reservations.list")&&!hasPermission(player, "Reservations.*")){
						player.sendMessage(ChatColor.RED + "You don't have the permission!");
						return true;
					}
					list(player);
				}
				if(args[0].equalsIgnoreCase("add")){
					if(!hasPermission(player, "Reservations.add")&&!hasPermission(player, "Reservations.*")){
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
					if(!hasPermission(player, "Reservations.delete")&&!hasPermission(player, "Reservations.*")){
						player.sendMessage(ChatColor.RED + "You don't have the permission!");
						return true;
					}
					delete(player, args[1]);
				}
		}else{
			player.sendMessage(ChatColor.RED + "You have to be OP to use this command!");
		}
		return true;
	}
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerlistener, this);
		new File(mainDir).mkdir();
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
				bukkitconfig.set("KickMsg", "Someone with a higher rank joined you were randomly selected for kicking.");
				bukkitconfig.set("ServerFullMsg", "The server is full!");
				bukkitconfig.set("SorryMsg", "No one was found with a lower rank. :(");
				bukkitconfig.set("Permissions", true);
				bukkitconfig.set("PEXRankSystem", false);
				bukkitconfig.set("Ranks.HomerBond005", 1);
				bukkitconfig.set("defaultRank", 100);
				bukkitconfig.save(config);
				System.out.println("[Reservations]: config.yml created.");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		try{
			bukkitconfig.load(config);
		}catch(Exception e){}
		if(!bukkitconfig.isSet("Ranks")){
			bukkitconfig.set("PEXRankSystem", false);
			bukkitconfig.set("Ranks.HomerBond005", 1);
			bukkitconfig.set("defaultRank", 100);
			bukkitconfig.set("VIPSorryMsg", null);
			bukkitconfig.set("SorryMsg", "No one was found with a lower rank. :(");
			bukkitconfig.set("KickMsg", "Someone with a higher rank joined you were randomly selected for kicking.");
			try{
				bukkitconfig.save(config);
			}catch(IOException e){}
			System.out.println("[Reservations]: Saved new config. Please check the messages!");
		}
		if(bukkitconfig.getBoolean("Permissions", false)){
			if(pm.isPluginEnabled("PermissionsEx")){
				pexmanager = PermissionsEx.getPermissionManager();
				permSys = 1;
				System.out.println("[Reservations]: Using PermissionsEx");
			}else if(pm.isPluginEnabled("bPermissions")){
				permSys = 2;
				System.out.println("[Reservations]: Using bPermissions");
			}else{
				permSys = 3;
				System.out.println("[Reservations]: Using BukkitPermissions");
			}
		}else{
			permSys = 0;
			System.out.println("[Reservations]: Defaulting to OP-only");
		}
		if(bukkitconfig.getBoolean("PEXRankSystem", false)){
			if(pm.isPluginEnabled("PermissionsEx")){
				if(pexmanager == null)
					pexmanager = PermissionsEx.getPermissionManager();
				usePEXRanks = true;
				System.out.println("[Reservations]: Using PEX based rank system!");
			}else{
				System.out.println("[Reservations]: Please enable PermissionsEx to use the PEX rank system!");
				usePEXRanks = false;
			}
		}
		System.out.println("[Reservations] is enabled!");
	}
	public void onDisable(){
		System.out.println("[Reservations] is disabled!");
	}
	public boolean isVIP(Player player){
		if(hasPermission(player, "Reservations.VIP")){
			return true;
		}else{
			try{
				bukkitconfig.load(config);
			}catch(Exception e){}
			return bukkitconfig.isSet("VIPs." + player.getName());
		}
	}
	private boolean isVIPDefined(String name){
		try{
			bukkitconfig.load(config);
		}catch(Exception e){}
		return bukkitconfig.isSet("VIPs." + name);
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
	private void setSorryMsg(Player player, String message) throws FileNotFoundException, IOException, InvalidConfigurationException{
		bukkitconfig.load(config);
		bukkitconfig.set("SorryMsg", message);
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
		if(!isVIPDefined(name)){
			player.sendMessage(ChatColor.RED + "The player " + ChatColor.GOLD + name + ChatColor.RED + " isn't a VIP!");
			player.sendMessage(ChatColor.RED + "If the player has the permission,you have to delete it manually");
			return;
		}else{
			try{
				bukkitconfig.load(config);
				bukkitconfig.set("VIPs." + name, null);
				bukkitconfig.save(config);
			}catch(Exception e){return;}
			player.sendMessage(ChatColor.GREEN + "Successfully deleted " + ChatColor.GOLD + name + ChatColor.GREEN + " from the VIP list.");
		}
	}
	public Player generateKickPlayer(Player joining){
		Map<String, Integer> unsortedmap = new HashMap<String, Integer>();
		Player[] players = getServer().getOnlinePlayers();
		for(Player player : players){
			if(!isVIP(player))
				unsortedmap.put(player.getName(), getRank(player.getName()));
		}
		if(unsortedmap.size() == 0){
			return null;
		}
		System.out.println("UNSORTED");
		for(Entry<String, Integer> entry : unsortedmap.entrySet()){
        	System.out.println(entry.getKey() + "|" + entry.getValue());
        }
		ValueComparator bvc =  new ValueComparator(unsortedmap);
        @SuppressWarnings("unchecked")
		TreeMap<String, Integer> sortedmap = new TreeMap<String, Integer>(bvc);
        sortedmap.putAll(unsortedmap);
        System.out.println("SORTED");
        for(Entry<String, Integer> entry : sortedmap.entrySet()){
        	System.out.println(entry.getKey() + "|" + entry.getValue());
        }
        int ownrank = getRank(joining.getName());
        List<String> possiblekickplayers = new ArrayList<String>();
        for(Entry<String, Integer> entry : sortedmap.entrySet()){
        	if(entry.getValue() > ownrank){
        		possiblekickplayers.add(entry.getKey());
        	}
        }
        if(sortedmap.size() == 0){
			return null;
		}
        System.out.println("SORTED lower rank");
        for(Entry<String, Integer> entry : sortedmap.entrySet()){
        	System.out.println(entry.getKey() + "|" + entry.getValue());
        }
        String[] playerarray = possiblekickplayers.toArray(new String[0]);
        if(playerarray.length == 0){
        	return null;
        }
        return getServer().getPlayer(playerarray[randomgen.nextInt(playerarray.length)]);
	}
	private int getRank(String player){
		if(usePEXRanks){
			return pexmanager.getUser(player).getOptionInteger("rank", "", bukkitconfig.getInt("defaultRank", 100));
		}
		try{
			bukkitconfig.load(config);
		}catch (Exception e){}
		return bukkitconfig.getInt("Ranks." + player, bukkitconfig.getInt("defaultRank", 100));
	}
	public boolean hasPermission(Player player, String permission){
		if(permSys == 0){
			return player.isOp();
		}else if(permSys == 1){
			return pexmanager.has(player, permission);
		}else if(permSys == 2){
			return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), permission);
		}else if(permSys == 3){
			return hasPermission(player, permission);
		}else{
			return false;
		}
	}
	public void cancelTask(){
		Bukkit.getScheduler().cancelTask(taskID);
	}
}
