package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Speed implements Listener {
    private final Main plugin;

    public Speed(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void speedEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if Main.gameRules.get("speed") {
            player.setWalkSpeed(0.2f);
        } else {
            player.setWalkSpeed(0.1f);
        }
    }
}
