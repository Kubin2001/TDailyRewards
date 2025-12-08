package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Lang {
    private static final Map<String,String> langMap = new HashMap<>();

    public static void LoadLang(Plugin plugin, String fileName) throws IOException {

        File mainFolder = plugin.getDataFolder();
        File langFolder = new File(mainFolder,"Lang");
        if(!(langFolder.exists() && langFolder.isDirectory())){
            Bukkit.getLogger().info("No locale exist creating default english version");
            langFolder.mkdirs();
            CreateDefaultLocale(langFolder);
            return;
        }
        File langFile = new File(langFolder,fileName);
        if(!langFile.exists()){
            Bukkit.getLogger().info("Cannot load selected locale locale name : " + fileName);
            return;
        }
        YamlConfiguration langYaml = YamlConfiguration.loadConfiguration(langFile);
        ConfigurationSection mainSection = langYaml.getConfigurationSection("Localization");
        LoadLocalizationFile(mainSection);
    }

    private static void CreateEnglishLocale(File langFolder) throws IOException {
        File engLocale = new File(langFolder,"en.yml");
        YamlConfiguration yamlLocale = new YamlConfiguration();
        ConfigurationSection mainSec = yamlLocale.createSection("Localization");
        mainSec.set("WrongArgs2","&4Incorrect Arguments Expected [Username] [Value]");
        mainSec.set("TextToNum","&4ERROR you need to provide number");
        mainSec.set("NotOnline","&4Player is not on the server");
        mainSec.set("SetTo","Value set to: ");
        mainSec.set("WrongArgs1","&4Incorrect Arguments One Expected Username: ");
        mainSec.set("ResetFor","Restarted!");
        mainSec.set("LevelInfo","Reward Day: ");
        mainSec.set("TimeInfo","Time to reward: ");
        mainSec.set("MissingRewardLevel","ERROR  GetPlayerRewardLevel player somehow does not have reward level");
        mainSec.set("MissingRewardTime","ERROR GetPlayerRewardTimer player somehow does not have reward time");
        mainSec.set("PlayerOnly","You cannot call this command from console it is Player only");
        mainSec.set("MissingTimerP","&4Plugin error you do not have reward timer contact server administartor");
        mainSec.set("WaitTime","&4You need to wait: ");
        mainSec.set("EmptyReward","&4There is no reward for this day");
        mainSec.set("RewardGetInfo","&bYou received reward for day:  &4&l");
        mainSec.set("RewardItemInfo","&2&l");
        mainSec.set("RewardMoneyInfo","&bMoney: ");
        mainSec.set("RewardReady","&6&lYou can get daily reward type /reward");
        yamlLocale.save(engLocale);
    }

    private static void CreatePolishLocale(File langFolder) throws IOException {
        File engLocale = new File(langFolder,"pl.yml");
        YamlConfiguration yamlLocale = new YamlConfiguration();
        ConfigurationSection mainSec = yamlLocale.createSection("Localization");
        mainSec.set("WrongArgs2","&4Niepoprawne argumenty komenda wymaga: [Nick Gracza] [Wartość]");
        mainSec.set("TextToNum","&4Bład musisz podac numer");
        mainSec.set("NotOnline","&4Gracz nie jest na serwerze");
        mainSec.set("SetTo","Wartość ustawiona na: ");
        mainSec.set("WrongArgs1","&4Niepoprawna liczba argumentów oczekiwano [nazwa gracza]: ");
        mainSec.set("ResetFor","Zrestartowano!");
        mainSec.set("LevelInfo","Dzień nagrody: ");
        mainSec.set("TimeInfo","Czas do nagrody: ");
        mainSec.set("MissingRewardLevel","Bład  GetPlayerRewardLevel gracz nie ma poziomu nagrody");
        mainSec.set("MissingRewardTime","ERROR GetPlayerRewardTimer gracz nie ma czasu do następnej nagrody");
        mainSec.set("PlayerOnly","Ta komendą może zostać odpalona wyłącznie z poziomu gracza");
        mainSec.set("MissingTimerP","&4Bład pluginu nie znaleziono czasu nagrody gracza skontaktuj się z administracją");
        mainSec.set("WaitTime","&4Musisz poczekać: ");
        mainSec.set("EmptyReward","&4Za ten dzień nie ma nagrody");
        mainSec.set("RewardItemInfo","&2&l");
        mainSec.set("RewardGetInfo","&bOtrzymałeś nagrodę za dzień:  &4&l");
        mainSec.set("RewardMoneyInfo","&bMonety: ");
        mainSec.set("RewardReady","&6&lMożesz odebrać codzienną nagrodę wpisz /reward");
        yamlLocale.save(engLocale);
    }

    private static void CreateDefaultLocale(File langFolder) throws IOException {

        CreateEnglishLocale (langFolder);
        CreatePolishLocale (langFolder);
        //Loading default en.locale
        File langFile = new File(langFolder,"en.yml");
        YamlConfiguration langYaml = YamlConfiguration.loadConfiguration(langFile);
        ConfigurationSection mainSection = langYaml.getConfigurationSection("Localization");
        LoadLocalizationFile(mainSection);
    }
    private static void  LoadLocalizationFile(ConfigurationSection mainSection){
        if(mainSection == null){
            Bukkit.getLogger().info("[Daily Reward] No default section in localization file aborting");
            return;
        }
        for(String secItem : mainSection.getKeys(false)){
            String val = mainSection.getString(secItem);
            if(val == null){
                Bukkit.getLogger().info("[Daily Reward] Wrong configuration in " + secItem);
                continue;
            }
            langMap.put(secItem,val);
        }

        Bukkit.getLogger().info("[Daily Reward] Loaded lang functions");
        for(Map.Entry<String ,String> entry : langMap.entrySet ()){
            Bukkit.getLogger().info(entry.getKey() + "  " + entry.getValue());
        }
    }

    public static String GetTrans(String key){
        String val  = langMap.get(key);
        if(val == null){
            return "Missing translation key: " + key;
        }
        return val;
    }
}
