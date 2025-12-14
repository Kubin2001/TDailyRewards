package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class CommandRestart implements CommandExecutor {
    private Plugin pl = null;

    public CommandRestart (Plugin plugin){
	this.pl = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
	if (!pl.getDataFolder().exists()) {
	    pl.getDataFolder().mkdirs();
	}
	try {
	    MainConfig.Load (pl);
	    Lang.LoadLang(pl,MainConfig.langName + ".yml");
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
	if(sender instanceof Player p){
	    Helpers.SendFormated (p,Lang.GetTrans ("Restarted"));
	}
	else{
	    pl.getLogger ().info (Lang.GetTrans ("Restarted"));
	}
	return  true;
    }
}
