package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Reward implements  CommandExecutor{

    public  boolean onCommand(CommandSender sender, Command command, String label , String[] args){
        if(!(sender instanceof Player p)){
            Bukkit.getLogger ().info (Lang.GetTrans ("PlayerOnly"));
            return true;
        }

        String uuid = p.getUniqueId ().toString ();
        if(!Helpers.IsPlayerRewardTimer (uuid)){
            Helpers.SendFormated (p,Lang.GetTrans ("MissingTimerP"));
            return true;
        }
        LocalDateTime rewardTimer = Helpers.GetPlayerRewardTimer (uuid);

        if(!rewardTimer.isBefore (LocalDateTime.now ())){
            Duration remain = Duration.between (LocalDateTime.now (),rewardTimer);

            long totalMinutes = remain.toMinutes();
            long days = totalMinutes / (24 * 60);
            long hours = (totalMinutes % (24 * 60)) / 60;
            long minutes = totalMinutes % 60;
            Helpers.SendFormated(p, Lang.GetTrans ("WaitTime") + days + "d " + hours + "h " + minutes + "m");
            Helpers.PlayErrorSound (p);
            return true;
        }
        int rewardDays = Helpers.GetPlayerRewardLevel (uuid);

        if(ItemParser.loadedItems.containsKey (rewardDays)){
            ParseReward (p,uuid,rewardDays,false);
        }
        else{
            ParseReward (p,uuid,rewardDays,true);
        }
        return  true;
    }

    public void ParseTypeInstant(ItemStack item, LoadedItem lItem, Player p, int rewardDays){
        if(item != null){
            p.getInventory ().addItem (item);
            if(lItem.cutomMassage != null){
                Helpers.SendFormated (p,lItem.cutomMassage);
            }
            else{
                Helpers.SendFormated (p, Lang.GetTrans ("RewardGetInfo") + rewardDays);
                Helpers.SendFormated (p, Lang.GetTrans ("RewardItemInfo") +
                        lItem.amount + " " + Helpers.GetItemName (lItem.material));
            }
        }

        int money = lItem.ToMoney (rewardDays);
        if(money != 0){
            Helpers.getEco ().depositPlayer (p,money);
            if(lItem.cutomMassage == null){
                Helpers.SendFormated (p,Lang.GetTrans ("RewardMoneyInfo") + money);
            }
        }
    }

    public void ParseTypeUI(ItemStack item, LoadedItem lItem, Player p, int rewardDays){
        if(item != null){
            Inventory rewardGui = Bukkit.createInventory(null,27,
                    Helpers.CFormat(Lang.GetTrans("RewardGUI")));
            rewardGui.setItem(13, item);
            p.openInventory(rewardGui);
        }

        int money = lItem.ToMoney (rewardDays);
        if(money != 0){
            Helpers.getEco ().depositPlayer (p,money);
            Helpers.SendFormated (p,Lang.GetTrans ("RewardMoneyInfo") + money);
        }
    }

    public void ParseReward(Player p, String uuid, int rewardDays, boolean finalScalling){
        ArrayList<LoadedItem> possibleItems = null;
        if(finalScalling){possibleItems = ItemParser.loadedItems.get (-1);}
        else{possibleItems = ItemParser.loadedItems.get (rewardDays);}

        if(possibleItems == null){
            Helpers.SendFormated (p,Lang.GetTrans ("EmptyReward"));
            Helpers.SetPlayerRewardTimer (uuid, LocalDateTime.now ().plusDays (1));
            Helpers.SetPlayerRewardLevel (uuid, rewardDays +1);
            return;
        };
        LoadedItem lItem = possibleItems.get(Helpers.GetRandom (0,possibleItems.size ()-1));

        ItemStack item = lItem.ToItem (rewardDays);
        if(MainConfig.rewardType == 1){
            ParseTypeInstant(item,lItem,p,rewardDays);
        }
        else{
            ParseTypeUI(item,lItem,p,rewardDays);
        }

        Helpers.PlaySoundToPLayer (p,Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        Helpers.SetPlayerRewardTimer (uuid, LocalDateTime.now ().plusDays (1));
        Helpers.SetPlayerRewardLevel (uuid, rewardDays +1);
    }
}
