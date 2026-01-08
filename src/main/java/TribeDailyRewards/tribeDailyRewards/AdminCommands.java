package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminCommands implements CommandExecutor, TabCompleter {
    public List<String> GetPlayerNames(String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                String name = p.getName();
                if (name.toLowerCase().startsWith(prefix)) {
                    names.add(name);
                }
            }
            Collections.sort(names);
            return names;
        }

        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setRewardLevel")) {
            if (args.length != 2) {
                sender.sendMessage(Helpers.CFormat(Lang.GetTrans("WrongArgs2")));
                return true;
            }
            String targetName = args[0];
            int value = 0;
            try {
                value = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Helpers.CFormat(Lang.GetTrans("TextToNum")));
                return true;
            }

            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                sender.sendMessage(Helpers.CFormat(Lang.GetTrans("NotOnline") + targetName));
                return true;
            }
            Helpers.SetPlayerRewardLevel(target.getUniqueId().toString(), value);
            if (sender instanceof Player p) {
                Helpers.SendFormated(p, Lang.GetTrans("SetTo") + value);
            } else {
                Bukkit.getLogger().info(Lang.GetTrans("SetTo") + value);
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("resetRewardTimer")) {
            if (args.length != 1) {
                sender.sendMessage(Helpers.CFormat(Lang.GetTrans("WrongArgs1")));
                return true;
            }
            String targetName = args[0];

            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                sender.sendMessage(Helpers.CFormat(Lang.GetTrans("NotOnline") + targetName));
                return true;
            }
            Helpers.SetPlayerRewardTimer(target.getUniqueId().toString(), LocalDateTime.now());

            if (sender instanceof Player p) {
                Helpers.SendFormated(p, Lang.GetTrans("ResetFor"));
            } else {
                Bukkit.getLogger().info(Lang.GetTrans("ResetFor"));
            }

            return true;
        } else if (command.getName().equalsIgnoreCase("rewardInfo")) {
            if (args.length != 1) {
                sender.sendMessage(Helpers.CFormat(Lang.GetTrans("WrongArgs1")));
                return true;
            }
            String targetName = args[0];

            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                sender.sendMessage(Helpers.CFormat(Lang.GetTrans("NotOnline") + targetName));
                return true;
            }

            if (sender instanceof Player p) {
                p.sendMessage(Lang.GetTrans("LevelInfo") + Helpers.GetPlayerRewardLevel(target.getUniqueId().toString()));
                Duration remain = Duration.between(LocalDateTime.now(), Helpers.GetPlayerRewardTimer(target.getUniqueId().toString()));
                long totalMinutes = remain.toMinutes();
                long days = totalMinutes / (24 * 60);
                long hours = (totalMinutes % (24 * 60)) / 60;
                long minutes = totalMinutes % 60;
                Helpers.SendFormated(p, Lang.GetTrans("WaitTimeInfo") + days + "d " + hours + "h " + minutes + "m");
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (command.getName()) {
            case "setRewardLevel":
            case "resetRewardTimer":
            case "rewardInfo":
                return GetPlayerNames(args);
            default:
                return Collections.emptyList();
        }
    }
}
