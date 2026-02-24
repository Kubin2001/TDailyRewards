package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.swing.*;
import java.time.LocalDateTime;

public class TEventListener implements Listener {
    private final Plugin plugin;

    TEventListener(Plugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String uuid = p.getUniqueId().toString();
        if (Helpers.IsPlayerRewardLevel(uuid)) {
            LocalDateTime dateTime = Helpers.GetPlayerRewardTimer(uuid);
            if (dateTime.isBefore(LocalDateTime.now())) {
                Runnable task = () -> {
                    if (p.isOnline()) {
                        Helpers.SendFormated(p, Lang.GetTrans("RewardReady"));
                    }
                };
                Helpers.RunTask(plugin, task, 80); //4 sekundy

            }
        } else {
            Helpers.SetPlayerRewardLevel(uuid, 1);
            Helpers.SetPlayerRewardTimer(uuid, LocalDateTime.now());
        }
    }

    public void onLeave(PlayerQuitEvent event){
        Player p = event.getPlayer();
        QueuedItems.items.remove(p.getUniqueId());
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        InventoryView view = event.getView();

        if (view.getTitle().equals(Helpers.CFormat(Lang.GetTrans("RewardGUI")))) {
            Inventory inv = view.getTopInventory();

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);

                if (item == null || item.getType() == Material.AIR){
                    continue;
                }
                p.getWorld().dropItemNaturally(p.getLocation(), item);
            }
            inv.clear();
        }
        else if(view.getTitle().equals(Helpers.CFormat(Lang.GetTrans("ShuffleGUI")))){
            Inventory inv = view.getTopInventory();
            inv.clear();
            LoadedItem item = QueuedItems.items.remove(p.getUniqueId());
            if(item != null){
                item.ParseEveryting(p, Helpers.GetPlayerRewardLevel(p.getUniqueId().toString()));
            }
            else{
                Bukkit.getLogger().info("WARING PLAYER HAD NO ITEM WHEN CLOSSING SHUFFLE GUI THIS SHOULD NOT HAPPENDED");
            }
        }
        else if (view.getTitle().equals(Helpers.CFormat(Lang.GetTrans("SlideGUI")))) {
            Inventory inv = view.getTopInventory();
            inv.clear();
            LoadedItem item = QueuedItems.items.remove(p.getUniqueId());
            if(item != null){
                item.ParseEveryting(p, Helpers.GetPlayerRewardLevel(p.getUniqueId().toString()));
            }
            else{
                Bukkit.getLogger().info("WARING PLAYER HAD NO ITEM WHEN CLOSSING SLIDE GUI THIS SHOULD NOT HAPPENDED");
            }
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getView().getTitle().equals(Helpers.CFormat(Lang.GetTrans("ShuffleGUI")))) {
            event.setCancelled (true);
        }
        else if(event.getView().getTitle().equals(Helpers.CFormat(Lang.GetTrans("SlideGUI")))) {
            event.setCancelled (true);
        }
    }
}


