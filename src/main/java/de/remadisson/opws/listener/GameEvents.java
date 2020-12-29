package de.remadisson.opws.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class GameEvents implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e){
        if(e.getEntityType() == EntityType.PRIMED_TNT){
            e.setCancelled(true);
        }
    }
}
