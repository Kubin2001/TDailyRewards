package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
            //Helpers.SendFormated (p,"&6DEBUG wygenerowano dane codziennej nagrody");
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        InventoryView view = event.getView();

        if (view.getTitle().equals(Helpers.CFormat(Lang.GetTrans("RewardGUI")))) {
            Inventory inv = view.getTopInventory();

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);

                if (item == null || item.getType() == Material.AIR) continue;
                p.getWorld().dropItemNaturally(p.getLocation(), item);
            }
            inv.clear();
        }
    }
}
