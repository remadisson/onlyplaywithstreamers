package de.remadisson.opws.listener;

import de.remadisson.opws.events.PlayerChangePermissionEvent;
import de.remadisson.opws.files;
import de.remadisson.opws.manager.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class UpdateEvents implements Listener {

    @EventHandler
    public void onCommandExecute(PlayerCommandPreprocessEvent e){
        if(e.getMessage().toLowerCase().equals("/op")){
            if(e.getPlayer().hasPermission("minecraft.command.op")){
                Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
            }
        } else if(e.getMessage().toLowerCase().equals("/deop")){
            if(e.getPlayer().hasPermission("minecraft.command.deop")){
                Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
            }
        }

    }

    @EventHandler
    public void onPlayerPermission(PlayerChangePermissionEvent e){
        files.pool.execute(() -> {
            for(Player online : Bukkit.getOnlinePlayers()){
                TablistManager.getInstance().registerTeam(online, files.getPrefix(online.getUniqueId()), files.getColor(online.getUniqueId()), "", files.getLevel(online.getUniqueId()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
    }

}
