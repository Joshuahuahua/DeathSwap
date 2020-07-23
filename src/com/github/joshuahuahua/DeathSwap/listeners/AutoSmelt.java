package com.github.joshuahuahua.DeathSwap.listeners;

import com.github.joshuahuahua.DeathSwap.Main;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoSmelt implements Listener {
    private final Main plugin;

    public AutoSmelt(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockMined(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Block block = event.getBlock();
        Material drop;

        if (Main.gameRules.get("autoSmelt")) {
            if (event.getBlock().getType() == Material.GOLD_ORE) {
                drop = Material.GOLD_INGOT;
            } else if (event.getBlock().getType() == Material.IRON_ORE) {
                drop = Material.IRON_INGOT;
            } else {
                return;
            }

            if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
                return;
            }

            Material[] ironPick = {Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE, Material.STONE_PICKAXE};
            Material[] goldPick = {Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE};
            ArrayList<Material> ironPickList = new ArrayList<>(Arrays.asList(ironPick));
            ArrayList<Material> goldPickList = new ArrayList<>(Arrays.asList(goldPick));

            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                return;
            }

            if (goldPickList.contains(player.getInventory().getItemInMainHand().getType()) && drop == Material.GOLD_INGOT) {
                block.setType(Material.AIR);
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drop, 1));
                event.setCancelled(true);
            } else if (ironPickList.contains(player.getInventory().getItemInMainHand().getType()) && drop == Material.IRON_INGOT) {
                block.setType(Material.AIR);
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drop, 1));
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof Damageable) {
                    Damageable damageable = (Damageable) meta;
                    damageable.setDamage(damageable.getDamage() + 1);
                    item.setItemMeta(meta);
                    event.setCancelled(true);
                }
            }
        }

        // Add durability

        // Add xp
    }
}
