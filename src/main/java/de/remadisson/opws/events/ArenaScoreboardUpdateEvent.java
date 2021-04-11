package de.remadisson.opws.events;

import de.remadisson.opws.arena.ArenaManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaScoreboardUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final ArenaManager arenaManager;

    public ArenaScoreboardUpdateEvent(ArenaManager arenaManager){
        this.arenaManager = arenaManager;
    }

    public ArenaManager getArenaManager(){
        return arenaManager;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
