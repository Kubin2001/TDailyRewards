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

    public File playerDataFolder = null;
    private final Map<UUID, Integer> data = new HashMap<>();
    private final Map<UUID, LocalDateTime> dates = new HashMap<>();


    public void LoadPlayer(UUID uuid) {
        Integer days = null;
        LocalDateTime date = null;

        File file = new File(playerDataFolder, uuid + ".csv");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                if (scanner.hasNextLine()) {
                    String[] line = scanner.nextLine().split(",");
                    if(line.length != 2) {
                        Bukkit.getLogger().severe("[TDaily Reward] cannot load player with uuid " + uuid + " - broken save data");

                    } else {
                        days = Integer.parseInt(line[0]);
                        date = LocalDateTime.parse(line[1]);
                    }
                }
            } catch (Exception e) {
                getLogger().severe("Error when loading player data with UUID: " + uuid);
            }
        }

        Integer finalDays = days;
        LocalDateTime finalDate = date;
        Bukkit.getScheduler().runTask(this, () -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) return;

            if (finalDays == null || finalDate == null) {
                Helpers.data.put(uuid, 1);
                Helpers.dates.put(uuid, LocalDateTime.now());
                Helpers.SendFormated(p, Lang.GetTrans("RewardReady"));
            } else {
                Helpers.data.put(uuid, finalDays);
                Helpers.dates.put(uuid, finalDate);

                if (finalDate.isBefore(LocalDateTime.now())) {
                    Helpers.SendFormated(p, Lang.GetTrans("RewardReady"));
                }
            }
        });
    }

    public void SavePlayer(UUID uuid) {
        Integer days = Helpers.data.get(uuid);
        LocalDateTime date = Helpers.dates.get(uuid);
        if (days == null || date == null) {
            Bukkit.getLogger().severe("[TDaily Reward] cannot safe player with uuid " + uuid);
            return;
        }

        File file = new File(playerDataFolder, uuid + ".csv");

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println(days + "," + date.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[TDaily Rewards] Starting...");
        File dataFolder = getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        playerDataFolder = new File(dataFolder, "data");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        try {
            MainConfig.Load(this);
            Lang.LoadLang(this, MainConfig.langName + ".yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Helpers.Init(data, dates);
        SetupEconomy();
        ItemParser.Init(this);

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

    private void SetupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            VaultHook ecoHook = new VaultHook();
            if (ecoHook.SetupEconomy()) {
                Helpers.ecoHook = ecoHook;
                Helpers.hasEconomy = true;
                getLogger().info("[T Daily Rewards] Vault found economy integrated");
            } else {
                Helpers.hasEconomy = false;
                getLogger().warning("[T Daily Rewards] Vault found but no plugin provides economy");
            }
        }
        else {
            Helpers.hasEconomy = false;
            getLogger().info("[T Daily Rewards] No vault found money trading will be disabled");
        }
    }
}
