package de.remadisson.opws.listener;

import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.commands.StaffCommand;
import de.remadisson.opws.enums.DiscordWebHookState;
import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.files;
import de.remadisson.opws.heaven.HeavenManager;
import de.remadisson.opws.main;
import de.remadisson.opws.manager.StreamerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class GeneralSchedulerManager {

    private final static StreamerManager streamerManager = files.streamerManager;
    private final static String prefix = files.prefix;
    private final static String console = files.console;

    private static int minutes = 6;
    private static int temp_minutes = minutes;

    private static int cycle_seconds = -2;
    private static int cycle_times = 0;
    private static boolean initiateWarp = false;

    private static int worldcount = 1;

    public static void doCycle() {
        files.pool.execute(() -> {

            Bukkit.getScheduler().scheduleSyncRepeatingTask(main.getInstance(), () -> {

                cycle_seconds++;
                worldcount--;
                if(cycle_seconds == -1){
                    if(!files.maintenance) {
                        CheckStreamerCycle(cycle_times);
                    } else {
                        files.state = ServerState.CLOSED;
                    }
                }

                if (cycle_seconds == 10) {
                    cycle_times++;
                    cycle_seconds = 0;
                    if(!files.maintenance) {
                        CheckStreamerCycle(cycle_times);
                    } else {
                        files.state = ServerState.CLOSED;
                    }

                }

                if(worldcount == 0){
                    if(WorldListener.WorldCycle() || WorldListener.NetherCycle()) initiateWarp = false;
                    WorldListener.CheckArenaReset();
                    worldcount = 60*30;
                }

                if(worldcount == 60*30 - 2 && !initiateWarp){
                    files.loadFiles();
                    files.initateWarp();
                    initiateWarp = true;
                    files.state = ServerState.CLOSED;
                }

                if(worldcount == 2){
                    files.disableFiles();
                }

                if(cycle_times == 6){
                    cycle_times = 0;
                }

                if(!StaffCommand.vanishPlayer.isEmpty()){
                    for (Player player : StaffCommand.vanishPlayer) {
                        if(!ArenaManager.containsPlayer(player)){
                            player.sendActionBar("§7Du bist §b§lunsichtbar");
                        }
                    }
                }

                if(files.heavenManager != null) {
                    if (!files.heavenManager.getHeavenPlayerMap().isEmpty()) {
                        for (HeavenManager.HeavenPlayer heavenPlayer : files.heavenManager.getHeavenPlayerMap().values()) {
                            if (Bukkit.getPlayer(heavenPlayer.getUUID()) != null && Objects.requireNonNull(Bukkit.getPlayer(heavenPlayer.getUUID())).isOnline()) {
                                Objects.requireNonNull(Bukkit.getPlayer(heavenPlayer.getUUID())).sendActionBar("§bHimmelszeit §7〣 §f" + heavenPlayer.getDistanceAsString());
                            }
                        }
                    }
                }

            }, 20, 20);
        });

        files.pool.execute(() -> {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(main.getInstance(), () -> {
                if(files.heavenManager == null){
                    System.out.println(files.debug + (files.warpManager.getWarp("heaven") != null));
                    files.heavenManager = new HeavenManager(files.warpManager.getWarp("heaven") != null ? files.warpManager.getWarp("heaven").getLocation() : files.warpManager.getWarp("spawn").getLocation());
                    files.heavenManager.loadHeavenNeedToTeleport();
                    System.out.println(files.debug + "HeavenManager loaded!");
                } else {
                    files.heavenManager.checkHeaven();
                }

            }, 4*20, 10*30);
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
                files.sendDiscordServerStatus(DiscordWebHookState.OPEN, "remadisson");
            }

        } else {
            if (files.state == ServerState.OPEN) {
                if(seconds == 6) {
                    temp_minutes = temp_minutes - 1;
                    //Bukkit.getServer().getConsoleSender().sendMessage(console + "§eNoch §6" + temp_minutes + " Minuten§e, bis der Server schließt!");
                    if(temp_minutes > 0 && Bukkit.getOnlinePlayers().size() > 0) {
                        Bukkit.broadcastMessage(prefix + "§eNoch §6" + temp_minutes + (temp_minutes > 1 || temp_minutes < -1 ? " Minuten" : " Minute") + "§e bis der Server schließt!");
                    }
                }
            }
        }

        if (temp_minutes == 0) {
            files.sendDiscordServerStatus(DiscordWebHookState.CLOSED, "remadisson");
            Bukkit.broadcastMessage(prefix + "§eDer Server ist nun §cgeschlossen!");
            Bukkit.broadcastMessage((prefix + "§7Es ist nun kein Streamer mehr online, so werden alle Spieler ohne direkte berichtigung gekickt!"));

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.getPlayer().isOp() &&!streamerManager.getStreamer().contains(online.getUniqueId())) {
                    online.kickPlayer("§4Du wurdest gekickt!\n§cEs befindet sich derzeit kein Streamer auf dem Server!\n§bBitte komm später vorbei um dem Server beizutreten!\n§cMaikEagle §8- §6Community-Server");

                }
            }
            temp_minutes = minutes;
            files.state = ServerState.CLOSED;
        }

    }
}
