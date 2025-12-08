package TribeDailyRewards.tribeDailyRewards;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.util.*;

public class Helpers {
    private static Random rand = null;
    private static Map<String, Integer> data = null;
    private static Map<String, LocalDateTime> dates = null;
    private static Economy eco = null;

    public static void Init(Map<String, Integer> dataMap,  Map<String, LocalDateTime> datesMap, Economy ecoP){
	data = dataMap;
	dates = datesMap;
	rand = new Random ();
	eco = ecoP;
    }

    public static Economy getEco(){return  eco;}

    public static String CFormat(String s){
	return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void SendFormated(Player p, String s){
	p.sendMessage(Helpers.CFormat(s));
    }

    public static int GetRandom(int min, int max){
	return rand.nextInt((max - min) + 1) + min;
    }

    public static boolean SetPlayerRewardLevel(String uuid, int value){
	data.put ("RLevel" + uuid,value);
	return true;
    }

    public static int GetPlayerRewardLevel(String uuid){
	if(data.containsKey ("RLevel" + uuid)){
	    return data.get ("RLevel" + uuid);
	}
	Bukkit.getLogger().info (Lang.GetTrans ("MissingRewardLevel"));
	return  -1;
    }

    public static boolean IsPlayerRewardLevel(String uuid){
	return data.containsKey ("RLevel" + uuid);
    }

    public static boolean SetPlayerRewardTimer(String uuid, LocalDateTime value){
	dates.put ("RTime" + uuid,value);
	return true;
    }

    public static LocalDateTime GetPlayerRewardTimer(String uuid){
	if(dates.containsKey ("RTime" + uuid)){
	    return dates.get ("RTime" + uuid);
	}
	Bukkit.getLogger().info (Lang.GetTrans ("MissingRewardTime"));
	return  LocalDateTime.now ();
    }

    public static boolean IsPlayerRewardTimer(String uuid){
	return dates.containsKey ("RTime" + uuid);
    }

    public static void PlaySoundToPLayer(Player p, Sound sound){
	p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
    }

    public static void RunTask(Plugin plugin, Runnable task, int delay){
	Bukkit.getScheduler().runTaskLater(plugin, task ,delay);
    }

    public static void PlayErrorSound(Player p){
	p.playSound(p.getLocation(), Sound.ENTITY_HORSE_HURT, 1.0f, 1.0f);
    }

    public static void RepairAll(Player player) {
	for (ItemStack item : player.getInventory().getContents()) {
	    if (item == null) continue;

	    ItemMeta meta = item.getItemMeta();
	    if (meta instanceof Damageable dmg) {
		dmg.setDamage(0);
		item.setItemMeta((ItemMeta) dmg);
	    }
	}
    }

    public static ItemStack CreateItem(Material mat, String name, String desc){
	ItemStack retItem = new ItemStack(mat);

	ItemMeta meta = retItem.getItemMeta();
	meta.setDisplayName(Helpers.CFormat(name));
	List<String> lore = new ArrayList<>();
	lore.add(Helpers.CFormat(desc));
	meta.setLore(lore);
	retItem.setItemMeta(meta);
	return retItem;
    }

    public static String GetItemName(Material material){
	return material.name ().toLowerCase ().replace ("_", " ");
    }
}
