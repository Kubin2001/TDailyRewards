package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.util.*;

public class Helpers {
    private static Random rand = null;

    public static VaultHook ecoHook = null;

    public static boolean hasEconomy = false;

    public static Map<UUID, Integer> data = null;
    public static Map<UUID, LocalDateTime> dates = null;

    public static void Init(Map<UUID, Integer> dataMap, Map<UUID, LocalDateTime> datesMap) {
        data = dataMap;
        dates = datesMap;
        rand = new Random();
    }

    public static void SafeDeposit(int value, Player p){
        if (!Helpers.hasEconomy){
            Bukkit.getLogger().info("[T Daily Rewards] Warning tried to use money in reward without vault or money" +
                    " system provider");
            return;
        }
        Helpers.ecoHook.getEconomy().depositPlayer(p, value);
    }


    public static String CFormat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void SendFormated(Player p, String s) {
        p.sendMessage(Helpers.CFormat(s));
    }

    public static int GetRandom(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public static boolean SetPlayerRewardLevel(UUID uuid, int value) {
        data.put(uuid, value);
        return true;
    }

    public static int GetPlayerRewardLevel(UUID uuid) {
        if (data.containsKey(uuid)) {
            return data.get(uuid);
        }
        Bukkit.getLogger().info(Lang.GetTrans("MissingRewardLevel"));
        return -1;
    }

    public static boolean IsPlayerRewardLevel(UUID uuid) {
        return data.containsKey(uuid);
    }

    public static boolean SetPlayerRewardTimer(UUID uuid, LocalDateTime value) {
        dates.put(uuid, value);
        return true;
    }

    public static LocalDateTime GetPlayerRewardTimer(UUID uuid) {
        if (dates.containsKey(uuid)) {
            return dates.get(uuid);
        }
        Bukkit.getLogger().info(Lang.GetTrans("MissingRewardTime"));
        return LocalDateTime.now();
    }

    public static boolean IsPlayerRewardTimer(UUID uuid) {
        return dates.containsKey(uuid);
    }



    public static void PlaySoundToPLayer(Player p, Sound sound) {
        p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
    }

    public static void PlayerPositiveSound(Player p) {
        if(!MainConfig.positiveSoundEnable){return;}
        p.playSound(p.getLocation(), MainConfig.positiveSound, 1.0f, 1.0f);
    }
    public static void PlayErrorSound(Player p) {
        if(!MainConfig.negativeSoundEnable){return;}
        p.playSound(p.getLocation(), MainConfig.negativeSound, 1.0f, 1.0f);
    }



    public static ItemStack CreateItem(Material mat, String name, String desc) {
        ItemStack retItem = new ItemStack(mat);

        ItemMeta meta = retItem.getItemMeta();
        meta.setDisplayName(Helpers.CFormat(name));
        List<String> lore = new ArrayList<>();
        lore.add(Helpers.CFormat(desc));
        meta.setLore(lore);
        retItem.setItemMeta(meta);
        return retItem;
    }

    public static String GetItemName(Material material) {
        return material.name().toLowerCase().replace("_", " ");
    }
}
