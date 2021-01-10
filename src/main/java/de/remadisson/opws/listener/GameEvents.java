package de.remadisson.opws.listener;

import de.remadisson.opws.files;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GameEvents implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e){
        if(e.getEntityType() == EntityType.PRIMED_TNT){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalUseEvent(PlayerPortalEvent e){
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL){
            World world = e.getPlayer().getWorld();
            e.setCancelled(true);
            if(world.getEnvironment() != World.Environment.NETHER){
                e.getPlayer().teleport(files.worldManager.get("nether").getSpawnPoint());
            } else {
                e.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
        }

    }
}
