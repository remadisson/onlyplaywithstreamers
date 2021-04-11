package de.remadisson.opws.commands;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.files;
import de.remadisson.opws.listener.JoinAndQuitListener;
import de.remadisson.opws.main;
import de.remadisson.opws.manager.BannedPlayer;
import de.remadisson.opws.mojang.PlayerProfile;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class StaffCommand implements TabExecutor {

    String prefix = files.prefix;
    String permission = "opws.staff";

    public static ArrayList<Player> vanishPlayer = new ArrayList<>();
    public static ArrayList<Player> flyingPlayer = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player) || !sender.hasPermission(permission)) {
            sender.sendMessage(prefix + "§4You have no permission to execute this command!");
            return false;
        }

        if (args.length == 1 || args.length == 2) {
            String firstArgument = args[0].toLowerCase();

            switch (firstArgument) {
                case "vanish": {
                    if (args.length == 1) {
                        if (vanishPlayer.contains(sender)) {
                            setVanishPlayer((Player) sender);
                            sender.sendMessage(prefix + "§7Du bist nun §bsichtbar!");
                        } else {
                            setVanishPlayer((Player) sender);
                            sender.sendMessage(prefix + "§7Du bist nun §bunsichtbar!");
                        }
                    } else {
                        String secondArgument = args[1].toLowerCase();
                        Player target = Bukkit.getPlayer(secondArgument);
                        if (target == null) {
                            sender.sendMessage(prefix + "§7The Player §c" + secondArgument.toUpperCase() + "§7 is not online!");
                            return false;
                        }

                        if (target == (Player) sender) {
                            sender.sendMessage(prefix + "§7You §ccannot §7perform this command on §cyourself!");
                            return true;
                        }

                        if (!sender.isOp()) {
                            sender.sendMessage(prefix + "§cYou have no permission to execute this command!");
                            return true;
                        }


                        if (vanishPlayer.contains(target)) {
                            setVanishPlayer((Player) target);
                            target.sendMessage(prefix + "§7Du bist nun §bsichtbar!");
                            sender.sendMessage(prefix + "§7The Player §b" + target.getName() + "§7 is now §bvisible!");
                        } else {
                            setVanishPlayer((Player) target);
                            target.sendMessage(prefix + "§7Du bist nun §bunsichtbar!");
                            sender.sendMessage(prefix + "§7The Player §b" + target.getName() + "§7 is now §binvisible!");
                        }
                    }
                    return true;
                }

                case "fly": {
                    if (args.length == 1) {


                        if (flyingPlayer.contains(sender)) {
                            setFlyingPlayer((Player) sender, false);
                            sender.sendMessage(prefix + "§7Du kannst nun §cnicht mehr fliegen!");
                        } else {
                            setFlyingPlayer((Player) sender, true);
                            sender.sendMessage(prefix + "§7Du kannst nun §bfliegen!");
                        }
                    } else {
                        String secondArgument = args[1].toLowerCase();
                        Player target = Bukkit.getPlayer(secondArgument);
                        if (target == null) {
                            sender.sendMessage(prefix + "§7The Player §c" + secondArgument.toUpperCase() + "§7 is not online!");
                            return false;
                        }

                        if (target == (Player) sender) {
                            sender.sendMessage(prefix + "§7You §ccannot §7perform this command on §cyourself!");
                            return true;
                        }

                        if (!sender.isOp()) {
                            sender.sendMessage(prefix + "§cYou have no permission to execute this command!");
                            return true;
                        }

                        if (flyingPlayer.contains(target)) {
                            setFlyingPlayer((Player) target, false);
                            sender.sendMessage(prefix + "§7Du kannst nun §cnicht mehr fliegen!");
                            sender.sendMessage(prefix + "§7The Player §b" + target.getName() + " §ccannot fly §7anymore!");
                        } else {
                            setFlyingPlayer((Player) target, true);
                            sender.sendMessage(prefix + "§7Du kannst nun §bfliegen!");
                            sender.sendMessage(prefix + "§7The Player §b" + target.getName() + "§7 is now allowed to §bfly!");
                        }
                    }
                    return true;
                }

                case "invsee": {
                    if (args.length == 1) {
                        sendHelp(sender);
                        return true;
                    }
                    String secondArgument = args[1].toLowerCase();
                    Player target = Bukkit.getPlayer(secondArgument);
                    if (target == null) {
                        sender.sendMessage(prefix + "§7The Player §c" + secondArgument.toUpperCase() + "§7 is not online!");
                        return false;
                    }

                    if (target == (Player) sender) {
                        sender.sendMessage(prefix + "§7You §ccannot §7perform this command on §cyourself!");
                        return true;
                    }

                    sender.sendMessage(prefix + "§7Looking into the Inventory from §b" + target.getName());
                    ((Player) sender).openInventory(target.getInventory());
                    return true;
                }

                case "baninfo": {
                    if (args.length == 1) {
                        sendHelp(sender);
                        return true;
                    }

                    PlayerProfile playerProfile = MojangAPI.getPlayerProfile(args[1].toLowerCase());

                    if (playerProfile == null || playerProfile.getUUID() == null) {
                        sender.sendMessage(prefix + "§cThe Player §4" + args[1].toLowerCase() + " §cdoes not exists.");
                        return true;
                    }

                    if (!files.bannedPlayersMap.containsKey(playerProfile.getUUID())) {
                        sender.sendMessage(prefix + "§cThe Player §4" + playerProfile.getName() + "§c is not banned.");
                        return true;
                    }

                    files.pool.execute(() -> {

                        BannedPlayer bannedPlayer = files.bannedPlayersMap.get(playerProfile.getUUID());
                        PlayerProfile creator = MojangAPI.getPlayerProfile(bannedPlayer.getCreator());

                        sender.sendMessage(prefix + "§7Ban Entry: " + files.getPrefix(playerProfile.getUUID()) + playerProfile.getName());
                        sender.sendMessage(prefix + "§f - §aReason: §b" + bannedPlayer.getReason());
                        sender.sendMessage(prefix + "§f - §aCreator: §b" + files.getPrefix(bannedPlayer.getCreator()) + creator.getName());

                    });

                    return true;
                }

                default:
                    sendHelp(sender);
            }

        } else {
            sendHelp(sender);
        }
        return false;
    }

    public static void setVanishPlayer(Player player) {
        if (!vanishPlayer.contains(player)) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!vanishPlayer.contains(player)) {
                    online.hidePlayer(main.getInstance(), player);
                }
            }

            for (Player vanish : vanishPlayer) {
                player.showPlayer(main.getInstance(), vanish);
                vanish.showPlayer(main.getInstance(), player);
            }

            vanishPlayer.add(player);
        } else {
            vanishPlayer.remove(player);
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(main.getInstance(), player);
            }

            for (Player vanish : vanishPlayer) {
                player.hidePlayer(main.getInstance(), vanish);
            }

        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            JoinAndQuitListener.updateHeaderAndFooter(online);
        }

    }

    public static void setFlyingPlayer(Player player, boolean flying) {

        if (flying) {
            if (!flyingPlayer.contains(player)) {

                player.setAllowFlight(true);
                flyingPlayer.add(player);
            }
        } else {
            if (flyingPlayer.contains(player)) {
                player.setAllowFlight(false);
                flyingPlayer.remove(player);
            }

        }
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(prefix + "§7Help for §b/staff");
        sender.sendMessage(prefix + "§7Information: §a[] §8-> §bRequired; §a<> §8-> §bOptional!");
        sender.sendMessage(prefix + "§f - §b/staff vanish <Player>");
        sender.sendMessage(prefix + "§f - §b/staff fly <Player>");
        sender.sendMessage(prefix + "§f - §b/staff invsee [Player]");
        sender.sendMessage(prefix + "§f - §b/staff baninfo [Player]");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> flist = new ArrayList<>();
        String[] index = new String[]{"vanish", "fly", "invsee", "baninfo"};

        if (sender.hasPermission(permission)) {

            if(args[0].equalsIgnoreCase("baninfo")){
                for (UUID uuid : files.bannedPlayersMap.keySet()) {
                    files.pool.execute(() -> {
                        PlayerProfile playerProfile = MojangAPI.getPlayerProfile(uuid);
                        if(playerProfile.getName().toLowerCase().startsWith(args[0].toLowerCase())){
                            flist.add(playerProfile.getName());
                        }
                    });
                }

                return flist;
            }

            if (args.length == 1) {
                for (String s : index) {
                    if (s.startsWith(args[0].toLowerCase())) {
                        flist.add(s);
                    }
                }
            }

            if (args.length == 2 && args[1].length() > 1) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getName().toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase())) {
                        flist.add(online.getName());
                    }
                }
            }
        }

        return flist;
    }
}
