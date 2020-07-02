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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//#####################################  INIT  ##########################################################

public class Main extends JavaPlugin implements Listener {

    int time = 60;
    int timer;
    int countDown;
    int secondsRemaining;
    boolean isRunning;
    Player host = null;
    List<Player> lobby = new ArrayList<>();




    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DeathSwap Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("DeathSwap Disabled");
    }


    //######################################## On join #########################################################

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('$',"$c$lWelcome to DeathSwap"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('$',"$cBy Joshalot and Nel"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('$',"$aUse /ds help for available commands!"));
    }


    //######################################## On Death #########################################################

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (isRunning) {
            int i;
            for (i = 0; i < lobby.size(); i++) {
                if (lobby.get(i).getName().equals(event.getEntity().getName())) {
                    event.getEntity().setGameMode(GameMode.SPECTATOR);
                    lobby.remove(i);
                }
            }
            if (lobby.size() == 1) {
                isRunning = false;
                Bukkit.getScheduler().cancelTask(timer);
                Bukkit.getScheduler().cancelTask(countDown);

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$lDeath Swap has ended"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$a" + lobby.get(0).getName() + " $fis the winner!"));

                lobby.get(0).sendMessage(ChatColor.translateAlternateColorCodes('$',"$d$lYou are the winner!"));
                lobby.get(0).sendMessage(ChatColor.translateAlternateColorCodes('$',"$dHave some cake :)"));
                lobby.get(0).getInventory().clear();
                lobby.get(0).getInventory().addItem(new ItemStack(Material.CAKE, 1));

                lobby.clear();
            } else {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$l" + event.getEntity().getName() + " has died!"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$l" + lobby.size() + " players remaining."));
            }
        }
    }

    //######################################## On command #########################################################

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("DeathSwap") || label.equalsIgnoreCase("ds")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Error. Please use chat!");
                return false;
            }

            //################################# /ds help ################################
            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                sender.sendMessage("Usage: /ds create");
                sender.sendMessage("Usage: /ds timeset <minutes>");
                sender.sendMessage("Usage: /ds join");
                sender.sendMessage("Usage: /ds leave");
                sender.sendMessage("Usage: /ds start");
                sender.sendMessage("Usage: /ds stop");
            }



            //############################## /ds timeset <num> ################################
            if (args.length == 2 && args[0].equals("timeset")) {
                if (sender.getName().equals(host.getName())) {
                    if (!isRunning) {
                        time = Integer.parseInt(args[1])*60;
                        sender.sendMessage("Set time to " + time/60);
                    } else {
                        sender.sendMessage("You can not change the game mid-game!");
                    }
                } else {
                    sender.sendMessage("Only the host can do that!");
                }
            }


            //############################## /ds create ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
                if (host == null) {
                    host = (Player) sender;
                    lobby.add(host);
                    Bukkit.broadcastMessage(host.getName() + " is hosting DeathSwap!");
                    Bukkit.broadcastMessage("Use /ds join to join!");
                } else {
                    sender.sendMessage(host.getName() + " is already hosting DeathSwap!");
                    sender.sendMessage("Use /ds join to join!");
                }
                sender.sendMessage("players: " + lobby);
            }



            //############################## /ds join ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("join")) {
                if (host != null) {
                    if (!lobby.contains((Player) sender)) {
                        lobby.add((Player) sender);
                        Bukkit.broadcastMessage(sender.getName() + " has joined the lobby!");
                        sender.sendMessage("Current players in your lobby:");
                        for (Player player : lobby) {
                            sender.sendMessage(player.getName());
                        }
                    } else {
                        sender.sendMessage("You are already in a lobby!");
                    }
                } else {
                    sender.sendMessage("There are no available games to join!");
                    sender.sendMessage("Use /ds create to create one!");
                }
                sender.sendMessage("players: " + lobby);
            }



            //############################## /ds leave ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                if (lobby.contains((Player) sender)) {
                    lobby.remove((Player) sender);
                    Bukkit.broadcastMessage(sender.getName() + " has left the lobby!");
                } else {
                    sender.sendMessage("You are not in a lobby!");
                    sender.sendMessage("Use /ds create to create one or /ds join to join an existing one!");
                }
                if (sender.getName().equals(host.getName())) {
                    Bukkit.broadcastMessage(host.getName() + " has closed the lobby!");
                    for (Player player : lobby) {
                        player.sendMessage("You have been removed from the lobby!");
                    }
                    host = null;
                    lobby.clear();
                }
                sender.sendMessage("players: " + lobby);
            }



            //############################## /ds stop ################################

            if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                if (sender.getName().equals(host.getName())) {
                    if (isRunning) {
                        isRunning = false;
                        Bukkit.getScheduler().cancelTask(timer);
                        Bukkit.getScheduler().cancelTask(countDown);
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$', "$c$lDeath Swap Stopped"));
                    } else {
                        sender.sendMessage("No active Death Swap!");
                    }
                } else {
                    sender.sendMessage("Only the host can do that!");
                }
            }



            //############################# START ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
                if (sender.getName().equals(host.getName())) {
                    if (!isRunning) {
                        if (lobby.size() > 1) {
                            isRunning = true;
                            secondsRemaining = time;

                            World world = Bukkit.getServer().getWorld("world");
                            assert world != null;
                            world.setTime(0);

                            //############################# Init Players ################################
                            for (Player player : lobby) {
                                player.setGameMode(GameMode.SURVIVAL);
                                player.getInventory().clear();
                                player.setHealth(20);
                                player.setFoodLevel(20);

                                double playerX = Math.random() * ( 1000 - -1000 );
                                double playerY = Math.random() * ( 1000 - -1000 );
                                Location startCoord = new Location(world,playerX,200,playerY);
                                player.teleport(startCoord);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,8*20,200));
                            }

                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$lDeath Swap Started"));
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$cSwap time minutes: " + time));
                            BukkitScheduler scheduler = getServer().getScheduler();



                            //############################# Create ################################
                            timer = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                                @Override
                                public void run() {

                                    Location player1pos = lobby.get(0).getLocation();
                                    int i;
                                    for (i = 0; i < lobby.size(); i++) {
                                        if (lobby.get(i) == lobby.get(lobby.size() - 1)) {
                                            lobby.get(i).teleport(player1pos);
                                        } else {
                                            lobby.get(i).teleport(lobby.get(i+1).getLocation());
                                        }
                                    }
                                    secondsRemaining = time;
                                }
                            }, 20*time, 20*time);



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
                            sender.sendMessage("At least 2 players are required for DeathSwap!");
                        }
                    } else {
                        sender.sendMessage("Game already in progress!");
                    }
                } else {
                    sender.sendMessage("Only the host can do that!");
                }
            }
            return true;
        }
        return false;
    }
}


// ds lobby
// ds join
// ds leave
// when starting, check for min 2 players