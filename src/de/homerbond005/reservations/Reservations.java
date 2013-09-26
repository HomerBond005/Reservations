/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.homerbond005.reservations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import de.homerbond005.reservations.Metrics.Graph;

public class Reservations extends JavaPlugin {
	private RSPL playerlistener;
	private boolean usePEXRanks;
	private boolean kickLowestRank;
	private Metrics metrics;
	private Logger log;
	private int permissionBasedRanks;
	private List<String> vips;
	private boolean preventKickFromAnotherLocationLogin;
	private String loginFromAnotherLocationMessage;
	private String broadcastMsg;
	private String sorryMsg;
	private String kickMsg;
	private PermissionManager pexmanager;

	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		log = getLogger();
		playerlistener = new RSPL(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerlistener, this);
		getConfig().addDefault("KickMsg", "Someone with a higher rank joined you were randomly selected for kicking.");
		getConfig().addDefault("SorryMsg", "No one was found with a lower rank. :(");
		getConfig().addDefault("Permissions", true);
		getConfig().addDefault("PEXRankSystem", false);
		getConfig().addDefault("defaultRank", 100);
		getConfig().addDefault("kickLowestRank", false);
		getConfig().addDefault("permissionBasedRanks", 10);
		getConfig().addDefault("Broadcast", "[Reservations]: %lowerrank% has been kicked because %higherrank% joined.");
		getConfig().addDefault("preventKickFromAnotherLocationLogin", true);
		getConfig().addDefault("loginFromAnotherLocationMessage", "You are already logged in from another location!");
		HashMap<String, Object> defaultRanks = new HashMap<String, Object>();
		defaultRanks.put("HomerBond005", 1);
		getConfig().addDefault("Ranks", defaultRanks);
		permissionBasedRanks = getConfig().getInt("permissionBasedRanks");
		List<String> defaultVIPs = new LinkedList<String>();
		defaultVIPs.add("Admin");
		defaultVIPs.add("HomerBond005");
		getConfig().addDefault("VIPs", defaultVIPs);
		getConfig().addDefault("updateReminderEnabled", true);
		getConfig().options().copyDefaults(true);
		saveConfig();
		if (getConfig().isConfigurationSection("VIPs")) {
			getConfig().set("VIPs", new LinkedList<String>(getConfig().getConfigurationSection("VIPs").getKeys(false)));
			saveConfig();
		}
		vips = new LinkedList<String>();
		for (String vip : getConfig().getStringList("VIPs")) {
			vips.add(vip.toLowerCase());
		}
		kickLowestRank = getConfig().getBoolean("kickLowestRank");
		kickMsg = getConfig().getString("KickMsg");
		sorryMsg = getConfig().getString("SorryMsg");
		broadcastMsg = getConfig().getString("Broadcast");
		preventKickFromAnotherLocationLogin = getConfig().getBoolean("preventKickFromAnotherLocationLogin");
		loginFromAnotherLocationMessage = getConfig().getString("loginFromAnotherLocationMessage");
		if (getConfig().getBoolean("PEXRankSystem", false)) {
			if (pm.isPluginEnabled("PermissionsEx")) {
				pexmanager = PermissionsEx.getPermissionManager();
				usePEXRanks = true;
				log.log(Level.INFO, "Using PEX based rank system!");
			} else {
				log.log(Level.WARNING, "Please enable PermissionsEx to use the PEX rank system!");
				usePEXRanks = false;
			}
		} else {
			usePEXRanks = false;
		}
		try {
			metrics = new Metrics(this);
			String submit;
			if (usePEXRanks)
				submit = "PEX ranks";
			else
				submit = "Config/Permission ranks";
			Graph typegraph = metrics.createGraph("Rank system");
			typegraph.addPlotter(new Metrics.Plotter(submit) {
				@Override
				public int getValue() {
					return 1;
				}
			});
			metrics.start();
		} catch (IOException e) {
			log.log(Level.WARNING, "Error while enabling Metrics.");
		}
		log.log(Level.INFO, "is enabled!");
	}

	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() {
		log.log(Level.INFO, "is disabled!");
	}

	/**
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender,
	 *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		if (command.getName().toLowerCase().equals("reservations")) {
			if (args.length == 0)
				args = new String[] { "help" };
			String cmdchar = "";
			Player player = null;
			if (sender instanceof Player) {
				player = (Player) sender;
				cmdchar = "/";
			}
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Reservations Help");
				sender.sendMessage(ChatColor.GOLD + cmdchar + "reser list   " + ChatColor.GRAY + "Lists all VIPs.");
				sender.sendMessage(ChatColor.GOLD + cmdchar + "reser add <player>   " + ChatColor.GRAY + "Adds a player to VIPs.");
				sender.sendMessage(ChatColor.GOLD + cmdchar + "reser delete <player>   " + ChatColor.GRAY + "Deletes a player from VIPs");
				sender.sendMessage(ChatColor.GOLD + cmdchar + "reser set kickmsg <message>   " + ChatColor.GRAY + "Changes the kick-message");
				sender.sendMessage(ChatColor.GOLD + cmdchar + "reser set sorrymsg <message> " + ChatColor.GRAY + "Changes the message if a someone can't join.");
			} else if (args[0].equalsIgnoreCase("set")) {
				if (args.length < 2) {
					sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Reservations Help Message");
					sender.sendMessage(ChatColor.GOLD + cmdchar + "reser set kickmsg <message> " + ChatColor.GRAY + "Changes the kick message.");
					sender.sendMessage(ChatColor.GOLD + cmdchar + "reser set sorrymsg <message> " + ChatColor.GRAY + "Changes the message if someone can't join.");
				} else if (args[1].equalsIgnoreCase("kickmsg")) {
					if (!has(sender, "Reservations.set.kickmsg")) {
						player.sendMessage(ChatColor.RED + "You do not have the required permission!");
						return true;
					}
					if (args.length < 3) {
						sender.sendMessage(ChatColor.RED + "Please enter a message:");
						sender.sendMessage(ChatColor.RED + cmdchar + "reser set kickmsg <message>");
						return true;
					}
					setKickMsg(getLastString(args, 2));
					sender.sendMessage(ChatColor.GREEN + "Kick-Message set to:");
					sender.sendMessage(getLastString(args, 2));
					return true;
				} else if (args[1].equalsIgnoreCase("sorrymsg")) {
					if (!has(sender, "Reservations.set.sorrymsg")) {
						player.sendMessage(ChatColor.RED + "You do not have the required permission!");
						return true;
					}
					if (args.length < 3) {
						sender.sendMessage(ChatColor.RED + "Please enter a message:");
						sender.sendMessage(ChatColor.RED + cmdchar + "reser set sorrymsg <message>");
						return true;
					}
					setSorryMsg(getLastString(args, 2));
					sender.sendMessage(ChatColor.GREEN + "Sorry-Message set to:");
					sender.sendMessage(getLastString(args, 2));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (!has(sender, "Reservations.list")) {
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + "Following players are VIPs: (Defined in config.yml)");
				sender.sendMessage(ChatColor.GRAY + list());
			} else if (args[0].equalsIgnoreCase("add")) {
				if (!has(sender, "Reservations.add")) {
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Please enter a player:");
					sender.sendMessage(ChatColor.RED + cmdchar + "reser add <player>");
					return true;
				}
				vips.add(args[1].toLowerCase());
				reloadConfig();
				getConfig().set("VIPs", vips);
				saveConfig();
				sender.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to the VIP list.");
				return true;
			} else if (args[0].equalsIgnoreCase("delete")) {
				if (!has(sender, "Reservations.delete")) {
					player.sendMessage(ChatColor.RED + "You do not have the required permission!");
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Please enter a player:");
					sender.sendMessage(ChatColor.RED + cmdchar + "reser delete <player>");
					return true;
				}
				if (deleteVIP(args[1])) {
					sender.sendMessage(ChatColor.GREEN + "Successfully deleted " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " from the VIP list.");
				} else {
					sender.sendMessage(ChatColor.RED + "The player " + ChatColor.GOLD + args[1] + ChatColor.RED + " isn't a VIP!");
					sender.sendMessage(ChatColor.RED + "If the player has the permission, you have to delete it manually");
				}
			}
		}
		return true;
	}

	/**
	 * Check if a player is a VIP
	 * 
	 * @param player
	 * @return
	 */
	public boolean isVIP(Player player) {
		if (player.hasPermission("Reservations.VIP"))
			return true;
		else
			return isVIPDefined(player.getName());
	}

	/**
	 * Check if a player is defined as VIP in the config
	 * 
	 * @param name The player name
	 * @return Is he defined as VIP in the config?
	 */
	private boolean isVIPDefined(String name) {
		return vips.contains(name.toLowerCase());
	}

	/**
	 * Take the elements after a start index from an array and separate them
	 * with a space
	 * 
	 * @param arr The array that should be handled
	 * @param start The start index
	 * @return A string with the parts of the array
	 */
	private String getLastString(String[] arr, int start) {
		String temp = "";
		for (int i = start; i < arr.length; i++) {
			temp += " " + arr[i];
		}
		if (temp.length() != 0)
			temp = temp.substring(1, temp.length());
		return temp;
	}

	/**
	 * Set the kick message
	 * 
	 * @param message The new kick message
	 */
	private void setKickMsg(String message) {
		reloadConfig();
		getConfig().set("KickMsg", message);
		kickMsg = getConfig().getString("KickMsg");
		saveConfig();
	}

	/**
	 * Set the sorry message
	 * 
	 * @param message The new sorry message
	 */
	private void setSorryMsg(String message) {
		reloadConfig();
		getConfig().set("SorryMsg", message);
		kickMsg = getConfig().getString("KickMsg");
		saveConfig();
	}

	/**
	 * List all VIPs defined in the config
	 * 
	 * @return A string with all VIPs defined in the config
	 */
	private String list() {
		reloadConfig();
		if (vips.size() == 0) {
			return "No VIPs in config.yml";
		}
		String VIPString = "";
		for (int i = 0; i < vips.size(); i++) {
			if (vips.size() == i + 1) {
				VIPString += vips.get(i);
			} else {
				VIPString += vips.get(i) + ", ";
			}
		}
		return VIPString;
	}

	/**
	 * Delete a VIP from the config
	 * 
	 * @param name The name of the VIP
	 * @return Success?
	 */
	private boolean deleteVIP(String name) {
		if (!isVIPDefined(name)) {
			return false;
		} else {
			try {
				reloadConfig();
				vips.remove(name.toLowerCase());
				getConfig().set("VIPs", vips);
				saveConfig();
			} catch (Exception e) {
				return false;
			}
			return true;
		}
	}

	/**
	 * Generate a player that could be kicked based on a player that tries to
	 * join
	 * 
	 * @param joining The joining player
	 * @return A player that could be kicked or null if no one could be kicked
	 */
	protected Player generateKickPlayer(Player joining) {
		Map<String, Integer> unsortedmap = new HashMap<String, Integer>();
		Player[] players = getServer().getOnlinePlayers();
		for (Player player : players) {
			if (!isVIP(player))
				unsortedmap.put(player.getName(), getRank(player));
		}
		if (unsortedmap.size() == 0)
			return null;
		ValueComparator bvc = new ValueComparator(unsortedmap);
		@SuppressWarnings("unchecked")
		TreeMap<String, Integer> sortedmap = new TreeMap<String, Integer>(bvc);
		sortedmap.putAll(unsortedmap);
		int ownRank = getRank(joining);
		List<String> possiblekickplayers = new ArrayList<String>();
		if (!kickLowestRank)
			for (Entry<String, Integer> entry : sortedmap.entrySet()) {
				if (entry.getValue() > ownRank) {
					possiblekickplayers.add(entry.getKey());
				}
			}
		else {
			int firstRank = -1;
			for (Entry<String, Integer> entry : sortedmap.entrySet()) {
				if (firstRank == -1)
					firstRank = entry.getValue();
				if (ownRank >= entry.getValue())
					break;
				else if (ownRank < entry.getValue()) {
					if (firstRank == entry.getValue())
						possiblekickplayers.add(entry.getKey());
					else if (possiblekickplayers.size() == 0)
						firstRank = entry.getValue();
					else
						break;
				}
			}
		}
		if (sortedmap.size() == 0) {
			return null;
		}
		String[] playerarray = possiblekickplayers.toArray(new String[0]);
		if (playerarray.length == 0) {
			return null;
		}
		return getServer().getPlayer(playerarray[(int) (Math.random() * playerarray.length)]);
	}

	/**
	 * Get the rank of a player
	 * 
	 * @param player A player
	 * @return The rank of the player
	 */
	public int getRank(Player player) {
		if (usePEXRanks)
			return pexmanager.getUser(player).getOptionInteger("rank", null, getConfig().getInt("defaultRank", 100));
		for (int i = 0; i < permissionBasedRanks; i++) {
			if (player.hasPermission("Reservations.rank." + (i + 1)))
				return i + 1;
		}
		return getConfig().getInt("Ranks." + player, getConfig().getInt("defaultRank", 100));
	}

	/**
	 * Check if a CommandSender has a permission
	 * 
	 * @param sender The CommandSender that should be examined
	 * @param perm The permission node
	 * @return Does the CommandSender has the permission?
	 */
	private boolean has(CommandSender sender, String perm) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			return player.hasPermission(perm);
		} else
			return true;
	}

	/**
	 * Get the message that should be broadcasted when a player is kicked
	 * 
	 * @return A message as String without color formatting
	 */
	public String getBroadcastMsg() {
		return broadcastMsg;
	}

	/**
	 * Get the message the player should receive when he can't join
	 * 
	 * @return A message as String without color formatting
	 */
	public String getSorryMsg() {
		return sorryMsg;
	}

	/**
	 * Get the message the player should receive when he is kicked
	 * 
	 * @return A message as String without color formatting
	 */
	public String getKickMsg() {
		return kickMsg;
	}

	/**
	 * Check if the login from another computer should be prevented
	 * 
	 * @return Should it be prevented?
	 */
	public boolean getPreventKickFromAnotherLocationLogin() {
		return preventKickFromAnotherLocationLogin;
	}

	/**
	 * Get the message that should the player receive that tries to login from
	 * another computer
	 * 
	 * @return A message as String without color formatting
	 */
	public String getLoginFromAnotherLocationMessage() {
		return loginFromAnotherLocationMessage;
	}
}