package de.remadisson.opws.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangePermissionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public PlayerChangePermissionEvent(){}

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
