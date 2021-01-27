package de.remadisson.opws.arena;

import de.remadisson.opws.commands.SetupCommand;
import de.remadisson.opws.enums.TeamEnum;
import de.remadisson.opws.files;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ArenaEvents implements Listener {

    private String prefix = files.prefix;

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if(e.getAction() == Action.PHYSICAL) {
            for (Map.Entry<String, ArenaManager> arenaEntry : files.arenaManager.entrySet()) {
                HashMap<Location, TeamEnum> joinsMap = arenaEntry.getValue().getJoinLocations();

                Player player = e.getPlayer();
                try {
                    Location interaction = Objects.requireNonNull(e.getClickedBlock()).getLocation();

                    for (Location location : joinsMap.keySet()) {
                        if (location.equals(interaction)) {
                            if (SetupCommand.ArenaSetup.containsKey(player)) {
                                player.sendMessage(prefix + "§cThis Location is already in use by §3" + arenaEntry.getValue().getName() + " §7: " + joinsMap.get(location).getColor() + "Team" + joinsMap.get(location).getName());
                                return;
                            }

                            player.sendMessage(files.debug + "§bYou stepped on the PressurePlate of the Arena " + arenaEntry.getValue().getName() + " §7: " + joinsMap.get(location).getColor() + "Team" + joinsMap.get(location).getName());
                        }
                    }

                } catch (NullPointerException ex) {
                    return;
                }

            }
        }

        if (SetupCommand.ArenaSetup.containsKey(e.getPlayer())) {
            if (e.getAction() == Action.PHYSICAL) {
                System.out.println(files.debug + e.getAction());

                Player player = e.getPlayer();
                ArenaSetup setup = SetupCommand.ArenaSetup.get(e.getPlayer());
                e.setUseInteractedBlock(Event.Result.DENY);
                switch (setup.getStep()) {
                    case 6:
                        setup.setJoinTeam1(e.getClickedBlock().getLocation());
                        player.sendMessage(prefix + "§eNow step on a pressure plate, where §6Team 2 §ewill join!");
                        setup.nextStep();
                        return;
                    case 7:
                        setup.setJoinTeam2(e.getClickedBlock().getLocation());
                        player.sendMessage(prefix + "§eAll done! §6" + setup.getName() + " is now setup!");
                        files.arenaManager.put(setup.getName(), new ArenaManager(setup.getName(), setup.getViewerSpawn(), setup.getDeadPlayerSpawn(), setup.getExitSpawn(), setup.getJoinTeam1(), setup.getJoinTeam2(), setup.getSpawnTeam1(), setup.getSpawnTeam2()));
                        SetupCommand.ArenaSetup.remove(player);
                        return;
                    default:
                        player.sendMessage(prefix + "§4An error encountered! We don't know how, and don't even know what happend. Sorry :(");
                }
            }
        }

    }

}
