package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class playerClickInventory implements Listener {

    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        message.global(event.getView().getTitle());
        if (event.getView().getTitle().equals("Gamemodes")) {
            if (event.getCurrentItem().getItemMeta() != null) {
                event.getCurrentItem().getItemMeta().getDisplayName();
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Default")) {
                    player.sendMessage("You clicked Default!!!");
                    event.setCancelled(true);
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Speed")) {
                    player.sendMessage("You clicked Speed!!!");
                }
            }





        } else if (event.getView().getTitle().equals("Test")) {
            player.sendMessage("You clicked on the second gui!!!");
        }
        event.setCancelled(true);
    }
}

