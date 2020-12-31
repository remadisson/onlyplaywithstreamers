package de.remadisson.opws.commands;

import de.remadisson.opws.files;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameModeCommand implements CommandExecutor, TabExecutor {

    private final String prefix = files.prefix;
    private final String permission = "minecraft.command.gamemode";


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "§cAre you dumb?");
            return false;
        }

        if (!sender.hasPermission(permission)) {
            sender.sendMessage(prefix + "§cYou do not have Permission to execute this command!");
            return false;
        }

        if(args.length == 0) {
            sendHelp(sender);
        }

        if(args.length == 1) {
            String firstArgument = args[0].toLowerCase();
            Player player = (Player) sender;
            switch(firstArgument){
                case "survival":
                case "0":
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(prefix + "§eYour new gamemode is " + player.getGameMode());
                    return true;
                case "creative":
                case "1":
                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(prefix + "§eYour new gamemode is " + player.getGameMode());
                    return true;
                case "adventure":
                case "2":
                    player.setGameMode(GameMode.ADVENTURE);
                    player.sendMessage(prefix + "§eYour new gamemode is " + player.getGameMode());
                    return true;
                case "spectator":
                case "3":
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(prefix + "§eYour new gamemode is " + player.getGameMode());
                    return true;
            }
        }

        if(args.length == 2) {
            String firstArgument = args[0].toLowerCase();
            String secondArgument = args[1].toLowerCase();

            Player target = Bukkit.getPlayer(secondArgument);

            if(target == null){
                sender.sendMessage(prefix + "§cThe Player §4" + secondArgument + " §cis not online!");
                return false;
            }

            if(target == sender){
                sender.sendMessage(prefix + "§cThis operation is not allowed.");
                return false;
            }

            switch(firstArgument){
                case "survival":
                case "0":
                    target.setGameMode(GameMode.SURVIVAL);
                    target.sendMessage(prefix + "§eYour new gamemode is " + target.getGameMode());
                    sender.sendMessage(prefix + "§eThe new gamemode of §a" + secondArgument + "§e is §a" + target.getGameMode());
                    return true;
                case "creative":
                case "1":
                    target.setGameMode(GameMode.CREATIVE);
                    target.sendMessage(prefix + "§eYour new gamemode is " + target.getGameMode());
                    sender.sendMessage(prefix + "§eThe new gamemode of §a" + secondArgument + "§e is §a" + target.getGameMode());
                    return true;
                case "adventure":
                case "2":
                    target.setGameMode(GameMode.ADVENTURE);
                    target.sendMessage(prefix + "§eYour new gamemode is " + target.getGameMode());
                    sender.sendMessage(prefix + "§eThe new gamemode of §a" + secondArgument + "§e is §a" + target.getGameMode());
                    return true;
                case "spectator":
                case "3":
                    target.setGameMode(GameMode.SPECTATOR);
                    target.sendMessage(prefix + "§eYour new gamemode is " + target.getGameMode());
                    sender.sendMessage(prefix + "§eThe new gamemode of §a" + secondArgument + "§e is §a" + target.getGameMode());
                    return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> flist = new ArrayList<>();
        List<String> gamemodes = new ArrayList<>(Arrays.asList("0", "1", "2", "3", "survival", "creative", "adventure", "spectator"));
        List<Player> players = new ArrayList<>();
        players.addAll(Bukkit.getOnlinePlayers());

        if (args.length == 1) {
            for (String gamemode : gamemodes) {
                if (sender.hasPermission("minecraft.command.gamemode")){
                    if (gamemode.startsWith(args[0].toLowerCase())) {
                        flist.add(gamemode);
                    }
                }
            }
        }

        if (args.length == 2) {
            if(sender.hasPermission("mincraft.command.gamemode"))
                for (Player player : players) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        flist.add(player.getName());
                    }
                }
        }

        return flist;
    }

    public String getGameModeName(String gamemode){
        String args0 = gamemode;
        if (gamemode.equals("0")) {
            args0 = "survival";
        } else if (gamemode.equals("1")) {
            args0 = "creative";
        } else if (gamemode.equals("2")) {
            args0 = "adventure";
        } else if (gamemode.equals("3")) {
            args0 = "spectator";
        }
        return args0;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(prefix + "§eHelp for §6/gamemode");
        sender.sendMessage(prefix + "§f - §e/gamemode <0/1/2/3>");
        sender.sendMessage(prefix + "§f - §e/gamemode <0/1/2/3> <Player>");
    }

}