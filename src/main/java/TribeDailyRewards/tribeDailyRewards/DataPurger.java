package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class DataPurger {
    static void PurgeOldData(Plugin pl){
        if(MainConfig.purgeData != 1){return;}
        if(MainConfig.purgeDays < 1) {return;}

        pl.getLogger().info("Purging old data");

        long now = System.currentTimeMillis();
        long maxDays = MainConfig.purgeDays;
        long maxMillis = maxDays * 24L * 60L * 60L * 1000L;
        long removedData = 0; // I assume that it should be equal for boothMaps
        long removedDates = 0;

        Iterator<Map.Entry<String, Integer>> it = Helpers.data.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = it.next();

            UUID uuid = UUID.fromString(entry.getKey());
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            long lastPlayed = player.getLastPlayed();

            if (lastPlayed == 0) {
                it.remove();
                removedData++;
                continue;
            }

            if (now - lastPlayed > maxMillis) {
                it.remove();
                removedData++;
            }
        }

        Iterator<Map.Entry<String, LocalDateTime>> it2 = Helpers.dates.entrySet().iterator();

        while (it2.hasNext()) {
            Map.Entry<String, LocalDateTime> entry = it2.next();

            UUID uuid = UUID.fromString(entry.getKey());
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            long lastPlayed = player.getLastPlayed();

            if (lastPlayed == 0) {
                it2.remove();
                removedDates++;
                continue;
            }

            if (now - lastPlayed > maxMillis) {
                it2.remove();
                removedDates++;
            }
        }

        pl.getLogger().info("Purge Finished");
        pl.getLogger().info("Reward Days Entries Removed: " + removedData);
        pl.getLogger().info("Reward Dates Entries Removed: " + removedDates);
    }
}
