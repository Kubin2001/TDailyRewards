package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Reward implements CommandExecutor {

    private Plugin plugin = null;

    public  Reward(Plugin plugin){
        this.plugin = plugin;
    }

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
            Helpers.SendFormated(p, Lang.GetTrans("WaitTime") + days + Lang.GetTrans("Day")
                                    + hours + Lang.GetTrans("Hour")+ minutes + Lang.GetTrans("Minute"));
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
        Helpers.SendFormated(p, Lang.GetTrans("RewardGetInfo") + rewardDays);
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
        Helpers.SendFormated(p, Lang.GetTrans("RewardGetInfo") + rewardDays);
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

    public void ParseTypeShuffle(ArrayList<LoadedItem> possibleItems, Player p, int rewardDays) {
        Helpers.SendFormated(p, Lang.GetTrans("RewardGetInfo") + rewardDays);
        int slot = 13;
        int money = 0;
        Inventory rewardGui = Bukkit.createInventory(p, 27, Helpers.CFormat(Lang.GetTrans("ShuffleGUI")));
        ArrayList<ItemStack> items = new ArrayList<> ();


        for (LoadedItem lItem : possibleItems) {
            ItemStack item = lItem.ToItem (rewardDays);

            if (item != null) {
                items.add (item);
            }
            money += lItem.ToMoney(rewardDays);
        }
        if (money != 0) {
            Helpers.getEco().depositPlayer(p, money);
            Helpers.SendFormated(p, Lang.GetTrans("RewardMoneyInfo") + money);
        }
        ItemStack finalRouleteItem = items.get (Helpers.GetRandom (0, items.size ()-1));
        ItemStack firstItem = items.get (Helpers.GetRandom (0, items.size ()-1));
        rewardGui.setItem (slot, firstItem);
        p.openInventory(rewardGui);

        new BukkitRunnable (){
            int counter = 0;
            final int iter = 10;

            @Override
            public void run(){
                if (p.getOpenInventory().getTopInventory() == null ||
                    !p.getOpenInventory().getTitle().equals(Helpers.CFormat(Lang.GetTrans("ShuffleGUI")))) {
                    this.cancel();
                    return;
                }
                counter++;
                if(counter < iter){
                    rewardGui.setItem (slot, items.get (Helpers.GetRandom (0, items.size ()-1)));
                    Helpers.PlaySoundToPLayer (p,Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                }
                else{
                    rewardGui.clear ();
                    p.closeInventory ();
                    Inventory rewardGui2 = Bukkit.createInventory(p, 27,
                                                                 Helpers.CFormat(Lang.GetTrans("RewardGUI")));
                    rewardGui2.setItem (slot, finalRouleteItem);
                    p.openInventory (rewardGui2);
                    Helpers.PlaySoundToPLayer (p,Sound.ENTITY_PLAYER_LEVELUP);
                    this.cancel();
                }
            }

        }.runTaskTimer (plugin,0 , 10);  // Time is in ticks 1 sec = 20 ticks
    }

    public void ParseTypeSlide(ArrayList<LoadedItem> possibleItems, Player p, int rewardDays) {
        Helpers.SendFormated(p, Lang.GetTrans("RewardGetInfo") + rewardDays);
        int slot = 13;
        int money = 0;
        String invName = Helpers.CFormat(Lang.GetTrans("SlideGUI"));
        Inventory rewardGui = Bukkit.createInventory(p, 27, invName);
        ArrayList<ItemStack> items = new ArrayList<> ();


        for (LoadedItem lItem : possibleItems) {
            ItemStack item = lItem.ToItem (rewardDays);

            if (item != null) {
                items.add (item);
            }
            money += lItem.ToMoney(rewardDays);
        }
        if (money != 0) {
            Helpers.getEco().depositPlayer(p, money);
            Helpers.SendFormated(p, Lang.GetTrans("RewardMoneyInfo") + money);
        }
        int lastIndex = items.size()-1;
        // Filling central row
        for(int i = 9; i <= 17; i++){
            rewardGui.setItem (i, items.get(Helpers.GetRandom (0, lastIndex)));
        }
        p.openInventory(rewardGui);

        new BukkitRunnable (){
            int counter = 0;
            final int iter = 10;

            @Override
            public void run(){
                if (p.getOpenInventory().getTopInventory() == null || !p.getOpenInventory().getTitle().equals(invName)) {
                    this.cancel();
                    return;
                }
                counter++;
                if(counter < iter){
                    for(int i = 9; i <= 16; i++){
                        rewardGui.setItem(i,rewardGui.getItem(i+1));
                    }
                    rewardGui.setItem (17, items.get(Helpers.GetRandom (0, lastIndex)));
                    Helpers.PlaySoundToPLayer (p,Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                }
                else{
                    ItemStack finalItem = rewardGui.getItem(slot);
                    rewardGui.clear ();
                    p.closeInventory ();
                    Inventory rewardGui2 = Bukkit.createInventory(p, 27,
                            Helpers.CFormat(Lang.GetTrans("RewardGUI")));
                    rewardGui2.setItem (slot, finalItem);
                    p.openInventory (rewardGui2);
                    Helpers.PlaySoundToPLayer (p,Sound.ENTITY_PLAYER_LEVELUP);
                    this.cancel();
                }
            }

        }.runTaskTimer (plugin,0 , 10);  // Time is in ticks 1 sec = 20 ticks
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
        switch (MainConfig.rewardType){
            case 1:
                ParseTypeInstant(allLItems, p, rewardDays);
                break;
            case 2:
                ParseTypeUI(allLItems, p, rewardDays);
                break;
            case 3:
                ParseTypeShuffle(allLItems, p ,rewardDays);
                break;
            case 4:
                ParseTypeSlide(allLItems, p ,rewardDays);
                break;
        }

        Helpers.PlayerPositiveSound(p);
        Helpers.SetPlayerRewardTimer(uuid, LocalDateTime.now().plusHours (MainConfig.rewardHoursTime));
        Helpers.SetPlayerRewardLevel(uuid, rewardDays + 1);
    }
}
