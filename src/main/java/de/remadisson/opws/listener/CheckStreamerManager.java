package de.remadisson.opws.listener;

import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.files;
import de.remadisson.opws.main;
import de.remadisson.opws.manager.StreamerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class CheckStreamerManager {

    private final static StreamerManager streamerManager = files.streamerManager;
    private final static String prefix = files.prefix;
    private final static String console = files.console;

    private static int minutes = 6;
    private static int temp_minutes = minutes;

    private static int cycle_seconds = -2;
    private static int cycle_times = 0;

    public static void doCycle() {
        files.pool.execute(() -> {

            Bukkit.getScheduler().scheduleSyncRepeatingTask(main.getInstance(), () -> {

                cycle_seconds++;

                if(cycle_seconds == -1){
                    CheckStreamerCycle(cycle_times);
                }

                if (cycle_seconds == 10) {
                    cycle_times++;
                    cycle_seconds = 0;
                    CheckStreamerCycle(cycle_times);

                }

                if(cycle_times == 6){
                    cycle_times = 0;
                }

            }, 20, 20);
        });
    }

    public static void CheckStreamerCycle(int seconds) {

        boolean stayOnline = false;

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (streamerManager.getStreamer().contains(online.getUniqueId())) {
                stayOnline = true;
            }
        }

        if (stayOnline) {
            temp_minutes = minutes;

            if (files.state == ServerState.CLOSED) {
                //Bukkit.getServer().getConsoleSender().sendMessage(console + "§eDer Server ist nun geöffnet.");
                Bukkit.broadcastMessage(prefix + "§eDer Server ist nun geöffnet.");
                files.state = ServerState.OPEN;
            }

        } else {
            if (Bukkit.getOnlinePlayers().size() > 0 && files.state == ServerState.OPEN) {
                if(seconds == 6) {
                    temp_minutes = temp_minutes - 1;
                    //Bukkit.getServer().getConsoleSender().sendMessage(console + "§eNoch §6" + temp_minutes + " Minuten§e, bis der Server schließt!");
                    if(temp_minutes > 0) {
                        Bukkit.broadcastMessage(prefix + "§eNoch §6" + temp_minutes + " Minuten§e, bis der Server schließt!");
                    }
                }
            }
        }

        if (temp_minutes == 0) {
            ArrayList<UUID> allowed = streamerManager.getAllowed();

            //Bukkit.getServer().getConsoleSender().sendMessage(console + "§7Es ist nun kein Streamer mehr online, so werden alle Spieler ohne direkte berichtigung gekickt!");
            Bukkit.broadcastMessage(prefix + "§eDer Server ist nun §cgeschlossen!");
            Bukkit.broadcastMessage((prefix + "§7Es ist nun kein Streamer mehr online, so werden alle Spieler ohne direkte berichtigung gekickt!"));

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.getPlayer().isOp() &&!streamerManager.getStreamer().contains(online.getUniqueId())) {
                    online.kickPlayer("§4Du wurdest gekickt!\n§cEs befindet sich derzeit kein Streamer auf dem Server!\n§bBitte komm später vorbei um dem Server beizutreten!");

                }
            }
            temp_minutes = minutes;
            files.state = ServerState.CLOSED;
        }

    }
}
