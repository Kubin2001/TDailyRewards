package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

import net.milkbowl.vault.economy.Economy;

public final class TDailyRewards extends JavaPlugin {

    public File daysFolder = null;
    public File datesFolder = null;
    private final File datesFile = new File(getDataFolder(), "dates.csv");
    private final Map<UUID, Integer> data = new HashMap<>();
    private final Map<UUID, LocalDateTime> dates = new HashMap<>();
    private static Economy eco;
    private Random random = new Random();


    public void LoadPlayer(UUID uuid) {
        final Integer[] loadedValue = {null};
        final LocalDateTime[] loadedDate = {null};

        File dayFile = new File(daysFolder, uuid + ".csv");
        if (dayFile.exists()) {
            try (Scanner scanner = new Scanner(dayFile)) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    loadedValue[0] = Integer.parseInt(parts[1]);
                }
            } catch (Exception e) {
                getLogger().severe("Error when loading player reward days with UUID: " + uuid);
            }
        }

        File dateFile = new File(datesFolder, uuid + ".csv");
        if (dateFile.exists()) {
            try (Scanner scanner = new Scanner(dateFile)) {
                if (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    loadedDate[0] = LocalDateTime.of(
                            Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                            Integer.parseInt(parts[5]), Integer.parseInt(parts[6])
                    );
                }
            } catch (Exception e) {
                getLogger().severe("Error when loading player date with UUID: " + uuid);
            }
        }

        Bukkit.getScheduler().runTask(this, () -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) return;

            if (loadedValue[0] == null || loadedDate[0] == null) {
                Helpers.SetPlayerRewardLevel(uuid, 1);
                Helpers.SetPlayerRewardTimer(uuid, LocalDateTime.now());
                Helpers.SendFormated(p, Lang.GetTrans("RewardReady"));
            } else {
                Helpers.data.put(uuid, loadedValue[0]);
                Helpers.dates.put(uuid, loadedDate[0]);

                if (loadedDate[0].isBefore(LocalDateTime.now())) {
                    Helpers.SendFormated(p, Lang.GetTrans("RewardReady"));
                }
            }
        });
    }

    public void SavePlayer(UUID uuid) {
        Integer days = Helpers.data.get(uuid);
        if (days != null) {

            File file = new File(daysFolder, uuid + ".csv");

            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println(uuid + "," + days);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LocalDateTime date = Helpers.dates.get(uuid);
        if (date != null) {

            File file = new File(datesFolder , uuid + ".csv");

            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println(
                        uuid + "," +
                                date.getYear() + "," +
                                date.getMonthValue() + "," +
                                date.getDayOfMonth() + "," +
                                date.getHour() + "," +
                                date.getMinute() + "," +
                                date.getSecond()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[Daily Reward] Init");
        File dataFolder = getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        datesFolder = new File(dataFolder, "dates");
        if (!datesFolder.exists()) {
            datesFolder.mkdirs();
        }

        daysFolder = new File(dataFolder, "days");
        if (!daysFolder.exists()) {
            daysFolder.mkdirs();
        }
        try {
            MainConfig.Load(this);
            Lang.LoadLang(this, MainConfig.langName + ".yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SetupEconomy();
        ItemParser.Init(this);
        Helpers.Init(data, dates, eco);
        getServer().getPluginManager().registerEvents(new TEventListener(this), this);
        getCommand("reward").setExecutor(new Reward(this));
        getCommand("reload").setExecutor(new CommandRestart(this));
        getCommand("reloadItems").setExecutor(new CommandRestartItems(this));

        AdminCommands handler = new AdminCommands();

        getCommand("setRewardLevel").setExecutor(handler);
        getCommand("setRewardLevel").setTabCompleter(handler);

        getCommand("resetRewardTimer").setExecutor(handler);
        getCommand("resetRewardTimer").setTabCompleter(handler);

        getCommand("rewardInfo").setExecutor(handler);
        getCommand("rewardInfo").setTabCompleter(handler);

        getCommand("moveReward").setExecutor(handler);
        getCommand("moveReward").setTabCompleter(handler);

        DataPurger.PurgeOldData(this);
    }

    @Override
    public void onDisable() {
        Set<UUID> allUuids = new HashSet<>();

        allUuids.addAll(Helpers.data.keySet());
        allUuids.addAll(Helpers.dates.keySet());
        for (UUID uuid : allUuids) {
            SavePlayer(uuid);
        }

        getLogger().info("[Daily Reward] Saved days and dates data");
        Bukkit.getLogger().info("[Daily Reward] Closing");
    }

    private boolean SetupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault nie znaleziony!");
            return false;
        }

        var rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("Brak zarejestrowanego dostawcy ekonomii!");
            return false;
        }

        eco = rsp.getProvider();
        return eco != null;
    }

    public Economy GetEco() {
        return eco;
    }
}
