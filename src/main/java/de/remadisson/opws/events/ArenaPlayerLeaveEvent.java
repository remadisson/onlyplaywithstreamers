package de.remadisson.opws.events;

import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.arena.ArenaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaPlayerLeaveEvent extends Event {

    private static final HandlerList handler = new HandlerList();

    private Player player;
    private ArenaPlayer arenaPlayer;
    private ArenaManager arenaManager;

    public ArenaPlayerLeaveEvent(ArenaManager arenaManager, ArenaPlayer arenaPlayer, Player player){
        this.player = player;
        this.arenaManager = arenaManager;
        this.arenaPlayer = arenaPlayer;
    }

    public Player getPlayer(){
        return player;
    }

    public ArenaPlayer getArenaPlayer(){
        return arenaPlayer;
    }

    public ArenaManager getArenaManager(){
        return arenaManager;
    }

    public static HandlerList getHandlerList(){
        return handler;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }
}
