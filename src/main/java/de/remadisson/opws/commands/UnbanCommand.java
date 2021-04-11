package de.remadisson.opws.commands;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.enums.PunishmentEnum;
import de.remadisson.opws.files;
import de.remadisson.opws.manager.BannedPlayer;
import de.remadisson.opws.mojang.PlayerProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class
UnbanCommand implements TabExecutor {

    private final String permission = "opws.unban";
    private final String prefix = files.console;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission(permission)){
            sender.sendMessage(prefix + "§cYou do not have Permission to execute this command!");
            return true;
        }

        if(args.length !=1){
            sendHelp(sender);
            return true;
        }

        PlayerProfile playerProfile = MojangAPI.getPlayerProfile(args[0].toLowerCase());

        if (playerProfile == null || playerProfile.getUUID() == null) {
            sender.sendMessage(prefix + "§cThe Player §4" + args[0].toLowerCase() + " §cdoes not exists.");
            return true;
        }

        if(!files.bannedPlayersMap.containsKey(playerProfile.getUUID())){
            sender.sendMessage(prefix + "§cThe Player §4" + playerProfile.getName() + "§c is not banned.");
            return true;
        }

        unbanPlayer(sender, playerProfile.getName(), playerProfile.getUUID());

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> flist = new ArrayList<>();
        if(sender.hasPermission(permission)){
            for (UUID uuid : files.bannedPlayersMap.keySet()) {
                files.pool.execute(() -> {
                    PlayerProfile playerProfile = MojangAPI.getPlayerProfile(uuid);
                    if(playerProfile.getName().toLowerCase().startsWith(args[0].toLowerCase())){
                        flist.add(playerProfile.getName());
                    }
                });
            }
        }

        return flist;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(prefix + "§7Help for §a/unban");
        sender.sendMessage(prefix + "§7Information: §a[] §8-> §bRequired; §a<> §8-> §bOptional!");
        sender.sendMessage(prefix + "§f - §a/unban [Player]");
    }

    public void unbanPlayer(CommandSender sender, String name, UUID uuid){
        files.bannedPlayersMap.remove(uuid);
        files.bannedPlayerAPI.getSection("banned").set(uuid.toString(), null);
        if(sender instanceof Player) {
            files.sendDiscordPunishment(PunishmentEnum.UNBAN, new BannedPlayer(uuid, null, ((Player) sender).getUniqueId()));
        } else {
            files.sendDiscordPunishment(PunishmentEnum.UNBAN, new BannedPlayer(uuid, null, UUID.fromString("09b0e604-763f-4ee1-97a5-adc08a019849")));
        }
        sender.sendMessage(prefix + "§7The Player §a" + name + " §7has been unbanned!");
    }
}
