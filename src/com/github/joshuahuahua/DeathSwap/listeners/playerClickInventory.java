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
                Main.gameRules.put("autoSmelt", false);
                Main.gameRules.put("speed", false);
                Main.gameRules.put("countDown", true);
                changeGamemode(player,"Default");
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Speed")) {
                Main.gameRules.put("autoSmelt", true);
                Main.gameRules.put("speed", true);
                Main.time = 60;
                changeGamemode(player,"Speed");
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Blind")) {
                Main.gameRules.put("countDown", false);
                changeGamemode(player,"Blind");
            }
            event.setCancelled(true);



        }

        if (event.getView().getTitle().equals("Gamerules")) {
            Main.gameRules.put(event.getCurrentItem().getItemMeta().getDisplayName(), !Main.gameRules.get(event.getCurrentItem().getItemMeta().getDisplayName()));
            event.setCancelled(true);
            Main.selectInv(player, "gameruleInv");
        }
    }
    public static void changeGamemode(Player player, String gamemode) {
        player.closeInventory();
        message.global("Gamemode set to $l" + gamemode);
        Main.gamemode = gamemode;
    }
}

