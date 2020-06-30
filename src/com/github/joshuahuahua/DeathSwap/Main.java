package com.github.joshuahuahua.DeathSwap;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DeathSwap Enabled");
    }
    @Override
    public void onDisable() {
        getLogger().info("DeathSwap Disabled");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('$',"$c$lWelcome to DeathSwap"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('$',"$cBy Joshalot and Nel"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('$',"$aUsage: ds <player1> <player2>"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('$',"$aUsage: ds clear"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('$',"$aUsage: ds start"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('$',"$aUsage: ds stop"));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (isRunning) {
            isRunning = false;
            player1.setGameMode(GameMode.SPECTATOR);
            player2.setGameMode(GameMode.SPECTATOR);

            Bukkit.getScheduler().cancelTask(timer);
            Bukkit.getScheduler().cancelTask(countDown);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$lDeath Swap has ended"));
            if (event.getEntity().getName().equals(player1.getName())) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c" + player2.getName() + " $fis the winner!"));
            } else {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c" + player1.getName() + " $fis the winner!"));
            }
        }
    }

    Player player1 = null;
    Player player2 = null;
    int time = 3;
    int timer;
    int countDown;
    int secondsRemaining;
    boolean isRunning;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("DeathSwap") || label.equalsIgnoreCase("ds")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("BRUH");
            }

            if (args.length == 0) {
                sender.sendMessage("Usage: ds <player1> <player2>");
                sender.sendMessage("Usage: ds clear");
                sender.sendMessage("Usage: ds settime <num>");
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
                if (args[0].equals("settime")) {
                    time = Integer.parseInt(args[1]);
                    sender.sendMessage("Set time to " + time);
                } else {
                    player1 = Bukkit.getPlayerExact(args[0]);
                    player2 = Bukkit.getPlayerExact(args[1]);
                    if (player1 != null && player2 != null && player1 != player2) {
                        sender.sendMessage("Set player1 to " + player1.getName());
                        sender.sendMessage("Set player2 to " + player1.getName());
                        player1.sendMessage("You have been set to player1");
                        player2.sendMessage("You have been set to player2");
                    } else {
                        sender.sendMessage("Usage: ds <player1> <player2>");
                    }
                }
            }
            //START
            if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
                if (player1 != null && player2 != null) {
                    if (!isRunning) {

                        isRunning = true;
                        secondsRemaining = time*60;
                        player1.getWorld().setTime(0);
                        player1.setGameMode(GameMode.SURVIVAL);
                        player2.setGameMode(GameMode.SURVIVAL);
                        player1.setHealth(20);
                        player2.setHealth(20);
                        player1.setFoodLevel(20);
                        player2.setFoodLevel(20);
                        player1.getInventory().clear();
                        player2.getInventory().clear();


                        World world = Bukkit.getServer().getWorld("world");

                        double player1X = Math.random() * ( 1000 - -1000 );
                        double player1Y = Math.random() * ( 1000 - -1000 );
                        double player2X = Math.random() * ( 1000 - -1000 );
                        double player2Y = Math.random() * ( 1000 - -1000 );

                        Location startCoord1 = new Location(world,player1X,200,player1Y);
                        Location startCoord2 = new Location(world,player2X,200,player2Y);
                        player1.teleport(startCoord1);
                        player2.teleport(startCoord2);
                        player1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,8*20,200));
                        player2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,8*20,200));
                        //effect give Joshalot minecraft:resistance 10 200 true

                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$lDeath Swap Started"));
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$cSwap time minutes: " + time));
                        BukkitScheduler scheduler = getServer().getScheduler();
                        timer = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                            @Override
                            public void run() {
                                Location player1pos = player1.getLocation();
                                Location player2pos = player2.getLocation();
                                player1.teleport(player2pos);
                                player2.teleport(player1pos);
                                secondsRemaining = time*60;
                            }
                        }, 20*60*time, 20*60*time);


                        countDown = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                            @Override
                            public void run() {
                                secondsRemaining-=1;
                                //int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                                for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}) {
                                    if (i == secondsRemaining) {
                                        Bukkit.broadcastMessage(i+1 + " Seconds remaining!");
                                    }
                                }
                            }
                        }, 0, 20);



                    } else {
                        sender.sendMessage("Game already in progress!");
                    }
                } else {
                    sender.sendMessage("Please set 2 valid players.");
                    sender.sendMessage("Usage: ds <player1> <player2>");
                }
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                if (isRunning) {
                    isRunning = false;
                    Bukkit.getScheduler().cancelTask(timer);
                    Bukkit.getScheduler().cancelTask(countDown);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$lDeath Swap Stopped"));
                } else {
                    sender.sendMessage("No active Death Swap!");
                }
            }



            return true;
        }
        return false;
    }
}
