package de.remadisson.opws.arena;

import de.remadisson.opws.enums.TeamEnum;
import de.remadisson.opws.files;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.net.Inet4Address;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaScoreboard {


    private ArenaManager arenaManager;
    private HashMap<Player, PlayerScoreboard> playerScoreMap;

    public ArenaScoreboard(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
        playerScoreMap = new HashMap<>();
    }

    public void setScoreboard(Player p) {
        Scoreboard scoreboard = playerScoreMap.get(p) != null ? playerScoreMap.get(p).getScoreboard() : Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.getObjective(arenaManager.getName()) != null ? scoreboard.getObjective(arenaManager.getName()) : scoreboard.registerNewObjective(arenaManager.getName(), "dummy", "dummy");

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        if (arenaManager.getActiveCountdown() != null) {
            int countDown = arenaManager.getCountDownInstance().get(arenaManager.getActiveCountdown());
            int minutes = (int) Math.floor(countDown / 60);
            int seconds = countDown % 60;
            time = "§c" + (minutes > 9 ? "" + minutes : "0" + minutes) + ":" + (seconds > 9 ? "" + seconds : "0" + seconds);
        }

        objective.setDisplayName("§8» §a" + arenaManager.getName() + " §8| §b" + time);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        ArenaPlayer arenaPlayer = arenaManager.getPlayer(p);
        HashMap<Integer, String> lines = getScores(arenaPlayer);

        ArrayList<Score> scoreList = new ArrayList<>();
        for (Map.Entry<Integer, String> scoreMap : lines.entrySet()) {
            Score score = objective.getScore(scoreMap.getValue());
            score.setScore(scoreMap.getKey());
            scoreList.add(score);
        }

        playerScoreMap.put(p, new PlayerScoreboard(arenaManager, arenaPlayer, scoreboard).setScorboardScores(scoreList));
        p.setScoreboard(scoreboard);
    }

    public void removeScoreboard(Player p) {
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        playerScoreMap.remove(p);
    }

    public void updateTime() {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        if (arenaManager.getActiveCountdown() != null) {
            int countDown = arenaManager.getCountDownInstance().get(arenaManager.getActiveCountdown());
            int minutes = (int) Math.floor(countDown / 60);
            int seconds = countDown % 60;
            time = "§c" + (minutes > 9 ? "" + minutes : "0" + minutes) + ":" + (seconds > 9 ? "" + seconds : "0" + seconds);
        }
        for (PlayerScoreboard playerScoreboard : playerScoreMap.values()) {
            Scoreboard scoreboard = playerScoreboard.getScoreboard();
            Objective obj = scoreboard.getObjective(arenaManager.getName());
            obj.setDisplayName("§8» §a" + arenaManager.getName() + " §8| §b" + time);
        }

    }

    public void updateScoreboardValues(Player p) {
        ArenaPlayer arenaPlayer = arenaManager.getPlayer(p);
        PlayerScoreboard playerScoreboard = playerScoreMap.get(p);
        Scoreboard scoreboard = playerScoreMap.get(p).getScoreboard();
        Objective objective = scoreboard.getObjective(arenaManager.getName());

        HashMap<Integer, String> lines = getScores(arenaPlayer);

        ArrayList<Score> scoreList = playerScoreMap.get(p).getScoreList();
        for (Score score : scoreList) {
            scoreboard.resetScores(score.getEntry());
        }
        scoreList.clear();

        for (Map.Entry<Integer, String> scoreMap : lines.entrySet()) {
            Score score = objective.getScore(scoreMap.getValue());
            score.setScore(scoreMap.getKey());
            scoreList.add(score);
        }

        playerScoreboard.setScorboardScores(scoreList);
        playerScoreMap.put(p, playerScoreboard);

    }

    public HashMap<Integer, String> getScores(ArenaPlayer arenaPlayer) {
        LinkedHashMap<Integer, String> lines = new LinkedHashMap<>();

        switch (arenaManager.getArenaState()) {
            case LOBBY: {

                int acceptedFighters = Collections.frequency(arenaManager.getArenaFightersList().stream().map(ArenaPlayer::isReady).collect(Collectors.toList()), true);
                int allFighters = arenaManager.getArenaFightersList().size();

                lines.put(11, " ");
                lines.put(10, "§eTeam §7(" + arenaManager.getTeamBlue().getTeamEnum().getColor() + arenaManager.getTeamBlue().getPlayerList().size() + " §fvs " + arenaManager.getTeamRed().getTeamEnum().getColor() + arenaManager.getTeamRed().getPlayerList().size() + "§7)");
                lines.put(9, "§8» " + arenaPlayer.getTeam().getTeamString());
                lines.put(8, "  ");
                lines.put(7, "§eAngenommen");
                lines.put(6, "§8» §b" + acceptedFighters + "§8/§7" + allFighters);
                lines.put(5, "   ");
                lines.put(4, "§8» §7Viewer§8: §7" + arenaManager.getViewer().size());
                lines.put(3, "     ");

                break;
            }

            case FIGHT: {

                int kills = arenaPlayer.getKills();
                int deaths = arenaPlayer.getDeaths();
                TeamManager blue = arenaManager.getTeamBlue();
                TeamManager red = arenaManager.getTeamRed();
                int bluesize = blue.getPlayerList().size();
                int redsize = red.getPlayerList().size();
                int minimumIteratorSize = 6;

                lines.put((bluesize + redsize + minimumIteratorSize + 3), " ");
                lines.put((bluesize + redsize + minimumIteratorSize + 2), "§8» " + red.getTeamEnum().getTeamString());
                for (int iterator = (minimumIteratorSize + bluesize + 2); iterator < (bluesize + redsize + minimumIteratorSize + 2); iterator++) {
                    int teamIterator = iterator - minimumIteratorSize - bluesize - 2;
                    ArenaPlayer teamPlayer = red.getArenaPlayerList().get(teamIterator);
                    String playerStatus = teamPlayer.isDead() ? ("§8" + teamPlayer.getPlayer().getName()) : (red.getTeamEnum().getColor() + teamPlayer.getPlayer().getName());
                    lines.put(iterator, playerStatus);
                }

                lines.put((minimumIteratorSize + bluesize + 1), "  ");
                lines.put((minimumIteratorSize + bluesize), "§8» " + blue.getTeamEnum().getTeamString());
                for (int iterator = minimumIteratorSize; iterator < (bluesize + minimumIteratorSize); iterator++) {
                    int teamIterator = iterator - minimumIteratorSize;
                    ArenaPlayer teamPlayer = blue.getArenaPlayerList().get(teamIterator);
                    String playerStatus = teamPlayer.isDead() ? ("§8" + teamPlayer.getPlayer().getName()) : (blue.getTeamEnum().getColor() + teamPlayer.getPlayer().getName());
                    lines.put(iterator, playerStatus);
                }
                lines.put(5, "   ");
                if (arenaPlayer.getTeam() != TeamEnum.SPECTATOR) {
                    lines.put(4, "§8» §eK§7/§eD");
                    lines.put(3, "§8» §b" + kills + "§8/§b" + deaths);
                    lines.put(2, "    ");
                }

            }

        }
        return lines;
    }

}
