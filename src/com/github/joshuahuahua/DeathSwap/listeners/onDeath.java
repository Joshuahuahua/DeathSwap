package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.Main;
import com.github.joshuahuahua.DeathSwap.message;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class onDeath implements Listener {

    private final Main plugin;

    public onDeath(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.isRunning) {
                    int i;
                    for (i = 0; i < Main.lobby.size(); i++) {
                        if (Main.lobby.get(i).getName().equals(event.getEntity().getName())) {
                            event.getEntity().setGameMode(GameMode.SPECTATOR);
                            Main.lobby.remove(i);
                        }
                    }
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
                        message.global("$a$l" + event.getEntity().getName() + " has died!");
                        message.global("$c$l" + Main.lobby.size() + " players remaining.");
                    }
                }
            }
        }.runTaskLater(plugin, 5);


    }

}
