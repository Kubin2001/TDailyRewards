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
    public static int rewardType = 1;
    public static boolean positiveSoundEnable = true;
    public static boolean negativeSoundEnable = true;
    public static Sound positiveSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    public static Sound negativeSound = Sound.BLOCK_NOTE_BLOCK_BASS;
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
        if (!configFile.exists()) {
            Bukkit.getLogger().info("[DailyReward] generating mainConfig");
            plugin.saveResource("config.yml", false);
        }

        Bukkit.getLogger().info("[DailyReward] loading mainConfig");
        YamlConfiguration yamlConf = YamlConfiguration.loadConfiguration(configFile);
        langName = yamlConf.getString("UsedLanguage", "en");
        rewardType = yamlConf.getInt("RewardType", 1);

        positiveSound = LoadSound(yamlConf.getString("PositiveSound","---"),plugin);
        if(positiveSound == null){
            positiveSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
        positiveSoundEnable = yamlConf.getBoolean("EnablePositiveSounds",true);
        negativeSound = LoadSound(yamlConf.getString("NegativeSound","---"),plugin);
        if(negativeSound== null){
            negativeSound = Sound.BLOCK_NOTE_BLOCK_BASS;
        }
        negativeSoundEnable = yamlConf.getBoolean("EnableNegativeSounds",true);
        timeOutHours = yamlConf.getInt("TimeOutHours",25);
        if(timeOutHours > 1_000_000){
            timeOutHours = 1_000_000;
        }
        if(timeOutHours < 1){
            timeOutHours = 1;
        }
        resetType = yamlConf.getInt("ResetType",1);

        resetDaysRemove = yamlConf.getInt("ResetDaysRemove",1);
        if(resetDaysRemove < 1){
            resetDaysRemove = 1;
        }
        if(resetDaysRemove > 1000){
            resetDaysRemove = 1000;
        }

        rewardHoursTime = yamlConf.getInt ("RewardHoursTime",24);
        if(rewardHoursTime < 0){
            rewardHoursTime = 0;
        }
        if(rewardHoursTime > 1_000_000){
            rewardHoursTime = 1_000_000;
        }

        purgeData = yamlConf.getInt("PurgeData",0);

        purgeDays = yamlConf.getInt("PurgeDays",30);


    }
}
