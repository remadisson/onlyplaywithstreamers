package de.remadisson.opws.listener;

import de.remadisson.opws.files;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        if(e.getMessage().trim().length() > 0) {
            e.setFormat(files.getChatFormat(uuid) + " §8» §r" + e.getMessage().trim().replace("  ", ""));
        }
    }
}
