package com.github.joshuahuahua.DeathSwap;

// IMPORTS
import com.github.joshuahuahua.DeathSwap.files.customConfig;
import com.github.joshuahuahua.DeathSwap.listeners.onDeath;
import com.github.joshuahuahua.DeathSwap.listeners.onJoin;
import com.github.joshuahuahua.DeathSwap.listeners.onLeave;
import com.github.joshuahuahua.DeathSwap.listeners.playerClickInventory;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    public static Player host = null;
    public static List<Player> lobby = new ArrayList<>();

    // Game Rules
    public static boolean autoSmelt = false;



    @Override
    public void onEnable() {


        PluginManager pluginManager  = getServer().getPluginManager();
        pluginManager.registerEvents(new onJoin(this), this);
        pluginManager.registerEvents(new onDeath(this), this);
        pluginManager.registerEvents(new onLeave(this), this);
        pluginManager.registerEvents(new playerClickInventory(), this);



        // Config File (Please neaten this up)

        //Setup config

        //getConfig().options().copyDefaults();
        //saveDefaultConfig();
        customConfig.setup();
        customConfig.get().addDefault("messageOnJoin", true);
        customConfig.get().addDefault("prefix", "$b$lDeathSwap$r$8> $7");
        customConfig.get().addDefault("time", "1:00");

        //time input should be as follows;
        // 10 == 10 seconds
        // 1:10 == 1min 10 seconds
        // 1:10:10 == INVALID
        customConfig.get().options().copyDefaults(true);
        customConfig.save();

        // assign var from config
        //int time = Integer.parseInt(Objects.requireNonNull(customConfig.get().getString("time_seconds")));


        getLogger().info("DeathSwap Enabled");
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
                message.sender(sender, "$aReload complete");
                return true;
            }


            if (args.length == 1 && args[0].equalsIgnoreCase("test")) {
                Inventory gamemodesInv = Bukkit.createInventory(null, 9, "Gamemodes");
                gamemodesInv.setItem(0, createItem(Material.GRASS_BLOCK, ChatColor.RED + "Default", "Default gamemode"));
                gamemodesInv.setItem(1, createItem(Material.SUGAR, "Speed", "Sets time to 60 seconds"));
                Player player = (Player) sender;
                player.openInventory(gamemodesInv);
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("test2")) {
                Inventory testInv = Bukkit.createInventory(null, 9, "Test");
                testInv.setItem(0, createItem(Material.GRASS_BLOCK, "Test1", "Default gamemode"));
                testInv.setItem(1, createItem(Material.SUGAR, "Test2", "Sets time to 60 seconds"));
                Player player = (Player) sender;
                player.openInventory(testInv);
                return true;
            }


            //################################# /ds help ################################
            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                message.sender(sender,"$e------------ $fHelp: Index (1/1) $e------------");
                message.sender(sender,"$6Aliases: $f/deathswap, /ds");
                message.sender(sender,"$6/ds create: $fCreates a new DeathSwap Lobby");
                message.sender(sender,"$6/ds timeset: $fSet swap time (minutes)");
                message.sender(sender,"$6/ds join: $fJoins available DeathSwap lobby");
                message.sender(sender,"$6/ds leave: $fLeaves current lobby");
                message.sender(sender,"$6/ds start: $fStarts DeathSwap match");
                message.sender(sender,"$6/ds stop: $fStops DeathSwap match");
                return true;
            }

            //############################## /ds timeset <num> ################################
            if (args.length == 2 && args[0].equalsIgnoreCase("timeset")) {
                if (host != null) {
                    if (sender.getName().equals(host.getName())) {
                        if (!isRunning) {
                            time = Integer.parseInt(args[1])*60;
                            message.sender(sender,"Set time to$e " + time/60);
                        } else {
                            message.sender(sender,"$cYou can not change the game mid-game!");
                        }
                    } else {
                        sender.sendMessage("$cOnly the host can do that!");
                    }
                } else {
                    message.sender(sender,"$cThere are no available lobbies!");
                    message.sender(sender,"Use /ds create to host a lobby!");
                }
                return true;
            }


            //############################## /ds create ################################
            if (args.length == 1 && (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("host"))) {
                if (host == null) {
                    host = (Player) sender;
                    lobby.add(host);
                    message.global("$a$l" + host.getName() + " $r$7is has created a lobby!");
                    message.global("Use /ds join to join!");
                } else {
                    message.sender(sender,"$a$l" + host.getName() + " $r$cis already hosting DeathSwap!");
                    message.sender(sender,"Use /ds join to join!");
                }
                return true;
            }


            //############################## /ds join ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("join")) {
                if (host != null) {
                    if (!lobby.contains((Player) sender)) {
                        lobby.add((Player) sender);
                        message.global("$a$l" + sender.getName() + " $r$7has joined the lobby!");
                    } else {
                        message.sender(sender,"$cYou are already in a lobby!");
                    }
                } else {
                    message.sender(sender,"$cThere are no available lobbies!");
                    message.sender(sender,"Use /ds create to host a lobby!");
                }
                return true;
            }

            //############################## /ds list ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                if (lobby.contains((Player) sender)) {
                    message.sender(sender,"$lCurrent players in your lobby:");
                    for (Player player : lobby) {
                        message.sender(sender, player.getName());
                    }
                } else {
                    message.sender(sender,"$cYou are not in a lobby!");
                    message.sender(sender,"Use /ds create to create one or /ds join to join an existing one!");
                }
            }

            //############################## /ds leave ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                if (host != null) {
                    if (sender.getName().equals(host.getName())) {
                        message.global("$a$l" + host.getName() + " $r$7has closed the lobby!");
                        for (Player player : lobby) {
                            message.player(player,"$cYou have been removed from the lobby!");
                        }
                        host = null;
                        lobby.clear();
                    }
                    if (lobby.contains((Player) sender)) {
                        lobby.remove((Player) sender);
                        message.global("$a$l" + sender.getName() + " $r$7has left the lobby!");
                    } else {
                        message.sender(sender,"$cYou are not in a lobby!");
                        message.sender(sender,"Use /ds create to create one or /ds join to join an existing one!");
                    }
                } else {
                    message.sender(sender,"$cThere are no available lobbies!");
                    message.sender(sender,"Use /ds create to host a lobby!");
                }
                return true;
            }



            //############################## /ds stop ################################

            if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                if (host != null) {
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
                return true;
            }


            //############################# START ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
                if (host != null) {
                    if (sender.getName().equals(host.getName())) {
                        if (!isRunning) {
                            if (lobby.size() > 1) {
                                isRunning = true;
                                secondsRemaining = time;

                                World world = Bukkit.getServer().getWorld("world");
                                assert world != null;
                                world.setTime(0);

                                //############################# Init Players ###################
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

                                int divTime = time/60;
                                message.global("$a$lDeathSwap Started");
                                for (Player player : lobby) {
                                    player.sendTitle(ChatColor.translateAlternateColorCodes('$', "$a$lDeathSwap Started"), ChatColor.translateAlternateColorCodes('$',"$aSwap time minutes:$c$l " + divTime), 10,40,10);
                                }
                                BukkitScheduler scheduler = getServer().getScheduler();



                                //############################# Create ################################
                                timer = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
                                    @Override
                                    public void run() {
                                        for (Player player : lobby) {
                                            player.sendTitle(ChatColor.translateAlternateColorCodes('$', "$b$lSwap!"), "", 5,10,5);
                                        }
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
                                        for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}) {
                                            if (i == secondsRemaining) {
                                                int addedSeconds = i+1;
                                                message.global("$a$l" + addedSeconds + " $r$7seconds remaining!");
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
                } else {
                    message.sender(sender,"$cThere is no one hosting DeathSwap!");
                    message.sender(sender,"Use /ds create to host a lobby!");
                }
                return true;
            }
            message.sender(sender,"$cInvalid command!");
            message.sender(sender,"Use /ds help for a list of available commands.");
            return true;
        } else {
            return false;
        }
    }

    // Stops the timers. Used in /ds stop and in the onDeath listener. Not really needed but neatens up code a bit.
    public static void stopSchedulers() {
        Bukkit.getScheduler().cancelTask(timer);
        Bukkit.getScheduler().cancelTask(countDown);
    }

    public ItemStack createItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> itemLore = new ArrayList<String>();
        itemLore.add(lore);
        meta.setLore(itemLore);
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;

    }
}
