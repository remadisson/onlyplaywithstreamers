package de.remadisson.opws.arena;

import com.sun.istack.internal.NotNull;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class PlayerScoreboard {

    private ArenaManager arenaManager;
    private ArenaPlayer arenaPlayer;
    private Scoreboard scoreboard;
    private ArrayList<Score> scoreList;

    public PlayerScoreboard(ArenaManager arenaManager, ArenaPlayer arenaPlayer, Scoreboard scoreboard){
            this.arenaManager = arenaManager;
            this.arenaPlayer = arenaPlayer;
            this.scoreboard = scoreboard;
    }

    @NotNull
    public PlayerScoreboard setScorboardScores(ArrayList<Score> scoreList) {
            this.scoreList = scoreList;
            return this;
    }

    public ArrayList<Score> getScoreList(){
        return scoreList;
    }

    public ArenaManager getArenaManager(){
        return arenaManager;
    }

    public ArenaPlayer getArenaPlayer(){
        return arenaPlayer;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
