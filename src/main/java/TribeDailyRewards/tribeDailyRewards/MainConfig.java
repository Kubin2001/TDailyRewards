package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainConfig {

    public static String langName = "";
    public static int rewardType = 1; // 1 normalne odrazu się dostaje 2 dla itemu wyskakuje menu

    public static void Load(Plugin plugin) throws IOException {
        File file = plugin.getDataFolder();
        File configFile = new File(file, "config.yml");
        YamlConfiguration yamlConf = null;
        if (!configFile.exists()) {
            Bukkit.getLogger().info("[DailyReward] creating mainConfig");

            yamlConf = YamlConfiguration.loadConfiguration(configFile);
            ConfigurationSection langSec = yamlConf.createSection("Configuration");

            langSec.set("UsedLanguage", "en");

            langSec.set("RewardType", 1);

            List<String> comments = new ArrayList<String>();
            comments.add("UsedLanguage load language file from Lang folder on default only En and Pl are created but you can add your own file");
            comments.add("For example if tou want to support Spanish just copy En change messages to spanish " +
                    "(No not modify message names)");
            comments.add("Then just change file name to ES");
            comments.add("");

            comments.add("Reward Type");
            comments.add("1: Reward will be given to player with instantly");
            comments.add("2: GUI will open with all the items money will still be given instantly");
            yamlConf.setComments("Configuration", comments);

            yamlConf.save(configFile);
            langName = langSec.getString("UsedLanguage", "en");
            rewardType = langSec.getInt("RewardType", 1);
        } else {
            Bukkit.getLogger().info("[DailyReward] loading mainConfig");
            yamlConf = YamlConfiguration.loadConfiguration(configFile);
            ConfigurationSection langSec = yamlConf.getConfigurationSection("Configuration");
            langName = langSec.getString("UsedLanguage", "en");
            rewardType = langSec.getInt("RewardType", 1);
        }
    }
}
