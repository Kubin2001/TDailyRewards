package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class Reward implements  CommandExecutor{

    public  boolean onCommand(CommandSender sender, Command command, String label , String[] args){
	if(!(sender instanceof Player p)){
	    Bukkit.getLogger ().info (Lang.GetTrans ("PlayerOnly"));
	    return true;
	}

//	for (Map.Entry<Integer, ArrayList<LoadedItem>> entry : ItemParser.loadedItems.entrySet ()) {
//	    for (LoadedItem item : entry.getValue ()) {
//		p.sendMessage ("Material: " + item.material.toString ());
//		p.sendMessage ("Amount: " + item.amount);
//		p.sendMessage ("Day: " + entry.getKey ());
//		if(item.customName != null){
//		    p.sendMessage ("Cutom Name: " + item.customName);
//		}
//		if(item.enchants == null){continue;}
//
//		p.sendMessage ("Enchants");
//		for(FullEnchant ench : item.enchants){
//		    p.sendMessage ("   " + ench.enchant.toString () +"  LVL:  " + ench.level);
//		}
//		p.sendMessage ("   ");
//	    }
//	}
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
	    ParseReward (p,uuid,rewardDays);
	}
	else{
	    ParseReward (p,uuid,-1);
	}
	return  true;
    }

    public void ParseReward(Player p, String uuid, int rewardDays){
	ArrayList<LoadedItem> possibleItems = ItemParser.loadedItems.get (rewardDays);
	if(possibleItems == null){
	    Helpers.SendFormated (p,Lang.GetTrans ("EmptyReward"));
	    Helpers.SetPlayerRewardTimer (uuid, LocalDateTime.now ().plusDays (1));
	    Helpers.SetPlayerRewardLevel (uuid, rewardDays +1);
	    return;
	};
	LoadedItem lItem = null;
	if(ItemParser.loadedItems.size() == 1){
	    lItem = possibleItems.getFirst ();
	}
	else if(ItemParser.loadedItems.size() >1){
	    lItem = possibleItems.get(Helpers.GetRandom (0,possibleItems.size ()-1));
	}
	ItemStack item = lItem.ToItem (rewardDays);
	if(item != null){
	    p.getInventory ().addItem (item);
	}

	Helpers.SendFormated (p, Lang.GetTrans ("RewardGetInfo") + rewardDays);

	int money = lItem.ToMoney (rewardDays);
	if(money != 0){
	    Helpers.getEco ().depositPlayer (p,money);
	    Helpers.SendFormated (p,Lang.GetTrans ("RewardMoneyInfo") + money);
	}

	Helpers.PlaySoundToPLayer (p,Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
	Helpers.SetPlayerRewardTimer (uuid, LocalDateTime.now ().plusDays (1));
	Helpers.SetPlayerRewardLevel (uuid, rewardDays +1);
    }
}
