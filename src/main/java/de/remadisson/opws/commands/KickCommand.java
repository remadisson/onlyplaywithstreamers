package de.remadisson.opws.commands;

import de.remadisson.opws.enums.PunishmentEnum;
import de.remadisson.opws.files;
import de.remadisson.opws.manager.BannedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KickCommand implements TabExecutor {

    private final String permission = "opws.kick";
    private final String prefix = files.console;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(prefix + "§cYou do not have Permission to execute this command!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
            if (args[0].equalsIgnoreCase("@a")) {
                if (sender.isOp()) {
                int kickedPlayer = 0;
                if (args.length == 1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!player.isOp() && !files.streamerManager.getStreamer().contains(player.getUniqueId()) && !files.streamerManager.getWorker().contains(player.getUniqueId())) {
                            KickPlayer(sender, player, null, true);
                            kickedPlayer++;
                        }
                    }
                    sender.sendMessage(prefix + "§7You have kicked §a" + kickedPlayer + " Players!");
                    return true;
                }

                String reason = "";
                for (int iterator = 1; iterator < args.length; iterator++) {
                    reason = (reason.length() > 0 ? (reason + " ") : (reason)) + args[iterator];
                }

                reason = reason.substring(0, 1).toUpperCase() + reason.substring(1);


                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.isOp() && !files.streamerManager.getStreamer().contains(player.getUniqueId()) && !files.streamerManager.getWorker().contains(player.getUniqueId())) {
                        KickPlayer(sender, player, reason, true);
                        kickedPlayer++;
                    }
                }

                sender.sendMessage(prefix + "§7You have kicked §a" + kickedPlayer + " Players!");
                return true;

                } else {
                    sender.sendMessage(prefix + "§cYou dont have Permission to execute this command!");
                    return true;
                }

            }



        Player target = Bukkit.getPlayer(args[0].toLowerCase());

        if (target == null) {
            sender.sendMessage(prefix + "§cThe Player §4" + args[0].toLowerCase() + " §cdoes not exists.");
            return true;
        }

        if (!target.isOnline()) {
            sender.sendMessage(prefix + "§cThe Player §4" + args[0].toLowerCase() + "§cis offline!");
            return true;
        }

        if(target == sender){
            sender.sendMessage(prefix + "§cYou cannot kick yourself!");
            return true;
        }

        if (sender instanceof Player) {
            if (!sender.isOp() && target.isOp()) {
                sender.sendMessage(prefix + "§cYou cannot kick the Player §4" + target.getName() + "!");
                return true;
            }

            if (files.streamerManager.getStreamer().contains(((Player) sender).getUniqueId()) && files.streamerManager.getStreamer().contains(target.getUniqueId())) {
                sender.sendMessage(prefix + "§cYou cannot kick the Player §4" + target.getName() + "!");
                return true;
            }
        }

        if (args.length == 1) {
            KickPlayer(sender, target, null, false);
            return true;
        }

        String reason = "";
        for (int iterator = 1; iterator < args.length; iterator++) {
            reason = (reason.length() > 0 ? (reason + " ") : (reason)) + args[iterator];
        }

        reason = reason.substring(0, 1).toUpperCase() + reason.substring(1);

        KickPlayer(sender, target, reason, false);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> flist = new ArrayList<>();

        String[] presetReasons = new String[]{"Spam", "Unangebrachtes Verhalten"};

        if(sender.hasPermission(permission)){
            if(args.length == 1){
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(player.getName().toLowerCase().startsWith(args[0].toLowerCase())){
                        flist.add(player.getName());
                    }
                }

                if(sender.isOp()){
                    if("@a".startsWith(args[0].toLowerCase())){
                        flist.add("@a");
                    }
                }
            }

            if(args.length == 2){
                for (String presetReason : presetReasons) {
                    if(presetReason.toLowerCase().startsWith(args[1].toLowerCase())){
                        flist.add(presetReason);
                    }
                }
            }

        }

        return flist;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(prefix + "§7Help for §a/kick");
        sender.sendMessage(prefix + "§7Information: §a[] §8-> §bRequired; §a<> §8-> §bOptional!");
        sender.sendMessage(prefix + "§f - §a/kick [Player] <Reason>");
    }

    public void KickPlayer(CommandSender sender, Player kicked, String reason, boolean all) {
        String kicker = sender instanceof Player ? files.getPrefix(((Player) sender).getUniqueId()) + sender.getName() : "§cConsole";
        if (reason == null) {
            kicked.kickPlayer("§cDu wurdest gekickt!\n§7Von: " + kicker);
        } else {
            kicked.kickPlayer("§cDu wurdest gekickt!\n§7Von: " + kicker + "\n§7Grund: §b" + reason);
        }

        if (all) return;
        if(sender instanceof Player) {
            files.sendDiscordPunishment(PunishmentEnum.KICK, new BannedPlayer(kicked.getUniqueId(), reason == null ? "no reason specified" : reason, ((Player) sender).getUniqueId()));
        } else {
            files.sendDiscordPunishment(PunishmentEnum.KICK, new BannedPlayer(kicked.getUniqueId(), reason == null ? "no reason specified" : reason, UUID.fromString("09b0e604-763f-4ee1-97a5-adc08a019849")));
        }


        if(reason == null){
            sender.sendMessage(prefix + "§7You have kicked §a" + kicked.getName() + ".");
            return;
        }
        sender.sendMessage(prefix + "§7You have kicked §a" + kicked.getName() + " §7for §b" + reason + ".");
    }
}
