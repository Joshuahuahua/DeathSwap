package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.Main;
import com.github.joshuahuahua.DeathSwap.message;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class onLeave implements Listener {

    private final Main plugin;

    public onLeave(Main plugin) {
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
                        Main.isRunning = false;
                        Main.stopSchedulers();
                        message.global("$c$lDeath Swap has ended");
                        message.global("$a$l" + Main.lobby.get(0).getName() + " $r$7is the winner!");

                        Main.lobby.get(0).getInventory().clear();
                        Main.lobby.get(0).getInventory().addItem(new ItemStack(Material.CAKE, 1));

                        Main.host = null;
                        Main.lobby.clear();
                    } else {
                        message.global("$c$l" + Main.lobby.size() + " players remaining.");
                    }
                }
            }
        }.runTaskLater(plugin, 5);


    }

}
