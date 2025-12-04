package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class MainConfig {

    public static  String langName = "";

    public static void Load(Plugin plugin) throws IOException {
	File file = plugin.getDataFolder ();
	File configFile = new File(file,"config.yml");
	YamlConfiguration yamlConf = null;
	if(!configFile.exists ()){
	    Bukkit.getLogger ().info ("[DailyReward] creating mainConfig" );
	    yamlConf = YamlConfiguration.loadConfiguration (configFile);
	    ConfigurationSection langSec =  yamlConf.createSection ("Language");
	    langSec.set ("UsedLanguage","en");
	    yamlConf.save (configFile);
	    langName = langSec.get ("UsedLanguage").toString ();
	}
	else{
	    Bukkit.getLogger ().info ("[DailyReward] loading mainConfig");
	    yamlConf = YamlConfiguration.loadConfiguration (configFile);
	    ConfigurationSection langSec =  yamlConf.getConfigurationSection ("Language");
	    langName = langSec.get ("UsedLanguage").toString ();
	}
    }
}
