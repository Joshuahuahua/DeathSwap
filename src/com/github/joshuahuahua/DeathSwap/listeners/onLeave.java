package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.Main;
import com.github.joshuahuahua.DeathSwap.message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class OnLeave implements Listener {

    private final Main plugin;

    public OnLeave(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.isRunning) {
                    message.global("$6" + event.getPlayer().getName() + " $ahas left the DeathSwap!");
                    Main.lobby.remove(event.getPlayer());
                    if (Main.lobby.size() == 1) {
                        OnDeath.endGame();
                    } else {
                        message.global("$c$l" + Main.lobby.size() + " players remaining.");
                    }
                }
            }
        }.runTaskLater(plugin, 5);


    }

}
