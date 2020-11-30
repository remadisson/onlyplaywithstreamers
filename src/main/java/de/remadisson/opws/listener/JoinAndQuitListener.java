package de.remadisson.opws.listener;

import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.files;
import de.remadisson.opws.main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class JoinAndQuitListener implements Listener {

    ArrayList<UUID> streamer = files.streamerManager.getStreamer();
    ArrayList<UUID> allowed = files.streamerManager.getAllowed();

    private static final String prefix = files.prefix;

    @EventHandler
    public void onPreJoin(PlayerLoginEvent e){
        if(files.state == ServerState.CLOSED){
            if(!streamer.contains(e.getPlayer().getUniqueId()) && !allowed.contains(e.getPlayer().getUniqueId()) && !e.getPlayer().isOp()) {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§4Du wurdest gekickt!\n§cEs befindet sich derzeit kein Streamer auf dem Server!\n§bBitte komm später vorbei um dem Server beizutreten!");
            }
        } else if(files.state == ServerState.ERROR){
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§4Please inform a Admin:\n§cThe server encountered an error!");

        } else if(files.state == ServerState.OPEN){
            e.allow();

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if((allowed.contains(e.getPlayer().getUniqueId()) || e.getPlayer().isOp()) && !streamer.contains(e.getPlayer().getUniqueId())){
            e.setJoinMessage(null);
        } else {
            e.setJoinMessage(prefix + "§a+ §5" + e.getPlayer().getName());
        }

        if(streamer.contains(e.getPlayer().getUniqueId())){
            if(files.state == ServerState.CLOSED){
                files.state = ServerState.OPEN;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){

        if((allowed.contains(e.getPlayer().getUniqueId()) || e.getPlayer().isOp()) && !streamer.contains(e.getPlayer().getUniqueId())){
            e.setQuitMessage(null);
        } else {
            e.setQuitMessage(prefix + "§c- §5" + e.getPlayer().getName());
        }


        Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {

        boolean stayOnline = false;
        for(Player online : Bukkit.getOnlinePlayers()){
            if(streamer.contains(online.getUniqueId())){
                stayOnline = true;
            }
        }

        if(!stayOnline){
            Bukkit.broadcastMessage(prefix + "§cDer Server wird in wenigen Minuten geschlossen, sofern kein Streamer mehr joint!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
                boolean streamerOnline = false;
                for(Player online : Bukkit.getOnlinePlayers()){
                    if(streamer.contains(online.getUniqueId())){
                        streamerOnline = true;
                    }
                }
                if(!streamerOnline){
                    Bukkit.broadcastMessage(prefix + "§7Es ist nun kein Streamer mehr online, so werden alle Spieler ohne direkte berichtigung gekickt!");
                    for(Player online : Bukkit.getOnlinePlayers()){
                        if(!allowed.contains(online.getUniqueId()) && !e.getPlayer().isOp()){
                            online.kickPlayer("§4Du wurdest gekickt!\n§cEs befindet sich derzeit kein Streamer auf dem Server!\n§bBitte komm später vorbei um dem Server beizutreten!");

                        }
                    }

                    files.state = ServerState.CLOSED;
                }
            }, 20 * 60 * 5);
        }
        }, 20*4);
    }
}
