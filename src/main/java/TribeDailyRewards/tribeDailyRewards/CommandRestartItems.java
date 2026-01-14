package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class CommandRestartItems implements CommandExecutor {
    private Plugin pl = null;

    public CommandRestartItems(Plugin plugin) {
        this.pl = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long timeStart = System.nanoTime ();
        ItemParser.Clear();
        ItemParser.Init(pl);

        long timeEnd = System.nanoTime ();
        long lDurationSec = timeEnd - timeStart;

        double durationSec = lDurationSec / 1_000_000.0;
        if (sender instanceof Player p) {
            Helpers.SendFormated(p, Lang.GetTrans("ItemsRestarted") + " time: " + durationSec + " ms");
        } else {
            pl.getLogger().info(Lang.GetTrans("ItemsRestarted") + " time: " + durationSec + " ms");
        }
        return true;
    }
}
