package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.nio.file.StandardCopyOption;

public class ItemParser {
    private static File configFile;
    public static HashMap<Integer, ArrayList<LoadedItem>> loadedItems = new HashMap<>();
    public static HashMap<Integer, Integer> itemHelper = new HashMap<>(); // Dzień Ilośc

    public static void Clear() {
        configFile = null;
        loadedItems.clear();
        itemHelper.clear();
    }

    static void Init(Plugin plugin) {
        configFile = new File(plugin.getDataFolder(), "itemConfig.yml");
        if(!configFile.exists()){
            String fileName = "itemConfig.yml";

            try(InputStream stream = plugin.getResource(fileName)){
                if(stream == null) {
                    plugin.getLogger().info("Error when streaming config please report this on plugin discord");
                    return;
                }
                Files.copy(stream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e){
                plugin.getLogger().info("Error when loading config please report this on plugin discord");
                return;
            }
        }
        YamlConfiguration yamlConf = YamlConfiguration.loadConfiguration(configFile);
        ParseConfig(yamlConf);
    }

    static private String ParseItem(String itemStr){
        if(itemStr.isEmpty()){
            return  "";
        }
        return itemStr.toUpperCase(Locale.ROOT).replace(" ", "_");
    }

    private static Enchantment ParseEnchant(String name) {
        String key = name.toLowerCase(Locale.ROOT).replace(" ", "_").replace(".", "_");
        return Enchantment.getByKey(NamespacedKey.minecraft(key));
    }

    static private void ParseConfig(ConfigurationSection mainSection) {
        for (String selectionItem : mainSection.getKeys(false)) {
            ConfigurationSection confItem = mainSection.getConfigurationSection(selectionItem);
            if (confItem == null) {
                Bukkit.getLogger().info("Wrong configuration section is null in key " + selectionItem);
                continue;
            }
            String materialStr = ParseItem(confItem.getString("Item", ""));
            Material material = Material.getMaterial(materialStr);
            if(!materialStr.isEmpty() && material == null){ // If it "" it is fine
                material = Material.DIRT;
                Bukkit.getLogger().info("Unexisting material in item: " +confItem.toString() +
                        "wrong name: " + materialStr + " this reward will give dirt until fixed");
            }


            int day = confItem.getInt("Day", -1);
            int joinID = confItem.getInt("JoinID", 0);
            int amount = confItem.getInt("Amount", 1);
            String command = confItem.getString("Command",null);
            int scale = confItem.getInt("ScalingStart", -1);
            if (!loadedItems.containsKey(day)) {
                loadedItems.put(day, new ArrayList<>());
            }
            String customName = confItem.getString("CustomName", null);
            String lore = confItem.getString ("Lore",null);

            ConfigurationSection enchSection = confItem.getConfigurationSection("Enchants");
            ArrayList<FullEnchant> enchants = new ArrayList<>();
            if (enchSection != null) {
                for (String enchItem : enchSection.getKeys(false)) {
                    int level = enchSection.getInt(enchItem, 1);
                    Enchantment enchantment = ParseEnchant (enchItem);
                    if (enchantment == null) {
                        Bukkit.getLogger ().info ("Broken enchant when loading items key: "+ enchItem);
                        continue;
                    }
                    enchants.add(new FullEnchant(enchantment, level));
                }
            }
            int money = confItem.getInt("Money", 0);
            loadedItems.get(day).add(new LoadedItem(material, customName, lore, amount, enchants, money, scale, joinID,command));
            LoadedItem item = loadedItems.get(day).getLast();
            item.cutomMassage = confItem.getString("CustomMessage", null);
        }
    }
}
