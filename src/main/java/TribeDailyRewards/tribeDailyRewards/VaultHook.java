package TribeDailyRewards.tribeDailyRewards;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.Bukkit;

public class VaultHook {

    private Economy eco = null;

    public boolean SetupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return true;
    }

    public Economy getEconomy() {
        return eco;
    }
}