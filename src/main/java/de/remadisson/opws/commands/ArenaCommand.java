package de.remadisson.opws.commands;

import de.remadisson.opws.enums.ArenaState;
import de.remadisson.opws.enums.TeamEnum;
import de.remadisson.opws.events.ArenaScoreboardUpdateEvent;
import de.remadisson.opws.files;
import de.remadisson.opws.arena.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArenaCommand implements TabExecutor {

    private final String prefix = files.prefix;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "§cYou are no player!");
            return false;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (args.length == 1 || args.length == 2) {
            String firstArgument = args[0].toLowerCase();

            switch (firstArgument) {
                case "leave": {
                   if(args.length != 1){
                       sendHelp(sender);
                       return false;
                   }

                    if (!ArenaManager.containsPlayer(uuid)) {
                        player.sendMessage(prefix + "§cDu bist derzeit in keiner Arena!");
                        return false;
                    }

                    ArenaManager arena = ArenaManager.getPlayerArena(uuid);
                    ArenaPlayer arenaPlayer = ArenaManager.getArenaPlayer(uuid);
                    assert arena != null;
                    if(arenaPlayer.getTeam() == TeamEnum.SPECTATOR){
                        arena.removeViewer(player, false);
                        return true;
                    }

                    arena.removePlayer(player, false);
                    player.sendMessage(prefix + "§7Du hast die Arena §a" + arena.getName() + " §7verlassen!");

                    return false;
                }
                case "accept": {
                    if(args.length != 1){
                        sendHelp(sender);
                        return false;
                    }

                    if (!ArenaManager.containsPlayer(uuid)) {
                        player.sendMessage(prefix + "§cDu bist derzeit in keiner Arena!");
                        return false;
                    }

                    ArenaManager arena = ArenaManager.getPlayerArena(uuid);
                    assert arena != null;

                    ArenaPlayer arenaPlayer = ArenaManager.getArenaPlayer(uuid);

                    if(arenaPlayer.getTeam() == TeamEnum.SPECTATOR){
                        player.sendMessage(prefix + "§cDu bist Zuschauer, kein Mitwirkender!");
                        return false;
                    }

                    if(!arena.getArenaState().equals(ArenaState.LOBBY)){
                        player.sendMessage(prefix + "§7Die Arena ist bereits im FIGHT!");
                        return false;
                    }

                    if(arenaPlayer.isReady()){
                        player.sendMessage(prefix + "§cDu hast diese Arena bereits angenommen!");
                        return false;
                    }

                    arenaPlayer.setReady(true);
                    player.sendMessage(prefix + "§aDu hast dieses Match angenommen!");
                    arena.sendFightersMessage(arenaPlayer.getTeam().getColor() + player.getName() + "§7 hat die Arena mit §a/arena accept §7angenommen!");
                    Bukkit.getPluginManager().callEvent(new ArenaScoreboardUpdateEvent(arena));
                    return true;
                }

                case "view":{
                    if(ArenaManager.containsPlayer(uuid)){
                        sender.sendMessage(prefix + "§cDu bist bereits in einer Arena!");
                        return true;
                    }

                    if(!files.allowArenaFight && !player.isOp() && !ArenaManager.infiniteAllowedPlay.contains(player.getUniqueId())){
                        player.sendMessage(prefix + "§cArenen sind momentan deaktiviert!");
                        return true;
                    }

                    if(args.length != 2){
                        sendHelp(sender);
                        return false;
                    }

                    String arenaName = args[1].toLowerCase();

                    if(!files.arenaManager.containsKey(arenaName)){
                        sender.sendMessage(prefix + "§cDie Arena mit dem Namen §e" + arenaName.toUpperCase() + " §cexistiert nicht.");
                        return false;
                    }

                    ArenaManager arenaManager = files.arenaManager.get(arenaName);
                    arenaManager.addViewer(((Player) sender), TeamEnum.SPECTATOR);
                    sender.sendMessage(prefix + "§7Du schaust nun §b" + arenaName.toUpperCase());
                    return true;
                }

                case "list":{
                    if(args.length != 1){
                        sendHelp(sender);
                        return false;
                    }

                    ArrayList<String> arenaNames = new ArrayList<>(files.arenaManager.keySet());

                    if(arenaNames.isEmpty()){
                        sender.sendMessage(prefix + "§cThere are currently no entries!");
                        return false;
                    }

                    sender.sendMessage(prefix + "§7Es gibt folgende Arenen: §b" + arenaNames.stream().map(item -> item.substring(0,1).toUpperCase() + item.substring(1)).collect(Collectors.joining("§7, §b")));
                    return true;
                }

                case "prize":{
                    if(args.length != 1){
                        sendHelp(sender);
                        return false;
                    }


                    ItemStack stack = ArenaManager.hasPrize(((Player) sender).getUniqueId());

                    if(stack == null){
                        sender.sendMessage(prefix + "§cDu hast keinen Preis!");
                        return false;
                    }

                    Inventory inv = Bukkit.createInventory(null, 9*3, "§aNimm deinen Preis!");
                    inv.setItem(13, stack);
                    ((Player) sender).openInventory(inv);
                    return true;
                }
                default:
                    player.sendMessage(prefix + "§cDas Argument §e'" + firstArgument.toUpperCase() + "' §ckonnte nicht gefunden werden!");
                    return false;
            }

        } else {
            sendHelp(sender);
        }


        return false;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(prefix + "§eHelp for §6/arena");
        sender.sendMessage(prefix + "§f - §e/arena view <ArenaName>");
        sender.sendMessage(prefix + "§f - §e/arena list");
        sender.sendMessage(prefix + "§f - §e/arena accept");
        sender.sendMessage(prefix + "§f - §e/arena leave");
        sender.sendMessage(prefix + "§f - §e/arena prize");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> flist = new ArrayList<>();
        ArrayList<String> args1Complete = new ArrayList<>(Arrays.asList("accept", "leave", "view", "list", "prize"));
        ArrayList<String> args2Complete = new ArrayList<>(files.arenaManager.keySet());

        if(args.length == 1){
            for (String s : args1Complete) {
                if(s.startsWith(args[0].toLowerCase())){
                    flist.add(s);
                }
            }
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("view")){
            for (String s : args2Complete) {
                if(s.startsWith(args[1].toLowerCase())){
                    flist.add(s);
                }
            }
        }

        return flist;
    }
}
