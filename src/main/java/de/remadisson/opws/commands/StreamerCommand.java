package de.remadisson.commands;

import de.remadisson.files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StreamerCommand implements CommandExecutor {

    private final String console = files.console;
    private final String prefix = files.prefix;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(console +  "You are currently not capable of running this command!");
            return false;
        }

        if(args.length == 0){
            // TODO SEND HELP
            return true;
        }

        if(args.length == 1){
            // TODO SEND HELP & List
            return true;
        }

        if(args.length == 2){
            // TODO ADD / REMOVE & HELP & LIST
            return true;
        }
        return false;
    }
}
