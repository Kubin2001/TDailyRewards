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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.UUID;

public class TEventListener implements Listener {
    private Plugin plugin = null;

    TEventListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        UUID uuid = p.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            TDailyRewards tPlugin = (TDailyRewards)plugin;
            tPlugin.LoadPlayer(uuid);
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player p = event.getPlayer();
        UUID uuid = p.getUniqueId();
        QueuedItems.items.remove(p.getUniqueId());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            TDailyRewards tPlugin = (TDailyRewards)plugin;
            tPlugin.SavePlayer(uuid);
            Bukkit.getScheduler().runTask(plugin, () -> {
                Helpers.data.remove(uuid);
                Helpers.dates.remove(uuid);
            });
        });
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
                item.ParseEveryting(p, Helpers.GetPlayerRewardLevel(p.getUniqueId()));
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
                item.ParseEveryting(p, Helpers.GetPlayerRewardLevel(p.getUniqueId()));
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


