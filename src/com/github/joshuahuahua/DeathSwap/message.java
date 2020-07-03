package com.github.joshuahuahua.DeathSwap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class message {
    public static void global(String message) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',message));
    }

    public static void sender(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('$',message));
    }

    public static void player(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('$',message));
    }
}