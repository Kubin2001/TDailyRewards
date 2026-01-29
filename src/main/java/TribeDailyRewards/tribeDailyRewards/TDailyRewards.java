package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.inventory.MainHand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

public final class TDailyRewards extends JavaPlugin {

    private final File dataFile = new File(getDataFolder(), "playerData.csv");
    private final File datesFile = new File(getDataFolder(), "dates.csv");
    private final Map<String, Integer> data = new HashMap<>();
    private final Map<String, LocalDateTime> dates = new HashMap<>();
    private static Economy eco;
    private Random random = new Random();

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[Daily Reward] Init");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
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
        getCommand("reward").setExecutor(new Reward());
        getCommand("restart").setExecutor(new CommandRestart(this));
        getCommand("restartItems").setExecutor(new CommandRestartItems(this));

        AdminCommands handler = new AdminCommands();

        getCommand("setRewardLevel").setExecutor(handler);
        getCommand("setRewardLevel").setTabCompleter(handler);

        getCommand("resetRewardTimer").setExecutor(handler);
        getCommand("resetRewardTimer").setTabCompleter(handler);

        getCommand("rewardInfo").setExecutor(handler);
        getCommand("rewardInfo").setTabCompleter(handler);

        getCommand("moveReward").setExecutor(handler);
        getCommand("moveReward").setTabCompleter(handler);


        if (dataFile.exists()) {
            try {
                Scanner scanner = new Scanner(dataFile);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    Integer value = Integer.parseInt(parts[1]);
                    data.put(parts[0], value);
                }
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else { // Create empty file
            try {
                dataFile.createNewFile();
                getLogger().info("[Daily Reward] Creating new File playerData.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (datesFile.exists()) {
            try {
                Scanner scanner = new Scanner(datesFile);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    int year = Integer.parseInt(parts[1]);
                    int month = Integer.parseInt(parts[2]);
                    int day = Integer.parseInt(parts[3]);
                    int hour = Integer.parseInt(parts[4]);
                    int minute = Integer.parseInt(parts[5]);
                    int second = Integer.parseInt(parts[6]);

                    LocalDateTime date = LocalDateTime.of(year, month, day, hour, minute, second);
                    dates.put(parts[0], date);
                }
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else { // Create empty file
            try {
                datesFile.createNewFile();
                getLogger().info("[Daily Reward] Creating new File Utworzono nowy plik playerDates.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SetupEconomy();
        DataPurger.PurgeOldData(this);
    }

    @Override
    public void onDisable() {
        try (PrintWriter writer = new PrintWriter(dataFile)) {
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue().toString());
            }
            getLogger().info("[Daily Reward] Saving playerData.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter writer = new PrintWriter(datesFile)) {
            for (Map.Entry<String, LocalDateTime> entry : dates.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue().getYear() + "," +
                        entry.getValue().getMonthValue() + "," + entry.getValue().getDayOfMonth() + "," +
                        entry.getValue().getHour() + "," + entry.getValue().getMinute() + "," +
                        entry.getValue().getSecond());
            }
            getLogger().info("[Daily Reward] Saving dates.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public Map<String, Integer> GetDataMap() {
        return data;
    }

    public Economy GetEco() {
        return eco;
    }
}
