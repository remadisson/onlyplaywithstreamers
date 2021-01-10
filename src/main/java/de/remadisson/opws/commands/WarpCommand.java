package de.remadisson.opws.commands;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.enums.Warp;
import de.remadisson.opws.files;
import de.remadisson.opws.manager.WarpManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;


public class WarpCommand implements CommandExecutor, TabCompleter {

    private final String prefix = files.prefix;
    private final String permission = "opws.warp";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0){
            sendHelp(sender, 0);
        }

        if(args.length == 1){
            String firstArgument = args[0].toLowerCase();
            HashMap<String, Warp> warps = files.warpManager.getWarps();

            switch(firstArgument){
                case "add":
                    if(!sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }
                    sender.sendMessage(prefix + "§f - §e/warp add <Name>");
                    return true;
                case "remove":
                    if(!sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }
                    sender.sendMessage(prefix + "§f - §e/warp remove <Name>");
                    return true;
                case "list":
                    sendWarpsList(sender, 0);
                    return true;
                default:
                    if(!warps.containsKey(firstArgument)){
                        sendHelp(sender, 0);
                        return false;
                    }

                    if(!(sender instanceof Player)){
                        sender.sendMessage(prefix + "§cYou are the console, you cannot be ported! lulW");
                        return false;
                    }

                    if(!warps.get(firstArgument).getAvailable() && !sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }

                    Warp warp = warps.get(firstArgument);
                    sender.sendMessage(prefix + "§eDu wurdest zu §6" + warp.getName() + "§e teleportiert!");
                    ((Player) sender).teleport(warp.getLocation());
                    ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 5 , 1);

                    return true;
            }
        }

        if(args.length == 2){
            String firstArgument = args[0].toLowerCase();
            String secondArgument = args[1].toLowerCase();

            WarpManager warpManager = files.warpManager;

            switch(firstArgument){
                case "add":
                    if(!sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }

                    if(warpManager.contains(secondArgument)){
                        sender.sendMessage(prefix + "§cThere is already a Warp named §l" + secondArgument + "§c!");
                        return false;
                    }

                    if(!(sender instanceof Player)){
                        sender.sendMessage(prefix + "§cYou cannot execute this command!");
                        return false;
                    }

                    warpManager.addWarp(new Warp(secondArgument, ((Player) sender).getLocation(), ((Player) sender).getUniqueId(), true));
                    sender.sendMessage(prefix + "§eYou have created the Warp §6" + secondArgument + "§e!");
                    return true;
                case "remove":
                    if(!sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }

                    if(!warpManager.contains(secondArgument)){
                        sender.sendMessage(prefix + "§cThere is no a Warp named §l" + secondArgument + "§c!");
                        return false;
                    }

                    if(warpManager.getWarp(secondArgument).getOwner().equals(MojangAPI.getPlayerProfile("remadisson").getUUID()) && !((Player)sender).getUniqueId().equals(MojangAPI.getPlayerProfile("remadisson").getUUID())){
                        sender.sendMessage(prefix + "§cYou cannot remove a Warp, created by the System!");
                        return false;
                    }

                    warpManager.removeWarp(secondArgument);
                    sender.sendMessage(prefix + "§eYou have removed the Warp §6" + secondArgument + "§e!");

                    return true;
                case "list":
                    try {
                        sendWarpsList(sender, Integer.parseInt(secondArgument));
                    }catch(NumberFormatException ex){
                        sender.sendMessage(prefix + "§cDein Arugment entspricht keiner Zahl!");
                    }
                    return true;
                default:
                    sendHelp(sender, 0);
                    return true;
            }
        }

        return false;
    }

    public void sendWarpsList(CommandSender sender, int site){
        HashMap<String, Warp> warps = files.warpManager.getWarps();
        List<String> keys = new ArrayList<>(warps.keySet());

        int maxsites = (int) Math.ceil(warps.size() / 6);

        if (site - 1 > maxsites) {
            site = maxsites + 1;
        }

        if (warps.isEmpty()) {
            sender.sendMessage(prefix + "§cThere are currently no entries.");
            return;
        }

        if (site != 0) {
            site = site - 1;
        }

        sender.sendMessage(prefix + "§aWarps §6Page §8[§b" + (site + 1) + "§7/§e" + (maxsites + 1) + "§8]");

        for (int i = 0; i < (Math.min(warps.size() - (5 * site), 5)); i++) {
            Warp warp = warps.get(keys.get((site * 5) + i));
            if(!warp.getAvailable() && !sender.hasPermission(permission)){
                continue;
            }
            if(sender instanceof Player) {
                TextComponent textComponent = new TextComponent(prefix + "§f - ");
                TextComponent mainComponent = new TextComponent("§e/warp " + warp.getName());
                mainComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getName()));
                mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClick to warp directly to " + warp.getName()).create()));
                textComponent.addExtra(mainComponent);
                sender.spigot().sendMessage(textComponent);
            } else {
                sender.sendMessage(prefix + "§f - §e" + warp.getName());
            }
        }

        if (site != maxsites) {
            sender.sendMessage(prefix + "§e...");
        }
    }

    public void sendHelp(CommandSender sender, int site) {

        if(sender.hasPermission(permission)){
            sender.sendMessage(prefix + "§eHelp for §6/warp");
            sender.sendMessage(prefix + "§f - §e/warp <Name>");
            sender.sendMessage(prefix + "§f - §e/warp add <Name>");
            sender.sendMessage(prefix + "§f - §e/warp remove <Name>");
            sender.sendMessage(prefix + "§f - §e/warp list <Site>");
        } else {
            sender.sendMessage(prefix + "§eHelp for §6/warp");
            sender.sendMessage(prefix + "§f - §e/warp <Name>");
            sender.sendMessage(prefix + "§f - §e/warp list <Site>");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> flist = new ArrayList<>();
        ArrayList<String> indexListP = new ArrayList<>(Arrays.asList("add", "remove"));
        ArrayList<String> indexList = new ArrayList<>(Arrays.asList("list"));

        if(args.length == 1){
            if(sender.hasPermission(permission)){
                for(String index : indexListP){
                    if(index.startsWith(args[0].toLowerCase())){
                        flist.add(index);
                    }
                }
            }

            for(String index : indexList){
                if(index.startsWith(args[0].toLowerCase())){
                    flist.add(index);
                }
            }
            if(args[0].length() > 0) {
                for (Map.Entry<String, Warp> warp : files.warpManager.getWarps().entrySet()) {

                    if(!warp.getValue().getAvailable() && !sender.hasPermission(permission)){
                        continue;
                    }

                    if (warp.getKey().startsWith(args[0].toLowerCase())) {
                        flist.add(warp.getKey());
                    }
                }
            }

        }


        if(args.length == 2){
            if(sender.hasPermission(permission)) {
                if (args[0].toLowerCase().equals("remove")) {
                    for (String warp : files.warpManager.getWarps().keySet()) {
                        if (warp.toLowerCase().startsWith(args[1].toLowerCase())) flist.add(warp);
                    }
                } else if (args[0].toLowerCase().equals("list")) {
                    int maxsites = (int) Math.ceil(files.warpManager.getWarps().size() / 6);
                    for(int i = 1; i <= maxsites; i++){
                        flist.add(String.valueOf(i));
                    }
                }
            }
        }

        return flist;
    }
}
