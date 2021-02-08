package de.remadisson.opws.commands;

import de.remadisson.opws.files;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand implements CommandExecutor {

    String prefix = files.prefix;
    String permission = "opws.vanish";

    public static ArrayList<Player> vanishPlayer = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player) || !sender.hasPermission(permission)){
            sender.sendMessage(prefix + "§4You have no permission to execute this command!");
            return false;
        }

        if(args.length == 0){
            if(vanishPlayer.contains(sender)){
                setVanishPlayer((Player) sender, false);
                sender.sendMessage(prefix + "§eDu bist nun §bsichtbar!");
            } else {
                setVanishPlayer((Player) sender, true);
                sender.sendMessage(prefix + "§eDu bist nun §bunsichtbar!");
            }
        }

        return false;
    }

    public static void setVanishPlayer(Player player, boolean vanished){

        if(vanished){
            if(!vanishPlayer.contains(player)){
                for(Player online : Bukkit.getOnlinePlayers()){
                    if(!vanishPlayer.contains(player)) {
                        online.hidePlayer(player);
                    }
                }

                for(Player vanish : vanishPlayer){
                    player.showPlayer(vanish);
                    vanish.showPlayer(player);
                }


                vanishPlayer.add(player);
            }
        } else {
            if(vanishPlayer.contains(player)){
                for(Player online : Bukkit.getOnlinePlayers()){
                    online.showPlayer(player);
                }

                for(Player vanish : vanishPlayer){
                    player.hidePlayer(vanish);
                }

                vanishPlayer.remove(player);
            }

        }

    }

}
