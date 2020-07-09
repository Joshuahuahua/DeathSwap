package com.github.joshuahuahua.DeathSwap;

// IMPORTS
import com.github.joshuahuahua.DeathSwap.files.customConfig;
import com.github.joshuahuahua.DeathSwap.listeners.OnDeath;
import com.github.joshuahuahua.DeathSwap.listeners.OnJoin;
import com.github.joshuahuahua.DeathSwap.listeners.OnLeave;
import com.github.joshuahuahua.DeathSwap.listeners.PlayerClickInventory;
import com.github.joshuahuahua.DeathSwap.listeners.AutoSmelt;
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

import java.util.*;

public class Main extends JavaPlugin {

    // VARS
    // Time should be seconds + minutes + hours
    public static int time = 5*60;
    static int timer;
    static int countDown;
    int secondsRemaining;
    public static boolean isRunning;
    public static Player host = null;
    public static List<Player> lobby = new ArrayList<>();
    public static String gamemode = "Default";


    // Game Rules
    public static HashMap<String, Boolean> getGameRules() {
        HashMap<String, Boolean> gameRules = new HashMap<String, Boolean>();
        gameRules.put("autoSmelt", false);
        gameRules.put("speed", false);
        gameRules.put("countDown", true);
        return gameRules;
    }
    public static HashMap<String, Boolean> gameRules = Main.getGameRules();



    @Override
    public void onEnable() {

        PluginManager pluginManager  = getServer().getPluginManager();
        pluginManager.registerEvents(new OnJoin(this), this);
        pluginManager.registerEvents(new OnDeath(this), this);
        pluginManager.registerEvents(new OnLeave(this), this);
        pluginManager.registerEvents(new PlayerClickInventory(), this);
        pluginManager.registerEvents(new AutoSmelt(this), this);

        //time input should be as follows;
        // 10 == 10 seconds
        // 1:10 == 1min 10 seconds
        // 1:10:10 == INVALID

        //Setup config
        customConfig.setup();
        customConfig.get().addDefault("messageOnJoin", true);
        customConfig.get().addDefault("prefix", "$b$lDeathSwap$r$8> $7");
        customConfig.get().addDefault("time", "1:00");

        customConfig.get().options().copyDefaults(true);
        customConfig.save();





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
            if (args.length == 1 && args[0].equalsIgnoreCase("test")) {
                for (Map.Entry<String, Boolean> gameRule : gameRules.entrySet()) {
                    message.global(gameRule.getKey() + " / " + gameRule.getValue());
                }
                return true;
            }


            //################################# /ds reload ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                customConfig.reload();
                message.sender(sender, "$aReload complete");
                return true;
            }


            //################################# /ds gamemode/gamerule ################################
            if (args.length == 1 && (args[0].equalsIgnoreCase("gamemode") || args[0].equalsIgnoreCase("gm"))) {
                if (host != null) {
                    if (sender.getName().equals(host.getName())) {
                        if (!isRunning) {
                            selectInv((Player) sender, "gamemodeInv");
                        } else {
                            message.sender(sender,"$cYou can not change this mid-game!");
                        }
                    } else {
                        message.sender(sender,"$cOnly the host can do that!");
                    }
                } else {
                    message.sender(sender,"$cOnly a host can do that!");
                }
                return true;
            }
            if (args.length == 1 && (args[0].equalsIgnoreCase("gamerule") || args[0].equalsIgnoreCase("gr"))) {
                if (host != null) {
                    if (sender.getName().equals(host.getName())) {
                        if (!isRunning) {
                            selectInv((Player) sender, "gameruleInv");
                        } else {
                            message.sender(sender,"$cYou can not change this mid-game!");
                        }
                    } else {
                        message.sender(sender,"$cOnly the host can do that!");
                    }
                } else {
                    message.sender(sender,"$cOnly a host can do that!");
                }
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
                message.sender(sender,"$6/ds query: $fReturns swap time remaining");
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
                            message.sender(sender,"$cYou can not change this mid-game!");
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
                    if (!isRunning) {
                        if (!lobby.contains((Player) sender)) {
                            lobby.add((Player) sender);
                            message.global("$a$l" + sender.getName() + " $r$7has joined the lobby!");
                        } else {
                            message.sender(sender,"$cYou are already in a lobby!");
                        }
                    } else {
                        message.sender(sender,"$cThere is already a game in progress!");
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
                return true;
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
                        return true;
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
                            PlayerClickInventory.defaultGamemode();
                            for(Player player: Bukkit.getOnlinePlayers()) {
                                Main.gameRuleInit(player);
                            }
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
                                    gameRuleInit(player);


                                    double playerX = Math.random() * ( 10000 - -10000 );
                                    double playerY = Math.random() * ( 10000 - -10000 );
                                    Location startCoord = new Location(world,playerX,200,playerY);
                                    player.teleport(startCoord);
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,8*20,200, false,false,false));
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
                                        if (gameRules.get("countDown")) {
                                            for (int i : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}) {
                                                if (i == secondsRemaining) {
                                                    int addedSeconds = i+1;
                                                    message.global("$a$l" + addedSeconds + " $r$7seconds remaining!");
                                                }
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


            //############################## /ds query ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("query")) {
                if (host != null) {
                    if (sender.getName().equals(host.getName())) {
                        if (isRunning) {
                            if (secondsRemaining > 60) {
                                message.sender(sender, "Time remaining: " + secondsRemaining/60 + " minutes.");
                            } else {
                                message.sender(sender, "Time remaining: " + secondsRemaining + " seconds.");
                            }
                        } else {
                            message.sender(sender,"$cNo active Death Swap!");
                        }
                    } else {
                        message.sender(sender,"$cOnly the host can do that!");
                    }
                } else {
                    message.sender(sender,"$cNo active Death Swap!");
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

    public static ItemStack createItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> itemLore = new ArrayList<String>();
        itemLore.add(lore);
        meta.setLore(itemLore);
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;

    }

    public static ItemStack gameruleItem(Boolean gamerule, String gameruleName) {
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ArrayList<String> itemLore = new ArrayList<String>();

        if (gamerule) {
            item = new ItemStack(Material.EMERALD_BLOCK);
            itemLore.add("Enabled");
        } else {
            itemLore.add("Disabled");
        }
        ItemMeta meta = item.getItemMeta();
        meta.setLore(itemLore);
        meta.setDisplayName(gameruleName);

        item.setItemMeta(meta);
        return item;
    }

    public static void selectInv(Player player, String inventory) {

        if (inventory.equalsIgnoreCase("gamemodeInv")) {
            Inventory gamemodeInv = Bukkit.createInventory(null, 9, "Gamemodes");

            gamemodeInv.addItem(createItem(Material.GRASS_BLOCK, "Default", "Default gamemode"));
            gamemodeInv.addItem(createItem(Material.SUGAR, "Speed", "Sets time to 60 seconds"));
            gamemodeInv.addItem(createItem(Material.ENDER_EYE, "Blind", "Removes the countdown"));

            player.openInventory(gamemodeInv);
        }

        if (inventory.equalsIgnoreCase("gameruleInv")) {


            Inventory gameruleInv = Bukkit.createInventory(null, (int) Math.ceil(gameRules.size()/9.0)*9, "Gamerules");


            for (Map.Entry<String, Boolean> gameRule : gameRules.entrySet()) {
                gameruleInv.addItem(gameruleItem(gameRule.getValue(), gameRule.getKey()));
            }

            player.openInventory(gameruleInv);
        }
    }

    public static void gameRuleInit(Player player) {

        if (gameRules.get("speed")) {
            player.setWalkSpeed(0.3f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,999,2,false,false,false));
        } else {
            player.setWalkSpeed(0.2f);
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
        }
    }
}