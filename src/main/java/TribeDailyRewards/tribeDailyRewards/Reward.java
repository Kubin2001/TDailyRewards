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

public class Reward implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            Bukkit.getLogger().info(Lang.GetTrans("PlayerOnly"));
            return true;
        }

        String uuid = p.getUniqueId().toString();
        if (!Helpers.IsPlayerRewardTimer(uuid)) {
            Helpers.SendFormated(p, Lang.GetTrans("MissingTimerP"));
            return true;
        }
        LocalDateTime rewardTimer = Helpers.GetPlayerRewardTimer(uuid);


        if (!rewardTimer.isBefore(LocalDateTime.now())) {
            Duration remain = Duration.between(LocalDateTime.now(), rewardTimer);
            long totalMinutes = remain.toMinutes();
            long days = totalMinutes / (24 * 60);
            long hours = (totalMinutes % (24 * 60)) / 60;
            long minutes = totalMinutes % 60;
            Helpers.SendFormated(p, Lang.GetTrans("WaitTime") + days + "d " + hours + "h " + minutes + "m");
            Helpers.PlayErrorSound(p);
            return true;
        }
        Duration timeAfter = Duration.between(rewardTimer, LocalDateTime.now());
        int rewardDays = Helpers.GetPlayerRewardLevel(uuid);
        if(timeAfter.toHours() > MainConfig.timeOutHours){ // If player claimed last reward more than 25 hours ago reset reward days to 1
            Helpers.SendFormated(p,Lang.GetTrans("RewardTimedOut"));
            if(MainConfig.resetType == 1){
                rewardDays = 1;
            }
            else {
                rewardDays = Math.max(1,rewardDays - MainConfig.resetDaysRemove);
            }

        }

        if (ItemParser.loadedItems.containsKey(rewardDays)) {
            ParseReward(p, uuid, rewardDays, false);
        } else {
            ParseReward(p, uuid, rewardDays, true);
        }
        return true;
    }

    public void ParseTypeInstant(ArrayList<LoadedItem> possibleItems, Player p, int rewardDays) {
        for (LoadedItem lItem : possibleItems) {
            ItemStack item = lItem.ToItem(rewardDays);
            if (item != null) {
                Inventory inv = p.getInventory();
                if (inv.firstEmpty() != -1) { // there is a free slot
                    inv.addItem(item);
                } else { // No empty drop item at player
                    p.getWorld().dropItemNaturally(p.getLocation(), item);
                }

                if (lItem.cutomMassage != null) {
                    Helpers.SendFormated(p, lItem.cutomMassage);
                } else {
                    Helpers.SendFormated(p, Lang.GetTrans("RewardGetInfo") + rewardDays);
                    Helpers.SendFormated(p, Lang.GetTrans("RewardItemInfo") +
                            lItem.amount + " " + Helpers.GetItemName(lItem.material));
                }
            }

            int money = lItem.ToMoney(rewardDays);
            if (money != 0) {
                Helpers.getEco().depositPlayer(p, money);
                if (lItem.cutomMassage == null) {
                    Helpers.SendFormated(p, Lang.GetTrans("RewardMoneyInfo") + money);
                }
            }
        }
    }

    public void ParseTypeUI(ArrayList<LoadedItem> possibleItems, Player p, int rewardDays) {
        int slot = Math.max(0,13 - possibleItems.size()/2) ;
        int money = 0;
        Inventory rewardGui = Bukkit.createInventory(null, 27,
                Helpers.CFormat(Lang.GetTrans("RewardGUI")));
        for (LoadedItem lItem : possibleItems) {
            if (slot > 26) {
                break;
            }
            ItemStack item = lItem.ToItem(rewardDays);

            if (item != null) {
                rewardGui.setItem(slot, item);
            }

            money += lItem.ToMoney(rewardDays);
            slot++;
        }
        if (money != 0) {
            Helpers.getEco().depositPlayer(p, money);
            Helpers.SendFormated(p, Lang.GetTrans("RewardMoneyInfo") + money);
        }
        p.openInventory(rewardGui);
    }

    public void ParseReward(Player p, String uuid, int rewardDays, boolean finalScalling) {
        ArrayList<LoadedItem> possibleItems = null;
        if (finalScalling) {
            possibleItems = ItemParser.loadedItems.get(-1);
        } else {
            possibleItems = ItemParser.loadedItems.get(rewardDays);
        }

        if (possibleItems == null) {
            Helpers.SendFormated(p, Lang.GetTrans("EmptyReward"));
            Helpers.SetPlayerRewardTimer(uuid, LocalDateTime.now().plusDays(1));
            Helpers.SetPlayerRewardLevel(uuid, rewardDays + 1);
            return;
        }

        LoadedItem lItem = possibleItems.get(Helpers.GetRandom(0, possibleItems.size() - 1));
        ArrayList<LoadedItem> allLItems = new ArrayList<>();
        ArrayList<ItemStack> allItems = new ArrayList<>();
        if (lItem.joinID != 0) { // IF it has join id it searches other items with the same id to merge
            for (LoadedItem lItemIter : possibleItems) {
                if (lItemIter.joinID == lItem.joinID) {
                    allLItems.add(lItemIter);
                    allItems.add(lItemIter.ToItem(rewardDays));
                }
            }
        } else { // If not it will just pass one item
            allLItems.add(lItem);
            allItems.add(lItem.ToItem(rewardDays));
        }
        if (MainConfig.rewardType == 1) {
            ParseTypeInstant(allLItems, p, rewardDays);
        } else {
            ParseTypeUI(allLItems, p, rewardDays);
        }

        Helpers.PlayerPositiveSound(p);
        Helpers.SetPlayerRewardTimer(uuid, LocalDateTime.now().plusDays(1));
        Helpers.SetPlayerRewardLevel(uuid, rewardDays + 1);
    }
}
