package de.remadisson.opws.listener;

import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.files;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingEvent implements Listener {

    @EventHandler
    public void onServerPing(ServerListPingEvent e){
        ServerState state = files.state;

        if(state == ServerState.CLOSED){
            e.setMotd("§c§lGeschlossen §7- §cDerzeit kein Streamer online!");
        } else if(state == ServerState.ERROR){
            e.setMotd("§cERROR §4- §cPlease inform an Admin!");
        } else if(state == ServerState.OPEN){
            e.setMotd("§a§lGeöffnet §f- §eSpiele mit!");
        }

    }

}
