package de.remadisson.opws.arena;

import de.remadisson.opws.enums.ArenaState;
import de.remadisson.opws.enums.TeamEnum;
import de.remadisson.opws.files;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaScoreboard {

    private Scoreboard scoreboard;
    private ArenaManager arenaManager;
    private ScoreboardObjective objective;

    /**
     * Scoreboard 1 | Waiting
     * <p>
     * Arena | Lobby
     * ----------------
     * Accepted Players X/10
     * <p>
     * Team Blue 5/5
     * Team Red  5/5
     * -----------------
     * <p>
     * Scoreboard 2 | Fight
     * Time in Sec. / max time 10 minutes per round
     * <p>
     * Best of 3 ( Wins RED / Wins Blue) : insgesamte Runden
     * (Team Red) X vs (Team Blue) X
     */


    public ArenaScoreboard(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
        this.scoreboard = new Scoreboard();
        this.objective = scoreboard.registerObjective(arenaManager.getName(), IScoreboardCriteria.DUMMY, new ChatMessage("dummy"), IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
    }

    public void setScoreboard(Player p) {
        LinkedHashMap<Integer, String> lines = new LinkedHashMap<>();
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        if (arenaManager.getActiveCountdown() != null) {
            int countDown = arenaManager.getCountDownInstance().get(arenaManager.getActiveCountdown());
            int minutes = (int) Math.floor(countDown / 60);
            int seconds = countDown % 60;
            time = "§c" + (minutes > 9 ? "" + minutes : "0" + minutes) + ":" + (seconds > 9 ? "" + seconds : "0" + seconds);
        }

        objective.setDisplayName(new ChatMessage("§e" + arenaManager.getName() + " §8| §b" + time));

        ArenaPlayer arenaPlayer = arenaManager.getPlayer(p);

        switch (arenaManager.getArenaState()) {
            case LOBBY: {
                if (!lines.isEmpty()) {
                    lines.clear();
                }

                int acceptedFighters = Collections.frequency(arenaManager.getArenaFightersList().stream().map(ArenaPlayer::isReady).collect(Collectors.toList()), true);
                int allFighters = arenaManager.getArenaFightersList().size();
                if (arenaPlayer.getTeam() != TeamEnum.SPECTATOR) {
                    lines.put(11, " ");
                    lines.put(10, "§eTeam ");
                    lines.put(9, "§8» " + arenaPlayer.getTeam().getTeamString());
                }
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
                TeamManager blue = arenaManager.getTeamBlue();
                TeamManager red = arenaManager.getTeamRed();
                int roundsPlayed = arenaManager.getRoundsPlayed();
                int maxRounds = arenaManager.getMaximalPlayingRounds();
                int redRemaining = Collections.frequency(red.getArenaPlayerList().stream().map(ArenaPlayer::isDead).collect(Collectors.toList()), false);
                int blueRemaining = Collections.frequency(blue.getArenaPlayerList().stream().map(ArenaPlayer::isDead).collect(Collectors.toList()), false);


                lines.put(11, "§7Best of §b" + maxRounds + "§8(" + red.getTeamEnum().getColor() + red.getWins() + " §7/ " + blue.getTeamEnum().getColor() + blue.getWins() + "§8)");
                lines.put(10, "§7Runde §e" + roundsPlayed + "§8(" + red.getTeamEnum().getColor() + redRemaining + " §7vs " + blue.getTeamEnum().getColor() + blueRemaining + "§8)");
                lines.put(9, " ");
                lines.put(7, "§eTeam");
                lines.put(6, "§8» " + arenaPlayer.getTeam().getTeamString());
                lines.put(5, "  ");
                if (arenaPlayer.getTeam() != TeamEnum.SPECTATOR) {
                    lines.put(4, "§eKills");
                    lines.put(3, "§8» §b" + kills);
                    lines.put(2, "   ");
                }
            }

        }

        sendPacket(p, getPacketObjective(objective, false));
        sendPacket(p, getPacketObjective(objective, true));
        sendPacket(p, new PacketPlayOutScoreboardDisplayObjective(1, objective));

        for (Map.Entry<Integer, String> scoreMap : lines.entrySet()) {
            if (scoreMap.getValue().trim().length() > 0) {
                PacketPlayOutScoreboardScore packetScore = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, arenaManager.getName(), scoreMap.getValue(), scoreMap.getKey());
                sendPacket(p, packetScore);
            } else {
                PacketPlayOutScoreboardScore packetScore = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, arenaManager.getName(), scoreMap.getValue(), scoreMap.getKey());
                sendPacket(p, packetScore);
            }

        }

    }

    public static void sendPacket(Player p, Packet<?> packet) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public void removeScoreboard(Player p) {
        sendPacket(p, getPacketObjective(objective, false));
    }

    private PacketPlayOutScoreboardObjective getPacketObjective(ScoreboardObjective obj, boolean packet) {
        if (packet) {
            // Creating Scoreboard
            return new PacketPlayOutScoreboardObjective(obj, 0);
        } else {
            // Removing Scoreboard
            return new PacketPlayOutScoreboardObjective(obj, 1);
        }
    }
}
