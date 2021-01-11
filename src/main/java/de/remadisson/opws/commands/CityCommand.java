package de.remadisson.opws.commands;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.enums.Warp;
import de.remadisson.opws.files;
import de.remadisson.opws.manager.CityManager;
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

public class CityCommand implements CommandExecutor, TabCompleter {

    private final String prefix = files.prefix;
    private final String permission = "opws.city";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0){
            sendHelp(sender, 0);
        }

        if(args.length == 1){
            String firstArgument = args[0].toLowerCase();
            HashMap<String, Warp> cities = files.cityManager.getCities();

            switch(firstArgument){
                case "add":
                    if(!sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }
                    sender.sendMessage(prefix + "§f - §e/city add <Name>");
                    return true;
                case "remove":
                    if(!sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }
                    sender.sendMessage(prefix + "§f - §e/city remove <Name>");
                    return true;
                case "list":
                    sendCityList(sender, 0);
                    return true;
                default:
                    if(!cities.containsKey(firstArgument)){
                        sendHelp(sender, 0);
                        return false;
                    }

                    if(!(sender instanceof Player)){
                        sender.sendMessage(prefix + "§cYou are the console, you cannot be ported! lulW");
                        return false;
                    }

                    if(!cities.get(firstArgument).getAvailable() && !sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }

                    Warp city = cities.get(firstArgument);
                    sender.sendMessage(prefix + "§eDu wurdest zu §6" + city.getFirstUpperName() + "§e teleportiert!");
                    ((Player) sender).teleport(city.getLocation());
                    ((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 5 , 1);

                    return true;
            }
        }

        if(args.length == 2){
            String firstArgument = args[0].toLowerCase();
            String secondArgument = args[1].toLowerCase();

            CityManager cityManager = files.cityManager;

            switch(firstArgument){
                case "add":
                    if(!sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }

                    if(cityManager.contains(secondArgument)){
                        sender.sendMessage(prefix + "§cThere is already a City named §l" + secondArgument + "§c!");
                        return false;
                    }


                    if(!(sender instanceof Player)){
                        sender.sendMessage(prefix + "§cYou cannot execute this command!");
                        return false;
                    }

                    if(cityManager.isOwner(((Player)sender).getUniqueId())){
                       sender.sendMessage(prefix + "§cYou can only have one city at once!");
                       return false;
                    }

                    cityManager.addCity(new Warp(secondArgument, ((Player) sender).getLocation(), ((Player) sender).getUniqueId(), true));
                    sender.sendMessage(prefix + "§eYou have created the City §6" + secondArgument + "§e!");
                    return true;
                case "remove":
                    if(!sender.hasPermission(permission)){
                        sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
                        return false;
                    }

                    if(!cityManager.contains(secondArgument)){
                        sender.sendMessage(prefix + "§cThere is no a City named §l" + secondArgument + "§c!");
                        return false;
                    }

                    if(cityManager.getCity(secondArgument).getOwner().equals(MojangAPI.getPlayerProfile("remadisson").getUUID()) && !((Player)sender).getUniqueId().equals(MojangAPI.getPlayerProfile("remadisson").getUUID())){
                        sender.sendMessage(prefix + "§cYou cannot remove a City, created by the System!");
                        return false;
                    }

                    if((sender instanceof Player) && (cityManager.getCity(secondArgument).getOwner().equals(((Player) sender).getUniqueId()) || sender.isOp())){
                        sender.sendMessage(prefix + "This is not your City!");
                        return false;
                    }

                    cityManager.removeCity(secondArgument);
                    sender.sendMessage(prefix + "§eYou have removed the City §6" + secondArgument + "§e!");

                    return true;
                case "list":
                    try {
                        sendCityList(sender, Integer.parseInt(secondArgument));
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

    public void sendCityList(CommandSender sender, int site){
        HashMap<String, Warp> cities = files.cityManager.getCities();
        List<String> keys = new ArrayList<>(cities.keySet());

        int maxsites = (int) Math.ceil(cities.size() / 6);

        if (site - 1 > maxsites) {
            site = maxsites + 1;
        }

        if (cities.isEmpty()) {
            sender.sendMessage(prefix + "§cThere are currently no entries.");
            return;
        }

        if (site != 0) {
            site = site - 1;
        }

        sender.sendMessage(prefix + "§aCities §6Page §8[§b" + (site + 1) + "§7/§e" + (maxsites + 1) + "§8]");

        for (int i = 0; i < (Math.min(cities.size() - (5 * site), 5)); i++) {
            Warp city = cities.get(keys.get((site * 5) + i));
            if(!city.getAvailable() && !sender.hasPermission(permission)){
                continue;
            }
            if(sender instanceof Player) {
                TextComponent textComponent = new TextComponent(prefix + "§f - ");
                TextComponent mainComponent = new TextComponent("§e/city " + city.getFirstUpperName());
                mainComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/city " + city.getFirstUpperName()));
                mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClick to teleport directly to " + city.getFirstUpperName()).create()));
                textComponent.addExtra(mainComponent);
                textComponent.addExtra(" §7- " + "§bEigentümer§7: §6" + MojangAPI.getPlayerProfile(city.getOwner()).getName());
                sender.spigot().sendMessage(textComponent);
            } else {
                sender.sendMessage(prefix + "§f - §e" + city.getFirstUpperName() + " §7- " + "§bEigentümer§7: §6" + MojangAPI.getPlayerProfile(city.getOwner()).getName());
            }
        }

        if (site != maxsites) {
            sender.sendMessage(prefix + "§e...");
        }
    }

    public void sendHelp(CommandSender sender, int site) {

        if(sender.hasPermission(permission)){
            sender.sendMessage(prefix + "§eHelp for §6/city");
            sender.sendMessage(prefix + "§f - §e/city <Name>");
            sender.sendMessage(prefix + "§f - §e/city add <Name>");
            sender.sendMessage(prefix + "§f - §e/city remove <Name>");
            sender.sendMessage(prefix + "§f - §e/city list <Site>");
        } else {
            sender.sendMessage(prefix + "§eHelp for §6/city");
            sender.sendMessage(prefix + "§f - §e/city <Name>");
            sender.sendMessage(prefix + "§f - §e/city list <Site>");
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
                for (Map.Entry<String, Warp> city : files.cityManager.getCities().entrySet()) {

                    if(!city.getValue().getAvailable() && !sender.hasPermission(permission)){
                        continue;
                    }

                    if (city.getKey().startsWith(args[0].toLowerCase())) {
                        flist.add(city.getKey());
                    }
                }
            }

        }


        if(args.length == 2){
            if(sender.hasPermission(permission)) {
                if (args[0].toLowerCase().equals("remove")) {
                    for (String city : files.cityManager.getCities().keySet()) {
                        if (city.toLowerCase().startsWith(args[1].toLowerCase())) flist.add(city);
                    }
                } else if (args[0].toLowerCase().equals("list")) {
                    int maxsites = (int) Math.ceil(files.cityManager.getCities().size() / 6);
                    for(int i = 1; i <= maxsites; i++){
                        flist.add(String.valueOf(i));
                    }
                }
            }
        }

        return flist;
    }
}
