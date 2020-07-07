package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.Main;
import com.github.joshuahuahua.DeathSwap.files.customConfig;
import com.github.joshuahuahua.DeathSwap.message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class onJoin implements Listener {

    private final Main plugin;

    public onJoin(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (customConfig.get().getBoolean("messageOnJoin")) {
                    message.player(event.getPlayer(), "$c$lWelcome to DeathSwap");
                    message.player(event.getPlayer(), "$cBy Joshalot and Nel");
                    message.player(event.getPlayer(), "$aUse /ds help for available commands!");
                }
            }
        }.runTaskLater(plugin, 5);

    }

}