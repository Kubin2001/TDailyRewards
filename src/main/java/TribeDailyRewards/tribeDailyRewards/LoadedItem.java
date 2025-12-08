package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class LoadedItem{
    public Material material;
    public String customName = null;
    public int amount = 1;
    public ArrayList<FullEnchant> enchants = null;
    public int money = 0;
    public int scalingStart = -1;
    public String cutomMassage = null;

    public LoadedItem (Material material, String customName, int amount, ArrayList<FullEnchant> enchants, int money,
    int scalingStart){
        this.material = material;
        this.customName = customName;
        this.amount = amount;
        this.enchants = enchants;
        this.money = money;
        this.scalingStart = scalingStart;
    }

    @Override
    public  String toString(){
        String itemStr = "Material: " + material.toString () + "\n";
        itemStr += "Amount: " + amount + "\n";
        if(customName != null){
            itemStr += "Name: " + customName + "\n";
        }
        itemStr +="Enchants:\n";
        if(enchants == null){return itemStr;}
        for(FullEnchant ench : enchants){
            itemStr += "   " + ench.enchant.toString () +"  LVL:  " + ench.level + "\n";
        }
        itemStr += "\n";
        return itemStr;
    }

    public  ItemStack ToItem(int rewardDay){
        if(material == null){
            return  null;
        }
        int scalledAmount = amount;
        if(scalingStart != -1){
            scalledAmount *= rewardDay - scalingStart;
            if(scalledAmount < 1 || scalledAmount > 128){
                scalledAmount = amount;
            }
        }
        ItemStack item = new ItemStack (material, scalledAmount);
        ItemMeta meta = item.getItemMeta ();
        if(customName != null && !customName.isEmpty ()){
            meta.setDisplayName (Helpers.CFormat (customName));
        }
        if(enchants != null){
            for(FullEnchant ench : enchants){
                meta.addEnchant (ench.enchant,ench.level,true);
            }
        }
        item.setItemMeta (meta);
        return  item;
    }

    public  int ToMoney(int rewardDay){
        if(scalingStart != -1){
            int scaledAmount = rewardDay - scalingStart;
            int scaledMoney = money * scaledAmount;
            return  scaledMoney;
        }
        return  money;
    }
}
