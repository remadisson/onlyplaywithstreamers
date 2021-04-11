package de.remadisson.opws.commands;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.enums.PunishmentEnum;
import de.remadisson.opws.files;
import de.remadisson.opws.manager.BannedPlayer;
import de.remadisson.opws.mojang.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanCommand implements TabExecutor {

    private final String permission = "opws.ban";
    private final String prefix = files.console;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission(permission)){
            sender.sendMessage(prefix + "§cYou do not have Permission to execute this command!");
            return true;
        }

        if(args.length <=1){
            sendHelp(sender);
            return true;
        }

        PlayerProfile playerProfile = MojangAPI.getPlayerProfile(args[0].toLowerCase());

        if (playerProfile == null || playerProfile.getUUID() == null) {
            sender.sendMessage(prefix + "§cThe Player §4" + args[0].toLowerCase() + " §cdoes not exists.");
            return true;
        }

        if (sender instanceof Player) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerProfile.getUUID());
            if(sender == offlinePlayer){
                sender.sendMessage(prefix + "§cYou cannot ban yourself!");
                return true;
            }

            if(offlinePlayer.hasPlayedBefore()) {
                if (!sender.isOp() && offlinePlayer.isOp()) {
                    sender.sendMessage(prefix + "§cYou cannot ban the Player §4" + playerProfile.getName() + "!");
                    return true;
                }

                if (files.streamerManager.getStreamer().contains(((Player) sender).getUniqueId()) && files.streamerManager.getStreamer().contains(playerProfile.getUUID())) {
                    sender.sendMessage(prefix + "§cYou cannot ban the Player §4" + playerProfile.getName() + "!");
                    return true;
                }
            }
        }

        if(files.bannedPlayersMap.containsKey(playerProfile.getUUID())){
            sender.sendMessage(prefix + "§cThe Player §4" + playerProfile.getName() + "§c is already banned for §4'" + files.bannedPlayersMap.get(playerProfile.getUUID()).getCorrectFormalReason() + "'.");
            return true;
        }

        String reason = "";
        for (int iterator = 1; iterator < args.length; iterator++) {
            reason = (reason.length() > 0 ? (reason + " ") : (reason)) + args[iterator];
        }

        reason = reason.substring(0, 1).toUpperCase() + reason.substring(1);

        banPlayer(sender, playerProfile.getName(), playerProfile.getUUID(), reason);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> flist = new ArrayList<>();

        String[] presetReasons = new String[]{"Spam", "Unangebrachtes Verhalten"};

        if(sender.hasPermission(permission)) {
            if (args.length == 1) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        flist.add(player.getName());
                    }
                }
            }

            if (args.length == 2) {
                for (String presetReason : presetReasons) {
                    if (presetReason.toLowerCase().startsWith(args[1].toLowerCase())) {
                        flist.add(presetReason);
                    }
                }
            }
        }

            return flist;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(prefix + "§7Help for §a/ban");
        sender.sendMessage(prefix + "§7Information: §a[] §8-> §bRequired; §a<> §8-> §bOptional!");
        sender.sendMessage(prefix + "§f - §a/ban [Player] [Reason]");
    }

    public void banPlayer(CommandSender sender, String name, UUID banned, String reason){
        String creator = sender instanceof Player ? files.getPrefix(((Player) sender).getUniqueId()) + sender.getName() : "§cConsole";
        Player target = Bukkit.getPlayer(banned);
        if(target != null && target.isOnline()) {
            target.kickPlayer("§cDu wurdest gebannt!\n§7von: " + creator + "\n§7Grund: §b" + reason + "\n\n§7Melde dich bei einem Admin, falls dies ein Fehler sein sollte.");
        }
        if(sender instanceof Player) {
            files.bannedPlayersMap.put(banned, new BannedPlayer(banned, reason.toLowerCase(), ((Player) sender).getUniqueId()));
            files.sendDiscordPunishment(PunishmentEnum.BAN, new BannedPlayer(banned, reason.toLowerCase(), ((Player) sender).getUniqueId()));
        } else {
            files.bannedPlayersMap.put(banned, new BannedPlayer(banned, reason.toLowerCase(), UUID.fromString("09b0e604-763f-4ee1-97a5-adc08a019849")));
            files.sendDiscordPunishment(PunishmentEnum.BAN, new BannedPlayer(banned, reason.toLowerCase(), UUID.fromString("09b0e604-763f-4ee1-97a5-adc08a019849")));
        }

        sender.sendMessage(prefix + "§7You have banned §a" + name + " §7for §b" + reason + ".");

    }

}
