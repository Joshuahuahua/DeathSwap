package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.Main;
import com.github.joshuahuahua.DeathSwap.files.customConfig;
import com.github.joshuahuahua.DeathSwap.message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onJoin implements Listener {

    public onJoin(Main plugin) {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (customConfig.get().getBoolean("messageOnJoin")){
            message.player(event.getPlayer(), "$c$lWelcome to DeathSwap");
            message.player(event.getPlayer(), "$cBy Joshalot and Nel");
            message.player(event.getPlayer(), "$aUse /ds help for available commands!");
        }
    }

}
