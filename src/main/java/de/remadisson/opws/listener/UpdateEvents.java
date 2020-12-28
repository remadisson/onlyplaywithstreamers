package de.remadisson.opws.listener;

import de.remadisson.opws.events.PlayerChangePermissionEvent;
import de.remadisson.opws.files;
import de.remadisson.opws.manager.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class UpdateEvents implements Listener {

    @EventHandler
    public void onCommandExecute(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().startsWith("/op") || e.getMessage().toLowerCase().startsWith("/deop")) {
            if (e.getPlayer().hasPermission("minecraft.command.op") || e.getPlayer().hasPermission("minecraft.command.deop")) {
                if (e.getMessage().split(" ").length > 1) {
                    Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
                }
            }
        }
    }

    @EventHandler
    public void onServer(ServerCommandEvent e) {
        if (e.getCommand().toLowerCase().startsWith("op") || e.getCommand().toLowerCase().startsWith("deop")) {
            if (e.getCommand().length() > 1) {
                Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
            }
        }
    }

    @EventHandler
    public void onPlayerPermission(PlayerChangePermissionEvent e) {
        files.pool.execute(() -> {
            for (Player online : Bukkit.getOnlinePlayers()) {

                try {
                Thread.sleep(3000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
                files.loadPermissions(online);
                TablistManager.getInstance().updateTeam(online, files.getPrefix(online.getUniqueId()), files.getColor(online.getUniqueId()), "", files.getLevel(online.getUniqueId()));
            }
        });
    }

}
