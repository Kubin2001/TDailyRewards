package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemWithCommand {
    public ItemStack item = null;
    public String command = null;

    public ItemWithCommand(ItemStack item , String command){
        this.item = item;
        this.command = command;
    }
}
