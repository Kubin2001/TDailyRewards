package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LoadedItem {
    public Material material;
    public String customName = null;
    public int amount = 1;
    public ArrayList<FullEnchant> enchants = null;
    public int money = 0;
    public int scalingStart = -1;
    public String cutomMassage = null;
    public String lore = null;
    public int joinID;
    public String command = null;

    public LoadedItem(Material material, String customName, String lore, int amount,
                      ArrayList<FullEnchant> enchants, int money, int scalingStart, int joinID, String command) {
        this.material = material;
        this.customName = customName;
        this.lore = lore;
        this.amount = amount;
        this.enchants = enchants;
        this.money = money;
        this.scalingStart = scalingStart;
        this.joinID = joinID;
        this.command = command;
    }

    public List<String> splitLore(String lore, int lineLength) {
        List<String> lines = new ArrayList<>();
        if (lore == null || lore.isEmpty()) return lines;

        int start = 0;
        while (start < lore.length()) {
            int end = Math.min(start + lineLength, lore.length());
            lines.add(lore.substring(start, end));
            start = end;
        }
        return lines;
    }

    @Override
    public String toString() {
        String itemStr = "Material: " + material.toString() + "\n";
        itemStr += "Amount: " + amount + "\n";
        if (customName != null) {
            itemStr += "Name: " + customName + "\n";
        }
        itemStr += "Enchants:\n";
        if (enchants == null) {
            return itemStr;
        }
        for (FullEnchant ench : enchants) {
            itemStr += "   " + ench.enchant.toString() + "  LVL:  " + ench.level + "\n";
        }
        itemStr += "\n";
        return itemStr;
    }

    public ItemStack ToItem(int rewardDay) {
        if (material == null) {
            return null;
        }
        int scalledAmount = amount;
        if (scalingStart != -1) {
            scalledAmount *= rewardDay - scalingStart;
            if (scalledAmount < 1 || scalledAmount > 128) {
                scalledAmount = amount;
            }
        }
        ItemStack item = new ItemStack(material, scalledAmount);
        ItemMeta meta = item.getItemMeta();
        if (customName != null && !customName.isEmpty()) {
            meta.setDisplayName(Helpers.CFormat(customName));
        }
        if(lore != null){
            meta.setLore (splitLore (lore,30));
        }
        if (enchants != null) {
            for (FullEnchant ench : enchants) {
                meta.addEnchant(ench.enchant, ench.level, true);
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemWithCommand ToItemWithCommand(int rewardDay, Player p){
        String com = GetCommand(p);
        return new ItemWithCommand(ToItem(rewardDay),com);
    }

    public int ToMoney(int rewardDay) {
        if (scalingStart != -1) {
            int scaledAmount = rewardDay - scalingStart;
            int scaledMoney = money * scaledAmount;
            return scaledMoney;
        }
        return money;
    }

    public String GetCommand(Player p){
        if(command == null){
            return null;
        }
        return command.replace("%player%", p.getName());
    }
}
