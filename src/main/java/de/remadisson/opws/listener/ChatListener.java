package de.remadisson.opws.listener;

import de.remadisson.opws.arena.ArenaSetup;
import de.remadisson.opws.commands.SetupCommand;
import de.remadisson.opws.files;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener {

    private final String prefix = files.prefix;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        UUID uuid = e.getPlayer().getUniqueId();

        if(SetupCommand.ArenaSetup.containsKey(player)){
           e.setCancelled(true);
            ArenaSetup setup = SetupCommand.ArenaSetup.get(player);

            if(!e.getMessage().equalsIgnoreCase("next") && !e.getMessage().equalsIgnoreCase("abort")){
                player.sendMessage(prefix + "§cThis is not required of your step!");
                player.sendMessage(prefix + "§7If you want to abort, type 'abort'!");
                return;
            }

            if(e.getMessage().equalsIgnoreCase("abort")){
                SetupCommand.ArenaSetup.remove(player);
                player.sendMessage(prefix + "§eYou're free now!");
                return;
            }

            switch(setup.getStep()){
                case 0:
                    setup.nextStep();
                    player.sendMessage(prefix + "§eNow go to the Spawn for the §6Viewer§e, and then type 'next', be aware of your head's position!");
                    return;
                case 1:
                    setup.setViewerSpawn(player.getLocation());
                    setup.nextStep();
                    player.sendMessage(prefix + "§eNow go to the Spawn for the §6deadPlayers§e, and then type 'next', be aware of your head's position!");

                    return;
                case 2:
                    setup.setDeadPlayerSpawn(player.getLocation());
                    setup.nextStep();
                    player.sendMessage(prefix + "§eNow go to the Spawn for the §6exitSpawn§e, and then type 'next', be aware of your head's position!");
                    return;
                case 3:
                    setup.setExitSpawn(player.getLocation());
                    setup.nextStep();
                    player.sendMessage(prefix + "§eNow go to the Spawn for the §6Team1§e, and then type 'next', be aware of your head's position!");
                    return;
                case 4:
                    setup.setSpawnTeam1(player.getLocation());
                    setup.nextStep();
                    player.sendMessage(prefix + "§eNow go to the Spawn for the §6Team2§e, and then type 'next', be aware of your head's position!");
                    return;
                case 5:
                    setup.setSpawnTeam2(player.getLocation());
                    player.sendMessage(prefix + "§eNow step on a pressure plate, where §6Team 1 §ewill join!");
                    setup.nextStep();
                    /*
                    player.sendMessage(prefix + "§eAll done! §6" + setup.getName() + " is now setup!");
                    files.arenaManager.put(setup.getName(), new ArenaManager(setup.getName(), setup.getViewerSpawn(), setup.getDeadPlayerSpawn(), setup.getExitSpawn(), setup.getJoinTeam1(), setup.getJoinTeam2(), setup.getSpawnTeam1(), setup.getSpawnTeam2()));
                    SetupCommand.ArenaSetup.remove(player);
                    */
                    return;
                default:
                    player.sendMessage(prefix + "§4An error encountered! We don't know how, and don't even know what happend. Sorry :(");
            }
        }

        String message = e.getMessage().trim().replace("  ", "");

        if(e.getPlayer().hasPermission("chat.color")){
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        if(e.getMessage().trim().length() > 0) {
            e.setFormat(files.getChatFormat(uuid) + " §8» §r" + message);
        }
    }
}
