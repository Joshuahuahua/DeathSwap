package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.Main;
import com.github.joshuahuahua.DeathSwap.files.customConfig;
import com.github.joshuahuahua.DeathSwap.message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class OnJoin implements Listener {

    private final Main plugin;

    public OnJoin(Main plugin) {
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
                event.getPlayer().setWalkSpeed(0.2f);
                event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
            }
        }.runTaskLater(plugin, 5);

    }

}