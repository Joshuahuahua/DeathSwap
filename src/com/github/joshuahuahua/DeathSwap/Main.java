package com.github.joshuahuahua.DeathSwap;

// IMPORTS
import com.github.joshuahuahua.DeathSwap.files.customConfig;
import com.github.joshuahuahua.DeathSwap.listeners.onDeath;
import com.github.joshuahuahua.DeathSwap.listeners.onJoin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;


public class Main extends JavaPlugin {


    // VARS
    // Time should be seconds + minutes + hours
    int time = 60;
    static int timer;
    static int countDown;
    int secondsRemaining;
    public static boolean isRunning;
    Player host = null;
    public static List<Player> lobby = new ArrayList<>();



    @Override
    public void onEnable() {


        PluginManager pluginManager  = getServer().getPluginManager();
        pluginManager.registerEvents(new onJoin(this), this);
        pluginManager.registerEvents(new onDeath(this), this);


        getLogger().info("DeathSwap Enabled");


        // Config File (Please neaten this up)

        //Setup config

        //getConfig().options().copyDefaults();
        //saveDefaultConfig();
        customConfig.setup();
        customConfig.get().addDefault("messageOnJoin", true);
        customConfig.get().addDefault("time_seconds", 4);
        customConfig.get().addDefault("time_mins", 4);
        customConfig.get().addDefault("time_hours", 4);

        //time input should be as follows;
        // 10 == 10 seconds
        // 1:10 == 1min 10 seconds
        // 1:10:10 == 1hour 1min 10 seconds
        // 1:10:10:10 == INVALID
        customConfig.get().options().copyDefaults(true);
        customConfig.save();

        // assign var from config
        //int time = Integer.parseInt(Objects.requireNonNull(customConfig.get().getString("time_seconds")));


    }

    @Override
    public void onDisable() {
        getLogger().info("DeathSwap Disabled");
    }

    //######################################## On command #########################################################

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("DeathSwap") || label.equalsIgnoreCase("ds")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Error. Please use chat!");
                return true;
            }

            //################################# /ds reload ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                customConfig.reload();
                message.sender(sender, "$a/ds start: $fStarts DeathSwap match");
            }


            //################################# /ds help ################################
            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                message.sender(sender,"$e--------- $fHelp: Index (1/1) $e---------------------");
                message.sender(sender,"$6Aliases: $f/deathswap, /ds");
                message.sender(sender,"$6/ds create: $fCreates a new DeathSwap Lobby");
                message.sender(sender,"$6/ds timeset: $fSet swap time (minutes)");
                message.sender(sender,"$6/ds join: $fJoins available DeathSwap lobby");
                message.sender(sender,"$6/ds leave: $fLeaves current lobby");
                message.sender(sender,"$6/ds start: $fStarts DeathSwap match");
                message.sender(sender,"$6/ds stop: $fStops DeathSwap match");
            }


            //############################## /ds timeset <num> ################################
            if (args.length == 2 && args[0].equalsIgnoreCase("timeset")) {
                if (sender.getName().equals(host.getName())) {
                    if (!isRunning) {
                        time = Integer.parseInt(args[1])*60;
                        message.sender(sender,"Set time to$e " + time/60);
                    } else {
                        message.sender(sender,"$cYou can not change the game mid-game!");
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
                    message.global("$a$l" + host.getName() + " $r$fis hosting $cDeathSwap!");
                    message.global("$dUse /ds join to join!");
                } else {
                    message.sender(sender,"$c$l" + host.getName() + " $r$cis already hosting DeathSwap!");
                    message.sender(sender,"$dUse /ds join to join!");
                }
            }



            //############################## /ds join ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("join")) {
                if (host != null) {
                    if (!lobby.contains((Player) sender)) {
                        lobby.add((Player) sender);
                        message.global("$6" + sender.getName() + " $ahas joined the lobby!");
                        message.sender(sender,"$lCurrent players in your lobby:");
                        for (Player player : lobby) {
                            message.sender(sender,"$6" + player.getName());
                        }
                    } else {
                        message.sender(sender,"$cYou are already in a lobby!");
                    }
                } else {
                    message.sender(sender,"$cThere are no available games to join!");
                    message.sender(sender,"$6Use /ds create to create one!");
                }
            }



            //############################## /ds leave ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                if (lobby.contains((Player) sender)) {
                    lobby.remove((Player) sender);
                    message.global("$6" + sender.getName() + " $chas left the lobby!");
                } else {
                    message.sender(sender,"You are not in a lobby!");
                    message.sender(sender,"$6Use /ds create to create one or /ds join to join an existing one!");
                }
                if (sender.getName().equals(host.getName())) {
                    message.global("$4" + host.getName() + " $chas closed the lobby!");
                    for (Player player : lobby) {
                        message.player(player,"$cYou have been removed from the lobby!");
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
                        stopSchedulers();
                        message.global("$c$lDeath Swap Stopped");
                    } else {
                        message.sender(sender,"$cNo active Death Swap!");
                    }
                } else {
                    message.sender(sender,"$cOnly the host can do that!");
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

                            message.global("$c$lDeath Swap Started");
                            message.global("$4Swap time minutes:$c$l " + time);
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
                                            message.global("$c$l" + i+1 + " $r$4Seconds remaining!");
                                        }
                                    }
                                }
                            }, 0, 20);
                        } else {
                            message.sender(sender,"$cAt least 2 players are required for DeathSwap!");
                        }
                    } else {
                        message.sender(sender,"$cGame already in progress!");
                    }
                } else {
                    message.sender(sender,"$cOnly the host can do that!");
                }
            }
            return true;
        }
        return false;
    }

    // Stops the timers. Used in /ds stop and in the onDeath listener. Not really needed but neatens up code a bit.
    public static void stopSchedulers() {
        Bukkit.getScheduler().cancelTask(timer);
        Bukkit.getScheduler().cancelTask(countDown);
    }
}