package de.remadisson.opws.commands;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.files;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class WhitelistCommand implements TabExecutor {

    /*
        Command: Whitelist
        Permission: minecraft.command.whitelist
        Syntax: whitelist <on/off/add/remove/list/reload> <Player>
     */

    private final String prefix = "§eWhitelist " + files.prefix;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("minecraft.command.whitelist")) {
            sender.sendMessage(prefix + "§cYou do not have permission to execute this command!");
            return false;
        }

        if (args.length == 0) {
            sendHelp(sender, 0);
            return true;
        }

        if (args.length == 1) {
            final String firstRaw = args[0].toLowerCase();
            switch (firstRaw) {
                case "on":
                case "true":
                case "1":

                    if (files.whitelistToggle) {
                        sender.sendMessage(prefix + "§cWhitelist is already §a§lon§c.");
                        return true;
                    }

                    files.whitelistToggle = true;
                    sender.sendMessage(prefix + "§eWhitelist is now §a§lon§e!");

                    return true;

                case "off":
                case "false":
                case "0":

                    if (!files.whitelistToggle) {
                        sender.sendMessage(prefix + "§cWhitelist is already §loff§c.");
                        return true;
                    }

                    files.whitelistToggle = false;
                    sender.sendMessage(prefix + "§eWhitelist is now §c§loff§e!");

                    return true;

                case "reload":
                    files.whitelist = new ArrayList<>(files.whitelistAPI.getStringList("whitelistedPlayer").stream().map(UUID::fromString).collect(Collectors.toList()));
                    sender.sendMessage(prefix + "§eWhitelist reloaded!");
                    return true;

                case "list":

                    files.pool.execute(() -> {
                        ArrayList<String> whitelistedNames = new ArrayList<>();

                        sender.sendMessage(prefix + "§eCurrently whitelisted Players:");

                        if(files.whitelist.isEmpty()){
                            sender.sendMessage(prefix + "§cThere are currently no entries!");
                            return;
                        }

                        for (UUID player : files.whitelist) {
                            whitelistedNames.add(Objects.requireNonNull(MojangAPI.getPlayerProfile(player)).getName());
                        }

                        String whitelistedPlayers = String.join("§f, §a", whitelistedNames);
                        sender.sendMessage(prefix + "§a" + whitelistedPlayers);
                    });

                    return true;

                case "add":
                case "remove":
                    sender.sendMessage(prefix + "§eHelp for §6/whitelist");
                    sender.sendMessage(prefix + "§f - §e/allowed add/remove <Name>");
                    return false;
                default:
                    sendHelp(sender, 0);

            }
            return true;
        }

        if(args.length == 2){
            final String firstRaw = args[0].toLowerCase();
            final String secondRaw = args[1].toLowerCase();

            switch(firstRaw){
                case "add": {
                    Player player = Bukkit.getPlayer(secondRaw);
                    UUID uuid = null;

                    if (player != null) {
                        uuid = player.getUniqueId();
                    } else {
                        uuid = Bukkit.getPlayerUniqueId(secondRaw);
                    }

                    if(uuid == null){
                        sender.sendMessage(prefix + "§cThis player does not exists!");
                        return false;
                    }

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                    if (files.whitelist.contains(offlinePlayer.getUniqueId())) {
                        sender.sendMessage(prefix + "§cThe Player §4§l" + secondRaw + " §cis already whitelisted!");
                        return false;
                    }

                    files.whitelist.add(offlinePlayer.getUniqueId());
                    sender.sendMessage(prefix + "§eThe Player §6§l" + secondRaw + "§e is now whitelisted!");

                    return true;
                }

                case "remove": {
                    Player player = Bukkit.getPlayer(secondRaw);
                    UUID uuid;

                    if (player != null) {
                        uuid = player.getUniqueId();
                    } else {
                        uuid = Bukkit.getPlayerUniqueId(secondRaw);
                    }

                    if(uuid == null){
                        sender.sendMessage(prefix + "§cThis player does not exists!");
                        return false;
                    }

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                    if (!files.whitelist.contains(offlinePlayer.getUniqueId())) {
                        sender.sendMessage(prefix + "§cThe Player §4§l" + secondRaw + "§cis not whitelisted!");
                        return false;
                    }

                    files.whitelist.remove(offlinePlayer.getUniqueId());
                    sender.sendMessage(prefix + "§eThe Player §6§l" + secondRaw + "§e not whitelisted anymore!");

                    return true;
                }

                default:
                    sendHelp(sender, 0);
                    return true;
            }

        }

        return false;
    }

    public void sendHelp(CommandSender sender, int site) {
        sender.sendMessage(files.prefix + "§eHelp for §6/whitelist §f- §dStatus: " + (files.whitelistToggle ? ("§atrue") : ("§cfalse")));
        sender.sendMessage(files.prefix + "§f - §e/whitelist on/off");
        sender.sendMessage(files.prefix + "§f - §e/whitelist add/remove <Name>");
        sender.sendMessage(files.prefix + "§f - §e/whitelist reload");
        sender.sendMessage(files.prefix + "§f - §e/whitelist list");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> flist = new ArrayList<>();
        List<String> parameters = new ArrayList<>(Arrays.asList("on", "off", "add", "remove", "reload", "list"));

        if(sender.hasPermission("minecraft.command.whitelist")) {
            if (args.length == 1) {
                for (String parameter : parameters) {
                    if (parameter.startsWith(args[0].toLowerCase())) {
                        flist.add(parameter);
                    }
                }
            }

            if (args.length == 2 && (args[0].toLowerCase().equals("add") || args[0].toLowerCase().equals("remove"))) {
                for (UUID whitelisted : files.whitelist) {
                    String name = null;
                        name = MojangAPI.getPlayerProfile(whitelisted).getName();

                    if (name.startsWith(args[1].toLowerCase())) {
                        flist.add(name);
                    }
                }
            }
        }
        return flist;
    }
}
