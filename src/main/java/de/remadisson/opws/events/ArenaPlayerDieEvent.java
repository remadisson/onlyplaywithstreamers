package de.remadisson.opws.events;

import com.sun.istack.internal.NotNull;
import de.remadisson.opws.arena.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class ArenaPlayerDieEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private ArenaManager arenaManager;
    private Player victim;
    private Player killer;

    public ArenaPlayerDieEvent(ArenaManager arenaManager, Player victim, Player killer){
        this.victim = victim;
        this.killer = killer;
        this.arenaManager = arenaManager;
    }

    @NotNull
    public Player getVictim() {
        return victim;
    }

    @Nullable
    public Player getKiller(){
        return killer;
    }

    @NotNull
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
