/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
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
import de.HomerBond005.Permissions.PermissionsChecker;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Reservations extends JavaPlugin{
	private final RSPL playerlistener = new RSPL(this);
	private static String mainDir = "plugins/Reservations";
	File config = new File (mainDir + File.separator + "config.yml");
	FileConfiguration bukkitconfig;
	private boolean usePEXRanks;
	PermissionsChecker pc;
	private Random randomgen = new Random();
	int taskID;
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		if(command.getName().toLowerCase().equals("reservations")){
			if(sender instanceof Player){
				Player player = (Player) sender;
				try{
					@SuppressWarnings("unused")
					String test = args[0];
				}catch(ArrayIndexOutOfBoundsException e){
					help(player);
					return true;
				}
				if(args[0].equalsIgnoreCase("help")){
					help(player);
					return true;
				}
				if(args[0].equalsIgnoreCase("set")){
					try{
						@SuppressWarnings("unused")
						String test = args[1];
					}catch(ArrayIndexOutOfBoundsException e){
						player.sendMessage(ChatColor.WHITE + "Reservations Help Message");
						player.sendMessage(ChatColor.RED + "/reser set kickmsg <message> " + ChatColor.BLUE + "Changes the kick message.");
						player.sendMessage(ChatColor.RED + "/reser set serverfullmsg <message> " + ChatColor.BLUE + "Changes the message if the server is full.");
						player.sendMessage(ChatColor.RED + "/reser set sorrymsg <message> " + ChatColor.BLUE + "Changes the message if someone can't join.");
						return true;
					}
					try{
						if(args[1].equalsIgnoreCase("kickmsg")){
							if(!pc.has(player, "Reservations.set.kickmsg")&&!pc.has(player, "Reservations.set.*")&&!pc.has(player, "Reservations.*")){
								pc.sendNoPermMsg(player);
								return true;
							}
							try{
								@SuppressWarnings("unused")
								String test = args[2];
							}catch(ArrayIndexOutOfBoundsException e){
								player.sendMessage(ChatColor.RED + "Please enter a message:");
								player.sendMessage(ChatColor.RED + "/reser set kickmsg <message>");
								return true;
							}
							setKickMsg(args[2]);
							player.sendMessage(ChatColor.GREEN + "Kick-Message set to:");
							player.sendMessage(args[2]);
							return true;
						}
						if(args[1].equalsIgnoreCase("sorrymsg")){
							if(!pc.has(player, "Reservations.set.sorrymsg")&&!pc.has(player, "Reservations.set.*")&&!pc.has(player, "Reservations.*")){
								pc.sendNoPermMsg(player);
								return true;
							}
							try{
								@SuppressWarnings("unused")
								String test = args[2];
							}catch(ArrayIndexOutOfBoundsException e){
								player.sendMessage(ChatColor.RED + "Please enter a message:");
								player.sendMessage(ChatColor.RED + "/reser set sorrymsg <message>");
								return true;
							}
							setSorryMsg(args[2]);
							player.sendMessage(ChatColor.GREEN + "Sorry-Message set to:");
							player.sendMessage(args[2]);
							return true;
						}
						if(args[1].equalsIgnoreCase("serverfullmsg")){
							if(!pc.has(player, "Reservations.set.serverfullmsg")&&!pc.has(player, "Reservations.set.*")&&!pc.has(player, "Reservations.*")){
								pc.sendNoPermMsg(player);
								return true;
							}
							try{
								@SuppressWarnings("unused")
								String test = args[2];
							}catch(ArrayIndexOutOfBoundsException e){
								player.sendMessage(ChatColor.RED + "Please enter a message:");
								player.sendMessage(ChatColor.RED + "/reser set serverfullmsg <message>");
								return true;
							}
							setServerFull(args[2]);
							player.sendMessage(ChatColor.GREEN + "Server-Full-Message set to:");
							player.sendMessage(args[2]);
							return true;
						}
					}catch(Exception e){}
				}
				if(args[0].equalsIgnoreCase("list")){
					if(!pc.has(player, "Reservations.list")&&!pc.has(player, "Reservations.*")){
						pc.sendNoPermMsg(player);
						return true;
					}
					player.sendMessage(ChatColor.GREEN + "Following players are VIPs: (Defined in VIP.yml)");
					player.sendMessage(ChatColor.GRAY + list());
				}
				if(args[0].equalsIgnoreCase("add")){
					if(!pc.has(player, "Reservations.add")&&!pc.has(player, "Reservations.*")){
						pc.sendNoPermMsg(player);
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
					if(!pc.has(player, "Reservations.delete")&&!pc.has(player, "Reservations.*")){
						pc.sendNoPermMsg(player);
						return true;
					}
					if(delete(args[1])){
						player.sendMessage(ChatColor.GREEN + "Successfully deleted " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " from the VIP list.");
					}else{
						player.sendMessage(ChatColor.RED + "The player " + ChatColor.GOLD + args[1] + ChatColor.RED + " isn't a VIP!");
						player.sendMessage(ChatColor.RED + "If the player has the permission,you have to delete it manually");
					}
				}
			}else{
				try{
					@SuppressWarnings("unused")
					String test = args[0];
				}catch(ArrayIndexOutOfBoundsException e){
					help();
					return true;
				}
				if(args[0].equalsIgnoreCase("help")){
					help();
					return true;
				}
				if(args[0].equalsIgnoreCase("set")){
					try{
						@SuppressWarnings("unused")
						String test = args[1];
					}catch(ArrayIndexOutOfBoundsException e){
						System.out.println("Reservations Help Message");
						System.out.println("/reser set kickmsg <message> " + ChatColor.BLUE + "Changes the kick message.");
						System.out.println("/reser set serverfullmsg <message> " + ChatColor.BLUE + "Changes the message if the server is full.");
						System.out.println("/reser set sorrymsg <message> " + ChatColor.BLUE + "Changes the message if someone can't join.");
						return true;
					}
					try{
						if(args[1].equalsIgnoreCase("kickmsg")){
							try{
								@SuppressWarnings("unused")
								String test = args[2];
							}catch(ArrayIndexOutOfBoundsException e){
								System.out.println("[Reservations]: Please enter a message:");
								System.out.println("[Reservations]: /reser set kickmsg <message>");
								return true;
							}
							setKickMsg(args[2]);
							System.out.println("Kick-Message set to:");
							System.out.println("[Reservations]: " + args[2]);
							return true;
						}
						if(args[1].equalsIgnoreCase("sorrymsg")){
							try{
								@SuppressWarnings("unused")
								String test = args[2];
							}catch(ArrayIndexOutOfBoundsException e){
								System.out.println("[Reservations]: Please enter a message:");
								System.out.println("[Reservations]: /reser set sorrymsg <message>");
								return true;
							}
							setSorryMsg(args[2]);
							System.out.println("[Reservations]: Sorry-Message set to:");
							System.out.println("[Reservations]: " + args[2]);
							return true;
						}
						if(args[1].equalsIgnoreCase("serverfullmsg")){
							try{
								@SuppressWarnings("unused")
								String test = args[2];
							}catch(ArrayIndexOutOfBoundsException e){
								System.out.println("[Reservations]: Please enter a message:");
								System.out.println("[Reservations]: /reser set serverfullmsg <message>");
								return true;
							}
							setServerFull(args[2]);
							System.out.println("[Reservations]: Server-Full-Message set to:");
							System.out.println("[Reservations]: " + args[2]);
							return true;
						}
					}catch(Exception e){}
				}
				if(args[0].equalsIgnoreCase("list")){
					System.out.println("[Reservations]: Following players are VIPs: (Defined in VIP.yml)");
					System.out.println("[Reservations]: " + list());
				}
				if(args[0].equalsIgnoreCase("add")){
					try{
						bukkitconfig.load(config);
						bukkitconfig.set("VIPs." + args[1], "");
						bukkitconfig.save(config);
					}catch(Exception e){}
					System.out.println("[Reservations]: Successfully added " + args[1] + " to the VIP list.");
					return true;
				}
				if(args[0].equalsIgnoreCase("delete")){
					if(delete(args[1])){
						System.out.println("[Reservations]: Successfully deleted " + args[1] + " from the VIP list.");
					}else{
						System.out.println("[Reservations]: The player " + args[1] + " isn't a VIP!");
						System.out.println("[Reservations]: If the player has the permission, you have to delete it manually.");
					}
				}
			}
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
			System.out.println("[Reservations]: Saved new config. Please check the messages!");
		}
		if(!bukkitconfig.isSet("Broadcast")){
			bukkitconfig.set("Broadcast", "[Reservations]: %lowerrank% have been kicked because %higherrank% joined.");
			System.out.println("[Reservations]: Saved new config. Please check the messages!");
		}
		try{
			bukkitconfig.save(config);
		}catch(IOException e){}
		pc = new PermissionsChecker(this, bukkitconfig.getBoolean("Permissions", false));
		if(bukkitconfig.getBoolean("PEXRankSystem", false)){
			if(pm.isPluginEnabled("PermissionsEx")){
				if(pc.pexmanager == null)
					pc.pexmanager = PermissionsEx.getPermissionManager();
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
		if(pc.has(player, "Reservations.VIP")){
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
		player.sendMessage(ChatColor.RED + "/reser list   " + ChatColor.BLUE + "Lists all VIPs.");
		player.sendMessage(ChatColor.RED + "/reser add <player>   " + ChatColor.BLUE + "Adds a player to VIPs.");
		player.sendMessage(ChatColor.RED + "/reser delete <player>   " + ChatColor.BLUE + "Deletes a player from VIPs");
		player.sendMessage(ChatColor.RED + "/reser set kickmsg <message>   " + ChatColor.BLUE + "Changes the kick-message");
		player.sendMessage(ChatColor.RED + "/reser set serverfullmsg <message>   " + ChatColor.BLUE + "Changes the message if the server is full");
		player.sendMessage(ChatColor.RED + "/reser set sorrymsg <message> " + ChatColor.BLUE + "Changes the message if a someone can't join.");
	}
	private void help(){
		System.out.println("Reservations Help");
		System.out.println("/reser list   Lists all VIPs.");
		System.out.println("/reser add <player>   Adds a player to VIPs.");
		System.out.println("/reser delete <player>   Deletes a player from VIPs");
		System.out.println("/reser set kickmsg <message>   Changes the kick-message");
		System.out.println("/reser set serverfullmsg <message>   Changes the message if the server is full");
		System.out.println("/reser set sorrymsg <message> Changes the message if someone can't join.");
	}
	private void setServerFull(String message) throws FileNotFoundException, IOException, InvalidConfigurationException{
		bukkitconfig.load(config);
		bukkitconfig.set("ServerFullMsg", message);
		bukkitconfig.save(config);
	}
	private void setKickMsg(String message) throws FileNotFoundException, IOException, InvalidConfigurationException{
		bukkitconfig.load(config);
		bukkitconfig.set("KickMsg", message);
		bukkitconfig.save(config);
	}
	private void setSorryMsg(String message) throws FileNotFoundException, IOException, InvalidConfigurationException{
		bukkitconfig.load(config);
		bukkitconfig.set("SorryMsg", message);
		bukkitconfig.save(config);
	}
	private String list(){
		try{
			bukkitconfig.load(config);
		}catch (Exception e){}
		Object[] VIPlist;
		try{
			VIPlist = bukkitconfig.getConfigurationSection("VIPs").getKeys(false).toArray();
		}catch(NullPointerException e){
			return "No VIPs in VIP.yml";
		}
		if(VIPlist.length == 0){
			return "No VIPs in VIP.yml";
		}
		String VIPString = "";
		for(int i = 0; i < VIPlist.length; i++){
			if(VIPlist.length == i + 1){
				VIPString += VIPlist[i];
			}else{
				VIPString += VIPlist[i] + ", ";
			}
		}
		return VIPString;
	}
	private boolean delete(String name){
		if(!isVIPDefined(name)){
			return false;
		}else{
			try{
				bukkitconfig.load(config);
				bukkitconfig.set("VIPs." + name, null);
				bukkitconfig.save(config);
			}catch(Exception e){
				return false;
			}
			return true;
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
		/*System.out.println("UNSORTED");
		for(Entry<String, Integer> entry : unsortedmap.entrySet()){
        	System.out.println(entry.getKey() + "|" + entry.getValue());
        }*/
		ValueComparator bvc =  new ValueComparator(unsortedmap);
        @SuppressWarnings("unchecked")
		TreeMap<String, Integer> sortedmap = new TreeMap<String, Integer>(bvc);
        sortedmap.putAll(unsortedmap);
        /*System.out.println("SORTED");
        for(Entry<String, Integer> entry : sortedmap.entrySet()){
        	System.out.println(entry.getKey() + "|" + entry.getValue());
        }*/
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
        /*System.out.println("SORTED lower rank");
        for(Entry<String, Integer> entry : sortedmap.entrySet()){
        	System.out.println(entry.getKey() + "|" + entry.getValue());
        }*/
        String[] playerarray = possiblekickplayers.toArray(new String[0]);
        if(playerarray.length == 0){
        	return null;
        }
        return getServer().getPlayer(playerarray[randomgen.nextInt(playerarray.length)]);
	}
	private int getRank(String player){
		if(usePEXRanks){
			return pc.pexmanager.getUser(player).getOptionInteger("rank", "", bukkitconfig.getInt("defaultRank", 100));
		}
		try{
			bukkitconfig.load(config);
		}catch (Exception e){}
		return bukkitconfig.getInt("Ranks." + player, bukkitconfig.getInt("defaultRank", 100));
	}
	public void cancelTask(){
		Bukkit.getScheduler().cancelTask(taskID);
	}
}
