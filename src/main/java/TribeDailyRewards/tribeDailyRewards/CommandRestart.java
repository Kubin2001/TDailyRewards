package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.time.Duration;

public class CommandRestart implements CommandExecutor {
    private Plugin pl = null;

    public CommandRestart(Plugin plugin) {
        this.pl = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!pl.getDataFolder().exists()) {
            pl.getDataFolder().mkdirs();
        }
        long timeStart = System.nanoTime ();
        try {
            MainConfig.Load(pl);
            Lang.LoadLang(pl, MainConfig.langName + ".yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long timeEnd = System.nanoTime ();
        long lDurationSec = timeEnd - timeStart;

        double durationSec = lDurationSec / 1_000_000.0;
        if (sender instanceof Player p) {
            Helpers.SendFormated(p, Lang.GetTrans("Restarted") + " time: " + durationSec + " ms");
        } else {
            pl.getLogger().info(Lang.GetTrans("Restarted") + " time: " + durationSec + " ms");
        }
        return true;
    }
}
