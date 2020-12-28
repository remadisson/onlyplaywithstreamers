package de.remadisson.opws.commands;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.files;
import de.remadisson.opws.manager.StreamerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StreamerCommand implements CommandExecutor, TabCompleter {

    private final String console = files.console;
    private final String prefix = files.prefix;

    private final StreamerManager streamerManager = files.streamerManager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       /* if (!(sender instanceof Player)) {
            sender.sendMessage(console + "You are currently not capable of running this command!");
            return false;
        }*/

        if (!sender.hasPermission("opws.streamer")) {
            sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
            return false;
        }
        if (args.length == 0) {
            sendHelp(sender, 0);
            return true;
        }

        if (args.length == 1) {
            String firstArgument = args[0].toLowerCase();

            switch (firstArgument) {
                case "add":
                    sender.sendMessage(prefix + "§f - §e/streamer add <Name>");
                    break;
                case "remove":
                    sender.sendMessage(prefix + "§f - §e/streamer remove <Name>");
                    break;
                case "list":
                    files.pool.execute(() -> {
                        sendStreamerList(sender, 0);
                    });
                    break;
                case "sync":
                    if(!sender.hasPermission("opws.streamer.sync")){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }
                    sender.sendMessage(prefix + "§aYou have synchronised the local stored data! §7(Storage & Memory)");
                    streamerManager.reload();
                    return true;
                default:
                    sendHelp(sender, 0);
            }

            return true;
        }

        if (args.length == 2) {
            String firstArgument = args[0].toLowerCase();
            String secondArgument = args[1].toLowerCase();

            switch (firstArgument) {
                case "add": {
                    Player player = Bukkit.getPlayer(secondArgument);
                    UUID uuid;
                    ArrayList<UUID> streamer = streamerManager.getStreamer();

                    try {
                        uuid = player != null ? player.getUniqueId() : MojangAPI.getPlayerProfile(secondArgument).getUUID();
                    }catch(NullPointerException ex){
                        sender.sendMessage(prefix + "§e" + secondArgument + "§c does not exists!");
                        return false;
                    }

                    if (streamer.contains(uuid)) {
                        sender.sendMessage(prefix + "§5Streamers§7-§bList §calready contains §4" + secondArgument);
                        return false;
                    }

                    streamerManager.addStreamer(uuid);
                    sender.sendMessage(prefix + "§aThe Player §2" + secondArgument + "§a is now a §5Streamer!");
                    return false;
                }
                case "remove": {
                    Player player = Bukkit.getPlayer(secondArgument);
                    UUID uuid;

                    try {
                        uuid = player != null ? player.getUniqueId() : MojangAPI.getPlayerProfile(secondArgument).getUUID();
                    }catch(NullPointerException ex){
                        sender.sendMessage(prefix + "§e" + secondArgument + "§c does not exists!");
                        return false;
                    }

                    ArrayList<UUID> streamer = streamerManager.getStreamer();

                    if(!streamer.contains(uuid)){
                        sender.sendMessage(prefix + "§5Streamers§7-§bList §calready contains §4" + secondArgument);
                        return false;
                    }

                    streamerManager.removeStreamer(uuid);
                    sender.sendMessage(prefix + "§aThe Player §2" + secondArgument + "§a is §cno §5Streamer §aanymore!");
                    return false;
                }
                case "list":
                    try {
                        sendStreamerList(sender, Integer.parseInt(secondArgument));
                    }catch(NumberFormatException ex){
                        ex.printStackTrace();
                    }
                    break;
                default:
                    sendHelp(sender, 0);
            }

            return true;
        }


        return false;
    }

    public void sendHelp(CommandSender sender, int site) {
        sender.sendMessage(prefix + "§eHelp for §6/streamer");
        sender.sendMessage(prefix + "§f - §e/streamer add <Name>");
        sender.sendMessage(prefix + "§f - §e/streamer remove <Name>");
        sender.sendMessage(prefix + "§f - §e/streamer list <Site>");
    }

    public void sendStreamerList(CommandSender sender, int site) {
        ArrayList<UUID> streamer = streamerManager.getStreamer();

        int maxsites = (int) Math.ceil(streamer.size() / 6);

        if (site-1 > maxsites) {
            site = maxsites+1;
        }

        if (streamer.isEmpty()) {
            sender.sendMessage(prefix + "§cThere are currently no entries.");
            return;
        }

        if (site != 0) {
            site = site - 1;
        }

        sender.sendMessage(prefix + "§eStreamers §6Page §8[§b" + (site+1) + "§7/§e" + (maxsites+1) + "§8]");

        for (int i = 0; i < (Math.min(streamer.size() - (5*site), 5)); i++) {
            Player player = Bukkit.getPlayer(streamer.get((site * 5) + i));
            sender.sendMessage(prefix + "§f - " + (player != null ? "§a" + player.getName() : "§c" + MojangAPI.getPlayerProfile(streamer.get((site * 5) + i)).getName()) + " §7- §fWhitelist: §b" + Bukkit.getWhitelistedPlayers().contains(Bukkit.getOfflinePlayer(MojangAPI.getPlayerProfile(streamer.get((site * 5) + i)).getUUID())));
        }

        if (site != maxsites) {
            sender.sendMessage(prefix + "§e...");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> flist = new ArrayList<>();
        ArrayList<String> indexList = new ArrayList<>(Arrays.asList("add", "remove", "list"));

        if(args.length == 1){
            if(sender.hasPermission("opws.streamer")) {
                for(String s : indexList){
                    if(s.startsWith(args[0].toLowerCase())){
                        flist.add(s);
                    }
                }
            } else if(sender.hasPermission("opws.streamer.sync")){
                if("sync".startsWith(args[0].toLowerCase())){
                    flist.add("sync");
                }
            }
        }

        if(args.length == 2){
            if(sender.hasPermission("opws.streamer")) {
                if (args[0].toLowerCase().equals("add") || args[0].toLowerCase().equals("remove")) {
                    for (String name : Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())) {
                        if (name.toLowerCase().startsWith(args[1].toLowerCase())) flist.add(name);
                    }
                } else if (args[0].toLowerCase().equals("list")) {
                    int maxsites = (int) Math.ceil(streamerManager.getStreamer().size() / 6);
                    for(int i = 1; i <= maxsites; i++){
                        flist.add(String.valueOf(i));
                    }
                }
            }
        }

        return flist;
    }
}
