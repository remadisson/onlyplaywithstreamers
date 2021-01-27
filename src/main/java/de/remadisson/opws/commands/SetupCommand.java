package de.remadisson.opws.commands;

import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.arena.ArenaSetup;
import de.remadisson.opws.enums.TeamEnum;
import de.remadisson.opws.enums.WorkerState;
import de.remadisson.opws.files;
import de.remadisson.opws.main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SetupCommand implements CommandExecutor, TabCompleter {

    private String prefix = files.prefix;
    private String[] permission = {"opws.setup"};

    public static HashMap<Player, ArenaSetup> ArenaSetup = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if(!(sender instanceof Player)){
                sender.sendMessage(prefix + "§cI'm sorry, but you have to be a player to process this command!");
                return false;
            }

            if(!Arrays.stream(permission).anyMatch(sender::hasPermission)){
                sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                return false;
            }

            if(args.length == 0){
                sendHelp(sender);
                return false;
            }

            String firstArgument = args[0].toLowerCase();

            switch(firstArgument){
                case "maintenance":

                    if(args.length == 2){

                        String secondArgument = args[1].toLowerCase();

                        switch(secondArgument){
                            case "on":

                                files.maintenance = true;

                                sender.sendMessage(prefix + "§eYou have turned §aon §ethe Maintenance!");

                                return true;
                            case "off":

                                files.maintenance = false;

                                sender.sendMessage(prefix + "§eYou have turned §4off §ethe Maintenance!");

                                return true;

                            default:

                                sender.sendMessage(prefix + "§4The Argument §e'" + secondArgument +"' §4is not valid!");

                                return true;
                        }

                    } else {

                        sender.sendMessage(prefix + "§eThe Maintenance is currently§7: " + (files.maintenance ? "§aOn" : "§cOff"));

                        return true;
                    }

                case "worker":

                    if(args.length == 2){

                        String secondArgument = args[1].toLowerCase();

                        switch(secondArgument){

                            case "always":
                            case "0":

                                files.workerState = WorkerState.ALWAYS;

                                sender.sendMessage(prefix + "§bWorkers §eare now allowed to join §aalways!");

                                return true;

                            case "maintenance":
                            case "1":

                                files.workerState  = WorkerState.MAINTENANCE;

                                sender.sendMessage(prefix + "§bWorkers §eare now allowed to join on §6maintenance!");

                                return true;

                            case "opened":
                            case "2":

                                files.workerState = WorkerState.OPENED;

                                sender.sendMessage(prefix + "§bWorkers are now allowed to join when the server only is §copened!");

                                return true;

                            default:
                                sender.sendMessage(prefix + "§4The Argument §e'" + secondArgument +"' §4is not valid!");
                                return true;

                        }

                    } else {

                        sender.sendMessage(prefix + "§eWorkers Allowance§7: §b" + files.workerState.name().toUpperCase());

                        return true;
                    }

                case "arena":

                    /*
                     *
                     *  Arena:
                     *      list:
                     *          Lists all available Arenas.
                     *      create <Name>:
                     *          Creates a new Arena, but the Process is  ongoing
                     *          - Set Spawn for Viewers
                     *          - Set Spawn for Specs
                     *          - Set Spawn for Exit
                     *          - Set Spawn for Team 1
                     *          - Set Spawn for Team 2

                     *          DONE!
                     *      remove <Name>:
                     *          Removes the Arena from it's functionality.
                     *      test <Name>:
                     *          Ports the executing Player to every position and tries to assign the player to each team.
                     *
                     *
                     */


                    //sender.sendMessage(prefix + "§4This command is in development!");

                    if(args.length == 2){
                       String secondArgument = args[1].toLowerCase();

                       if(secondArgument.equalsIgnoreCase("list")){
                           HashMap<String, ArenaManager> arenas = files.arenaManager.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparing(ArenaManager::isInited))).collect(Collectors.toMap(e-> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
                           List<String> keys = new ArrayList<>(arenas.keySet());

                           Collections.reverse(keys);

                           if(arenas.isEmpty()){
                               sender.sendMessage(prefix + "§cThere are currently no entries.");
                               return false;
                           }

                           sender.sendMessage(prefix + "§aArena§eList");

                           for(String key : keys){
                               sender.sendMessage(prefix + "§f - §e" + key + " §7- §bInitiated§8: " + (arenas.get(key).isInited() ? "§atrue §8(" + arenas.get(key).getTeamList().get(TeamEnum.RED).getTeamEnum().getColor() + arenas.get(key).getTeamList().get(TeamEnum.RED).getWins() + " §8: " + arenas.get(key).getTeamList().get(TeamEnum.BLUE).getTeamEnum().getColor() + arenas.get(key).getTeamList().get(TeamEnum.BLUE).getWins() + "§8)" : "§cfalse"));
                           }

                       } else {
                           sendHelp(sender);
                       }

                    } else if(args.length == 3){
                        String secondArgument = args[1].toLowerCase();
                        String thirdArgument = args[2].toLowerCase();

                        switch(secondArgument){
                            case "create":

                                if(files.arenaManager.containsKey(thirdArgument)){
                                   sender.sendMessage(prefix + "§cAn Arena with the Name §e'" + thirdArgument + "' §4already exists!");
                                    return false;
                                }

                                if(ArenaSetup.containsKey(sender)){
                                    sender.sendMessage(prefix + "§cIDK how, but stop it!");
                                    return true;
                                }

                                ArenaSetup.put((Player) sender, new ArenaSetup(0,thirdArgument));

                                sender.sendMessage(prefix + "§6Now please read every step carefully, type 'next' for the next step:");

                                return true;
                            case "remove":

                                if(!files.arenaManager.containsKey(thirdArgument)){
                                    sender.sendMessage(prefix + "§cAn Arena with the Name §e'" + thirdArgument + "' §cdoes not exists!");
                                    return false;
                                }

                                files.arenaManager.get(thirdArgument).removeArena();
                                sender.sendMessage(prefix + "§eYou successfully removed §6" + thirdArgument + "!");

                                return true;
                            case "test":

                                if(!files.arenaManager.containsKey(thirdArgument)){
                                    sender.sendMessage(prefix + "§cAn Arena with the Name §e'" + thirdArgument + "' §cdoes not exists!");
                                    return false;
                                }

                            ArenaManager arena = files.arenaManager.get(thirdArgument);

                            sender.sendMessage(prefix + "§eTesting initiated!");

                               files.pool.execute(() -> {

                                   try {
                                       Thread.sleep(2000);
                                   } catch (InterruptedException e) {
                                       e.printStackTrace();
                                   }

                                   for(Map.Entry<String, Location> teleports : arena.getLocationsAsList().entrySet()){
                                    sender.sendMessage(prefix + "§eTeleporting to " + teleports.getKey());
                                    ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 3 ,0);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    try{
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
                                            ((Player) sender).teleport(teleports.getValue());
                                        }, 0);

                                    }catch(NullPointerException ex){
                                        sender.sendMessage(prefix + "§cCould not teleport to §4" + teleports.getKey() + "§c on §4" + thirdArgument + "§c please report this to an Admin!");
                                        ((Player) sender).playSound(((Player) sender).getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 3, 1);
                                        continue;
                                    }

                                    sender.sendMessage(prefix + "§bTeleporting successful. Going on...");
                                    ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3, 1);

                                    try {
                                        Thread.sleep(4000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                   ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
                                sender.sendMessage(prefix + "§eDone with testing locations, in the future teams will be tested too!");
                            });
                                return true;
                            case "teleport":
                            case "tp":

                                if(!files.arenaManager.containsKey(thirdArgument)){
                                    sender.sendMessage(prefix + "§cAn Arena with the Name §e'" + thirdArgument + "' §cdoes not exists!");
                                    return false;
                                }

                                sender.sendMessage(prefix + "§eTeleported to §6" + thirdArgument + "!");
                                ((Player) sender).teleport(files.arenaManager.get(thirdArgument).getViewerSpawn());
                                ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 3, 1);

                                return true;
                            default:
                                sendHelp(sender);
                        }
                    } else {
                        sendHelp(sender);
                    }

                    return true;
                default:
                    sendHelp(sender);
            }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> flist = new ArrayList<>();
        ArrayList<String> indexes = new ArrayList<>(Arrays.asList("maintenance", "worker", "arena"));
        String[] maintenance = {"on", "off"};
        String[] worker = {"always", "maintenance", "opened"};
        String[] arena = {"list", "create", "remove", "test"};

        if(args.length == 1) {
            if(sender.hasPermission(permission[0])){
                for(String s : indexes){
                    if(s.startsWith(args[0].toLowerCase())){
                        flist.add(s);
                    }
                }
            }
        }

        if(args.length == 2) {
            if (sender.hasPermission(permission[0])) {
                if (args[0].toLowerCase().equalsIgnoreCase("maintenance")) {
                    for (String s : maintenance) {
                        if (s.startsWith(args[1].toLowerCase())) {
                            flist.add(s);
                        }
                    }
                } else if (args[0].toLowerCase().equalsIgnoreCase("worker")) {
                    for (String s : worker) {
                        if (s.startsWith(args[1].toLowerCase())) {
                            flist.add(s);
                        }
                    }
                } else if(args[0].equalsIgnoreCase("arena")){
                    for (String s : arena){
                        if(s.startsWith(args[1].toLowerCase())){
                            flist.add(s);
                        }
                    }
                }
            }

        }

        if(args.length == 3){
            if(sender.hasPermission(permission[0])){
                if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("test")){
                    for(String keys : files.arenaManager.keySet()){
                        if(keys.startsWith(args[2].toLowerCase())){
                            flist.add(keys);
                        }
                    }
                }
            }
        }

        return flist;
    }

    public void sendHelp(CommandSender sender) {
            sender.sendMessage(prefix + "§eHelp for §6/setup");
            sender.sendMessage(prefix + "§f - §e/setup maintenance <on/off>");
            sender.sendMessage(prefix + "§f - §e/setup worker <always/maintenance/opened>");
            sender.sendMessage(prefix + "§f - §e/setup arena <list/create/remove/test> <Name>");


    }
}
