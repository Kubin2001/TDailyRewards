package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
    public static Sound positiveSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    public static Sound negativeSound = Sound.ENTITY_HORSE_HURT;
    public static int timeOutHours = 25;
    public static int resetType = 1;
    public static int resetDaysRemove = 1;
    public static int rewardHoursTime = 24;
    public static int purgeData = 0;
    public static int purgeDays = 30;

    private static Sound LoadSound(String name, Plugin p){
        String strUpper = name.toUpperCase();
        try{
            return Sound.valueOf(strUpper);
        }
        catch (Exception e){
            p.getLogger().info("Cannot load sound from config is it missing? Or maybe format is wrong");
        }
        return null;
    }

    public static void Load(Plugin plugin){
        File file = plugin.getDataFolder();
        File configFile = new File(file, "config.yml");
        YamlConfiguration yamlConf = null;
        if (!configFile.exists()) {
            Bukkit.getLogger().info("[DailyReward] generating mainConfig");
            plugin.saveResource("config.yml", false);
        }

        Bukkit.getLogger().info("[DailyReward] loading mainConfig");
        yamlConf = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection confSec = yamlConf.getConfigurationSection("Configuration");
        if(confSec == null){
            plugin.getLogger().info("Main config does not have its configuration section it cannot be loaded");
            return;
        }
        langName = confSec.getString("UsedLanguage", "en");
        rewardType = confSec.getInt("RewardType", 1);

        positiveSound = LoadSound(confSec.getString("PositiveSound","---"),plugin);
        if(positiveSound == null){
            positiveSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
        negativeSound = LoadSound(confSec.getString("NegativeSound","---"),plugin);
        if(negativeSound== null){
            negativeSound = Sound.ENTITY_HORSE_HURT;
        }
        timeOutHours = confSec.getInt("TimeOutHours",25);
        if(timeOutHours > 1_000_000){
            timeOutHours = 1_000_000;
        }
        if(timeOutHours < 1){
            timeOutHours = 1;
        }
        resetType = confSec.getInt("ResetType",1);

        resetDaysRemove = confSec.getInt("ResetDaysRemove",1);
        if(resetDaysRemove < 1){
            resetDaysRemove = 1;
        }
        if(resetDaysRemove > 1000){
            resetDaysRemove = 1000;
        }

        rewardHoursTime = confSec.getInt ("RewardHoursTime",24);
        if(rewardHoursTime < 0){
            rewardHoursTime = 0;
        }
        if(rewardHoursTime > 1_000_000){
            rewardHoursTime = 1_000_000;
        }

        purgeData = confSec.getInt("PurgeData",0);

        purgeDays = confSec.getInt("PurgeDays",30);


    }
}
