package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.Main;
import com.github.joshuahuahua.DeathSwap.message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class playerClickInventory implements Listener {

    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("Gamemodes")) {

            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Default")) {
                changeGamemode(player,"Default");
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Speed")) {
                changeGamemode(player,"Speed");
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Blind")) {
                changeGamemode(player,"Blind");
            }
            event.setCancelled(true);



        } else if (event.getView().getTitle().equals("Gamerules")) {

            if (event.getCurrentItem().getItemMeta().getLore().get(0).equals("Enabled")) {
                player.closeInventory();
            } else {
                return;
            }
            event.setCancelled(true);
        }
    }
    public static void changeGamemode(Player player, String gamemode) {
        player.closeInventory();
        message.global("Gamemode set to $l" + gamemode);
        //Main.gamemode = gamemode
        // or something to that effect
    }
}


