package de.remadisson.opws.commands;

import de.remadisson.opws.files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetupCommand implements CommandExecutor {

    private final String prefix = files.prefix;
    private static final String permission = "opws.setup";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender.hasPermission(permission)){
            sender.sendMessage(files.prefix + "§cYou do not have permission to execute this command!");
            return false;
        }

        if(args.length == 0){

        }

        return false;
    }

    public void sendHelp(CommandSender sender, int site) {

        /*
           Flags:
            StreamerOpen
            AllowedJoinClosed

           Other Settings:
            forceOpen
            forceClose
            reset farmwelt
         */

        sender.sendMessage(prefix + "§eHelp for §6/setup");
        sender.sendMessage(prefix + "§f - §e/setup set <Flag> <true|false>");
        sender.sendMessage(prefix + "§f - §e/setup reset farmwelt");
    }
}
