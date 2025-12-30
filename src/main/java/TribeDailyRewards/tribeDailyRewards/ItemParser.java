package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemParser {
    private static Plugin plugin = null;
    private static File configFile;
    private static YamlConfiguration yamlData;
    private static ConfigurationSection mainSection = null;
    public static HashMap<Integer, ArrayList<LoadedItem>> loadedItems = new HashMap<>();
    public static HashMap<Integer, Integer> itemHelper = new HashMap<>(); // Dzień Ilośc

    public static void Clear(){
        configFile = null;
        mainSection = null;
        loadedItems.clear ();
        itemHelper.clear ();
    }

    static void Init(Plugin plugin){
        ItemParser.plugin = plugin;
        configFile = new File(plugin.getDataFolder(),"itemConfig.yml");
        if(!configFile.exists()){
            yamlData = new YamlConfiguration();

            mainSection  = yamlData.createSection("Items");
            List<String> comments = new ArrayList<String> ();
            comments.add ("-------------TribeDailyRewards-------------");
            comments.add ("Day: Represents at which day player will get reward can be any number");
            comments.add ("If there is a break in days like 1,2,3,4,8,9...");
            comments.add ("All empty days will be treated as -1");
            comments.add ("-1 Is a special day which is called when no day is fulfilled perfect for unlimited rewards");
            comments.add ("");
            comments.add ("Name");
            comments.add ("Represents material which item uses so in reality what item you will get");
            comments.add ("Amount");
            comments.add ("Represent how many items you will get base is 1");
            comments.add ("Custom Name");
            comments.add ("Represents if item has custom name supports color coding like &6, &9, &2&l");
            comments.add ("Enchants");
            comments.add ("Allows for defining a list of enchantments supports overriding vanilla values");
            comments.add ("Money");
            comments.add ("Accepts number how much money player will gain base is 0");
            comments.add ("Scaling");
            comments.add ("Special attribute decides if present if money or amount is multiplied by the amount of days");
            comments.add ("player is above the reward day");
            comments.add ("Custom Massage");
            comments.add ("Overrides base massage which pops up when player gets the reward also supports color codes");
            comments.add ("-------------------------------------------");
            comments.add ("Example item just copy what you need from this one");
            comments.add("SomeItem:");
            comments.add("  Day: 1");
            comments.add("  ScalingStart: 10");
            comments.add("  Money: 200");
            comments.add("  Name: DIAMOND");
            comments.add("  Amount: 4");
            comments.add("  CustomName: '&2&lReward Diamond'");
            comments.add("  Enchants:");
            comments.add("      sharpness: 5");
            comments.add("      unbreaking: 3");
            comments.add("      efficiency: 8");
            comments.add("  CustomMessage: '&6&lYou just recived your great reward'");
            comments.add ("-------------------------------------------");

            yamlData.setComments ("Items",comments);
            //CreateItem(3,Material.EMERALD,4,"&6Szmaragd uszaty śmieszny",null,0);

            ArrayList<FullEnchant> enchants = new ArrayList<> ();
            enchants.add (new FullEnchant (Enchantment.SHARPNESS,5));
            enchants.add (new FullEnchant (Enchantment.UNBREAKING,3));
            enchants.add (new FullEnchant (Enchantment.EFFICIENCY,8));
            //CreateItemFull (12,Material.DIAMOND,4,"&2&lReward Diamond",enchants,200,10);
            CreateItem(1,Material.IRON_INGOT,4);

            CreateItemFull (1,Material.DIAMOND,1,null,null,0,20,2);
            CreateItemFull (1,Material.IRON_BLOCK,1,null,null,0,20,2);

            CreateItem(2,Material.GOLD_INGOT,4);
            CreateItem(2,Material.REDSTONE,16);

            CreateItem(3,Material.EMERALD,4);
            CreateItem(3,Material.DIAMOND,1);

            CreateMoney(4,100);
            CreateItem(4,Material.OAK_LOG,64);

            CreateItem(5,Material.DIAMOND,2);
            CreateItem(5,Material.IRON_BLOCK,4);

            CreateItem(6,Material.GOLDEN_CARROT,32);
            CreateItem(6,Material.COOKED_BEEF,32);

            CreateItem(7,Material.NETHERITE_SCRAP,1);
            CreateItem(7,Material.BLAZE_ROD,16);

            CreateItem(8,Material.GOLDEN_APPLE,2);
            CreateItem(8,Material.DIAMOND,4);

            CreateMoney(9, 200);
            CreateItem(9,Material.DIAMOND_CHESTPLATE,1);

            CreateItem(10,Material.GOLDEN_CARROT,64);
            CreateItem(10,Material.DIAMOND_BLOCK,1);

            CreateItem(11,Material.OBSIDIAN,64);
            CreateItem(11,Material.NETHERITE_SCRAP,2);

            CreateItem(12,Material.IRON_BLOCK,16);
            CreateItem(12,Material.GOLD_BLOCK,16);

            CreateItem(13,Material.EXPERIENCE_BOTTLE,64);

            CreateItem(14,Material.NETHERITE_INGOT,1);

            CreateItem(15,Material.SHULKER_SHELL,2);
            CreateItem(15,Material.NETHERITE_INGOT,1);

            CreateMoney(16, 400);
            CreateItem(16,Material.HEART_OF_THE_SEA,1);

            CreateItem(17,Material.GHAST_TEAR,16);

            CreateItem(18,Material.NETHERITE_INGOT,2);

            CreateItem(19,Material.TOTEM_OF_UNDYING,3);

            CreateItem(20,Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,1);

            CreateMoneyScalling (-1,100,20);
            CreateItemFull (-1,Material.DIAMOND,1,null,null,0,20,0);
            CreateItemFull (-1,Material.IRON_BLOCK,1,null,null,0,20,0);

            try {
                yamlData.save(configFile);
                Bukkit.getLogger().info("[ItemParser] Created default itemConfig.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            ParseConfig (mainSection);
        }
        else{
            Bukkit.getLogger().info("Parsing item config");
            yamlData = YamlConfiguration.loadConfiguration(configFile);
            mainSection = yamlData.getConfigurationSection ("Items");
            if(mainSection == null){
                throw new RuntimeException ("Main item config section is null aborting parsing if you don't "
                                            + "know what to do regenerate config");
            }
            ParseConfig (mainSection);
        }
    }

    static private void CreateItem(int day, Material material, int amount){
        CreateItem (day,material,amount,null,null,0);
    }

    static private void CreateMoney(int day, int money){
        CreateItemFull (day,null,0,null,null, money,-1,0);
    }
    static private void CreateMoneyScalling(int day, int money,int scaling){
        CreateItemFull (day,null,0,null,null, money,scaling,0);
    }
    static private void CreateItemAndMoney(int day, Material material, int amount,
                                           String customName, ArrayList<FullEnchant> enchants, int money){
        CreateItem (day,null,0,null,null, money);
    }
    static private void CreateItem(int day, Material material, int amount,
                                       String customName, ArrayList<FullEnchant> enchants, int money){
        CreateItemFull (day,material,amount,customName,enchants,money,-1,0);
    }

    static private void CreateItemFull(int day, Material material, int amount,
                                       String customName, ArrayList<FullEnchant> enchants,
                                       int money, int scaling, int joinID){
        Integer count = itemHelper.get (day);
        if(count == null){ // If this day was not defined previously
            itemHelper.put (day,1);
            count =1;
        }
        else {
            itemHelper.put (day,count +1); // if it was defined
            count +=1;
        }

        String name = "Day" + day + "Item" + count;
        ConfigurationSection sec = mainSection.createSection(name);
        if(joinID != 0){
            sec.set ("JoinID",joinID);
        }
        sec.set("Day", day);
        if(scaling != -1){
            sec.set ("ScalingStart",scaling);
        }
        if(money > 0){
            sec.set ("Money",money);
        }
        if(material != null){
            sec.set("Name", material.toString());
            sec.set ("Amount", amount);
            if(customName != null){
                sec.set ("CustomName", customName);
            }
            if(enchants == null){return;}

            ConfigurationSection enchantSection = sec.createSection ("Enchants");
            for(FullEnchant ench : enchants){
                NamespacedKey key = ench.enchant.getKey();
                String keyString = key.toString ().substring ("minecraft:".length ());
                enchantSection.set(keyString, ench.level);
            }
        }
    }

    static private void ParseConfig(ConfigurationSection mainSection){
        for (String selectionItem : mainSection.getKeys (false)){
            ConfigurationSection confItem = mainSection.getConfigurationSection(selectionItem);
            if(confItem == null){
                Bukkit.getLogger ().info ("Wrong configuration section is null in key "+ selectionItem);
                continue;
            }
            String materialStr = confItem.getString("Name", "");
            Material material = Material.getMaterial (materialStr); // May be null it is expected bahaviour

            int day = confItem.getInt("Day", 1);
            int joinID = confItem.getInt ("JoinID",0);
            int amount = confItem.getInt("Amount", 1);
            int scale = confItem.getInt ("ScalingStart",-1);
            if(!loadedItems.containsKey (day)){
                loadedItems.put (day, new ArrayList<> ());
            }
            String customName = confItem.getString ("CustomName", null);

            ConfigurationSection enchSection = confItem.getConfigurationSection ("Enchants");
            ArrayList<FullEnchant> enchants = new ArrayList<> ();
            if(enchSection != null){
                for(String enchItem : enchSection.getKeys (false)){
                    int level = enchSection.getInt (enchItem,1);
                    NamespacedKey key = NamespacedKey.minecraft(enchItem);
                    Enchantment enchantment = Registry.ENCHANTMENT.get(key);

                    if(enchantment == null){continue;}
                    enchants.add (new FullEnchant (enchantment ,level)); ;
                }
            }
            int money = confItem.getInt("Money", 0);
            loadedItems.get(day).add (new LoadedItem (material,customName ,amount,enchants,money,scale,joinID));
            LoadedItem item = loadedItems.get (day).getLast ();
            item.cutomMassage = confItem.getString ("CustomMessage",null);

        }
    }
}
