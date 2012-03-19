package de.HomerBond005.Reservations;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

public class PermissionsChecker{
	int permSys;
	PermissionManager pexmanager;
    PermissionsPlugin pbplugin;
    boolean usePerm;
    Plugin main;
    public PermissionsChecker(Plugin main, boolean usePerm){
    	this.usePerm = usePerm;
    	this.main = main;
    	setupPermissions();
    }
    private void setupPermissions(){
    	if(usePerm){
    		PluginManager pm = main.getServer().getPluginManager();
    		if(pm.getPlugin("PermissionsEx") != null){
    			System.out.println("[" + main.getName() + "]: Using PermissionsEx!");
    			pexmanager = PermissionsEx.getPermissionManager();
    			permSys = 2;
    		}else if(pm.getPlugin("bPermissions") != null){
    			System.out.println("[" + main.getName() + "]: Using bPermissions!");
    			permSys = 3;
    		}else{
    			System.out.println("[" + main.getName() + "]: Using Bukkit Permissions!");
    			permSys = 1;
    		}
    	}else{
    		System.out.println("[" + main.getName() + "]: Using OP-only!");
    		permSys = 0;
    	}
    }
    public boolean has(Player player, String perm){
    	if(permSys == 0){
    		return player.isOp();
    	}else if(permSys == 1){
    		return player.hasPermission(perm);
    	}else if(permSys == 2){
    		return pexmanager.has(player, perm);
    	}else if(permSys == 3){
    		return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), perm);
    	}else{
    		return false;
    	}
    }
}
