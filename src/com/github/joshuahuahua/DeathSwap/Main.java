package com.github.joshuahuahua.DeathSwap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {

    

    @Override
    public void onEnable() {
        getLogger().info("DeathSwap Enabled");
        Bukkit.broadcastMessage("POGGERS");
    }
    @Override
    public void onDisable() {
        getLogger().info("DeathSwap Disabled");
    }

    Player player1 = null;
    Player player2 = null;
    int time = 5;
    int timer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("DeathSwap") || label.equalsIgnoreCase("ds")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("BRUH");
            }

            if (args.length == 0) {
                sender.sendMessage("Usage: ds <player1> <player2>");
                sender.sendMessage("Usage: ds clear");
                sender.sendMessage("Usage: ds start");
                sender.sendMessage("Usage: ds stop");
            }
            // ds p1/p2
            if (args.length == 1 && args[0].equalsIgnoreCase("p1")) {
                if (player1 != null) {
                    sender.sendMessage("Set player1 to " + player1.getName());
                } else {
                    sender.sendMessage("That player has not been set.");
                }
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("p2")) {
                if (player2 != null) {
                    sender.sendMessage("Set player1 to " + player2.getName());
                } else {
                    sender.sendMessage("That player has not been set.");
                }
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                player1 = null;
                player2 = null;
                sender.sendMessage("Cleared players");
            }

            if (args.length == 2) {
                player1 = Bukkit.getPlayerExact(args[0]);
                player2 = Bukkit.getPlayerExact(args[1]);
                if (player1 != null && player2 != null) {
                    sender.sendMessage("Set player1 to " + player1.getName());
                    sender.sendMessage("Set player2 to " + player1.getName());
                    player1.sendMessage("You have been set to player1");
                    player2.sendMessage("You have been set to player2");
                } else {
                    sender.sendMessage("Usage: ds <player1> <player2>");
                }
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
                Bukkit.broadcastMessage("DeathSwap Started");
                BukkitScheduler scheduler = getServer().getScheduler();
                timer = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                    @Override
                    public void run() {
                        Location player1pos = player1.getLocation();
                        Location player2pos = player2.getLocation();
                        player1.teleport(player2pos);
                        player2.teleport(player1pos);
                    }
                },20*time, 20*time);
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                Bukkit.getScheduler().cancelTask(timer);
                Bukkit.broadcastMessage("DeathSwap Stopped");
            }



            return true;
        }
        return false;
    }
}
