package TribeDailyRewards.tribeDailyRewards;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import javax.swing.*;
import java.time.LocalDateTime;

public class TribeEventListener  implements Listener {
    	private final Plugin plugin;

    	TribeEventListener(Plugin plugin){
		this.plugin = plugin;
	}


	@EventHandler
    	public void onJoin(PlayerJoinEvent event){
	    Player p = event.getPlayer ();
	    String uuid = p.getUniqueId ().toString ();
	    if(Helpers.IsPlayerRewardLevel (uuid)){
		LocalDateTime dateTime = Helpers.GetPlayerRewardTimer (uuid);
		if(dateTime.isBefore (LocalDateTime.now ()) ){
		    Runnable task = ()->{
			if(p.isOnline ()){
			    Helpers.SendFormated (p,Lang.GetTrans ("RewardReady"));
			}
		    };
		    Helpers.RunTask (plugin,task,80); //4 sekundy

		}
	    }
	    else{
		Helpers.SetPlayerRewardLevel (uuid,1);
		Helpers.SetPlayerRewardTimer (uuid, LocalDateTime.now());
		//Helpers.SendFormated (p,"&6DEBUG wygenerowano dane codziennej nagrody");
	    }
	}
}
