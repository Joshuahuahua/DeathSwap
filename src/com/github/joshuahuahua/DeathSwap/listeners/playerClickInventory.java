package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class playerClickInventory implements Listener {

    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        Inventory inv = event.getInventory()
        inv.getType().name()
        message.global(event.getClickedInventory().getClass().getName());
        message.global(event.getClickedInventory().getClass().getSimpleName());
        message.global(event.getClickedInventory().getClass().getCanonicalName());
        message.global(event.getClickedInventory().toString());



        if ("test" == "test") {
            if (event.getCurrentItem().getItemMeta() != null) {
                event.getCurrentItem().getItemMeta().getDisplayName();
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Default")) {
                    player.sendMessage("You clicked Default!!!");
                    event.setCancelled(true);
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Speed")) {
                    player.sendMessage("You clicked Speed!!!");
                }
            }
        }
        event.setCancelled(true);
    }
}

