package de.remadisson.opws.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.files;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PingEvent implements Listener {

    @EventHandler
    public void onServerPing(PaperServerListPingEvent e){
        ServerState state = files.state;

        if(files.maintenance){
            e.setMotd("§c§ka§r§4§lMaintenance §7- §4Wartungarbeiten!§c§ka");
            e.setVersion("§cOFFLINE");
            e.setHidePlayers(true);
            e.setProtocolVersion(1);
            return;
        }

        if(state == ServerState.CLOSED){
            e.setMotd("§c§lGeschlossen §7- §cDerzeit kein Streamer online!");
            e.setVersion("§cOFFLINE");
            e.setHidePlayers(true);
            e.setProtocolVersion(1);
        } else if(state == ServerState.ERROR){
            e.setMotd("§cERROR §4- §cPlease inform an Admin!");
            e.setVersion("§4ERROR");
            e.setProtocolVersion(1);
        } else if(state == ServerState.OPEN){
            e.setMotd("§a§lGeöffnet §f- §eSpiele mit!");
            e.setVersion("§aONLINE");
        } else if(state == ServerState.STARTUP){
            e.setMotd("§bSTARTUP SEQUENCE! - §aSlowdown, give us some time");
            e.setVersion("§bSTARTUP");
            e.setProtocolVersion(1);
        }


    }

}
