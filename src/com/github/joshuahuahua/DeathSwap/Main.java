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
                sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$e--------- $fHelp: Index (1/1) $e---------------------"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$6Aliases: $f/deathswap, /ds"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$6/ds create: $fCreates a new DeathSwap Lobby"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$6/ds timeset: $fSet swap time (minutes)"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$6/ds join: $fJoins available DeathSwap lobby"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$6/ds leave: $fLeaves current lobby"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$6/ds start: $fStarts DeathSwap match"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$6/ds stop: $fStops DeathSwap match"));
            }



            //############################## /ds timeset <num> ################################
            if (args.length == 2 && args[0].equals("timeset")) {
                if (sender.getName().equals(host.getName())) {
                    if (!isRunning) {
                        time = Integer.parseInt(args[1])*60;
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"Set time to$e " + time/60));
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$cYou can not change the game mid-game!"));
                    }
                } else {
                    sender.sendMessage("$4Only the host can do that!");
                }
            }


            //############################## /ds create ################################
            if (args.length == 1 && (args[0].equalsIgnoreCase("create")) || args[0].equalsIgnoreCase("host")) {
                if (host == null) {
                    host = (Player) sender;
                    lobby.add(host);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$a$l" + host.getName() + " $r$fis hosting $cDeathSwap!"));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$dUse /ds join to join!"));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$c$l" + host.getName() + " $r$cis already hosting DeathSwap!"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$dUse /ds join to join!"));
                }
            }



            //############################## /ds join ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("join")) {
                if (host != null) {
                    if (!lobby.contains((Player) sender)) {
                        lobby.add((Player) sender);
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$6" + sender.getName() + " $ahas joined the lobby!"));
                        sender.sendMessage("$lCurrent players in your lobby:");
                        for (Player player : lobby) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$6" + player.getName()));
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$cYou are already in a lobby!"));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$cThere are no available games to join!"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$6Use /ds create to create one!"));
                }
            }



            //############################## /ds leave ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                if (lobby.contains((Player) sender)) {
                    lobby.remove((Player) sender);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$6" + sender.getName() + " $chas left the lobby!"));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"You are not in a lobby!"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$6Use /ds create to create one or /ds join to join an existing one!"));
                }
                if (sender.getName().equals(host.getName())) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$4" + host.getName() + " $chas closed the lobby!"));
                    for (Player player : lobby) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('$',"$cYou have been removed from the lobby!"));
                    }
                    host = null;
                    lobby.clear();
                }
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
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$cNo active Death Swap!"));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('$', "$cOnly the host can do that!"));
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
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$4Swap time minutes:$c$l " + time));
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
                                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$', "$c$l" + i+1 + " $r$4Seconds remaining!"));
                                        }
                                    }
                                }
                            }, 0, 20);
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$cAt least 2 players are required for DeathSwap!"));
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$cGame already in progress!"));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('$',"$cOnly the host can do that!"));
                }
            }
            return true;
        }
        return false;
    }
}