package de.remadisson.opws.commands;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.files;
import de.remadisson.opws.mojang.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class BanListCommand implements CommandExecutor {

    private final String permission = "opws.banlist";
    private final String prefix = files.console;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission(permission)){
            sender.sendMessage(prefix + "§cYou do not have Permission to execute this command!");
            return true;
        }

        if(args.length != 0){
            sender.sendMessage(prefix + "§7Usage: §a/banlist");
            return true;
        }

        ArrayList<UUID> bannedPlayersUUID = new ArrayList<>(files.bannedPlayersMap.keySet());

        if(bannedPlayersUUID.isEmpty()){
            sender.sendMessage(prefix + "§cThere are currently no players banned!");
            return true;
        }

        files.pool.execute(() -> {
            sender.sendMessage(prefix + "§7Currently banned §cPlayers §8»");
            String bannedPlayers = bannedPlayersUUID.stream().map((item) -> {
                PlayerProfile playerProfile = MojangAPI.getPlayerProfile(item);
                return playerProfile.getName() + "§7(§b" + files.bannedPlayersMap.get(item).getReason() + "§7)";
            }).collect(Collectors.joining("§f, §c"));
            sender.sendMessage(prefix + "§c" + bannedPlayers);
        });

        return false;
    }
}
