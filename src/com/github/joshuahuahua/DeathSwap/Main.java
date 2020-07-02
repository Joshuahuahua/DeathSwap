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

    Player player1 = null;
    Player player2 = null;
    int time = 1;
    int timer;
    int countDown;
    int secondsRemaining;
    boolean isRunning;
    List<Player> players = new ArrayList<>();


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
            for (i = 0; i < players.size(); i++) {
                if (players.get(i).getName().equals(event.getEntity().getName())) {
                    event.getEntity().setGameMode(GameMode.SPECTATOR);
                    players.remove(i);
                }
            }
            if (players.size() == 1) {
                isRunning = false;
                Bukkit.getScheduler().cancelTask(timer);
                Bukkit.getScheduler().cancelTask(countDown);

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$lDeath Swap has ended"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$a" + players.get(0).getName() + " $fis the winner!"));

                players.get(0).sendMessage(ChatColor.translateAlternateColorCodes('$',"$d$lYou are the winner!"));
                players.get(0).sendMessage(ChatColor.translateAlternateColorCodes('$',"$dHave some cake :)"));
                players.get(0).getInventory().clear();
                players.get(0).getInventory().addItem(new ItemStack(Material.CAKE, 1));
            } else {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$l" + event.getEntity().getName() + " has died!"));
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('$',"$c$l" + players.size() + " players remaining."));
            }
        }
    }

    //######################################## On command #########################################################

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("DeathSwap") || label.equalsIgnoreCase("ds")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("BRUH");
            }

            //################################# /ds ################################
            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                sender.sendMessage("Usage: ds set <player1> <player2>...");
                sender.sendMessage("Usage: ds clear");
                sender.sendMessage("Usage: ds timeset <num>");
                sender.sendMessage("Usage: ds start");
                sender.sendMessage("Usage: ds stop");
            }



            //############################# 1 perameter ##############################

            //############################## /ds stop ################################

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

            //############################### /ds clear ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                players.clear();
                sender.sendMessage("Cleared players");
            }


            //############################# 2 perameters ################################

            //############################## /ds player/p <num> ################################
            if (args.length == 2 && (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("p"))) {
                if (Integer.parseInt(args[1]) > players.size()) {
                    sender.sendMessage("That player has not been set.");
                } else {
                    sender.sendMessage("Set player " + args[1] + " to " + players.get(Integer.parseInt(args[1])-1).getName());
                }
            }

            //############################## /ds timeset <num> ################################
            if (args.length == 2) {
                if (args[0].equals("timeset")) {
                    time = Integer.parseInt(args[1]);
                    sender.sendMessage("Set time to " + time);
                }
            }

            //############################# 2+ perameters ################################

            //############################## /ds set <player1> <player2>... ################################

            if (args[0].equals("set")) {
                if (args.length > 2) {
                    if (!isRunning) {
                        players.clear();
                        int i;
                        for (i = 1; i < args.length; i++) {

                            players.add(Bukkit.getPlayerExact(args[i]));
                            sender.sendMessage("Set player " + i + " to " + players.get(i - 1).getName());
                            players.get(i - 1).sendMessage("You have been set to player " + i);
                        }
                        sender.sendMessage("players: " + players);
                    }
                } else {
                    sender.sendMessage("At least 2 players required!");
                }

            }


            //############################# START ################################
            if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
                if (!isRunning) {

                    isRunning = true;
                    secondsRemaining = time*60;

                    World world = Bukkit.getServer().getWorld("world");
                    assert world != null;
                    world.setTime(0);

                    //############################# Init Players ################################
                    for (Player player : players) {
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

                            Location player1pos = players.get(0).getLocation();
                            int i;
                            for (i = 0; i < players.size(); i++) {
                                if (players.get(i) == players.get(players.size() - 1)) {
                                    players.get(i).teleport(player1pos);
                                } else {
                                    players.get(i).teleport(players.get(i+1).getLocation());
                                }
                            }
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