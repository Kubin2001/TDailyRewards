package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.UUID;

public class DataPurger {

    static void PurgeOldData(Plugin pl) {

        if (MainConfig.purgeData != 1) return;
        if (MainConfig.purgeDays < 1) return;

        pl.getLogger().info("Purging old data");

        long now = System.currentTimeMillis();
        long maxMillis = MainConfig.purgeDays * 24L * 60L * 60L * 1000L;

        File dataFolder = new File(pl.getDataFolder(), "data");

        int removedPlayers = PurgeFolder(dataFolder, now, maxMillis);

        pl.getLogger().info("Purge Finished");
        pl.getLogger().info("Players removed: " + removedPlayers);
    }

    private static int PurgeFolder(File folder, long now, long maxMillis) {

        int removed = 0;

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null) return 0;

        for (File file : files) {

            try {
                String uuidString = file.getName().replace(".csv", "");
                UUID uuid = UUID.fromString(uuidString);

                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                long lastPlayed = player.getLastPlayed();

                if (lastPlayed == 0 || now - lastPlayed > maxMillis) {

                    if (file.delete()) {
                        removed++;
                    }
                }

            } catch (Exception ignored) {}
        }

        return removed;
    }
}
