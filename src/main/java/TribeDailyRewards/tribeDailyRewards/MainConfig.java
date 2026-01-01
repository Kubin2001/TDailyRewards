package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

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
