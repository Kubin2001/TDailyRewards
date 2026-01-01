package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.enchantments.Enchantment;

public class FullEnchant {
    public Enchantment enchant = null;
    public int level = 0;

    public FullEnchant(Enchantment enchant, int level) {
        this.enchant = enchant;
        this.level = level;
    }
}
