package de.remadisson.opws.arena;

import de.remadisson.opws.enums.TeamEnum;
import de.remadisson.opws.files;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamManager {

    private final TeamEnum teamEnum;
    private HashMap<UUID, ArenaPlayer> memberList = new HashMap<>();
    private int wins = 0;
    private int loses = 0;

    public TeamManager(TeamEnum teamEnum){
        this.teamEnum = teamEnum;
    }

    public TeamEnum getTeamEnum() {
        return teamEnum;
    }

    public ArrayList<Player> getPlayerList(){
        return memberList.values().stream().map(ArenaPlayer::getPlayer).collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<ArenaPlayer> getArenaPlayerList(){
        return new ArrayList<>(memberList.values());
    }

    public boolean containsPlayer(UUID uuid){
        return getPlayerList().stream().map(Player::getUniqueId).anyMatch(item -> item.equals(uuid));
    }

    public boolean containsPlayer(String name){
        return getPlayerList().stream().map(Player::getName).anyMatch(item -> item.equalsIgnoreCase(name));
    }

    public HashMap<UUID, ArenaPlayer> getMemberMap(){
        return memberList;
    }


    public TeamManager addWin(){
        wins++;
        return this;
    }

    public void setWins(int wins){
        this.wins = wins;
    }

    public int getWins(){
        return wins;
    }

    public TeamManager addLose(){
        loses++;
        return this;
    }

    public void setLoses(int loses){
        this.loses = loses;
    }

    public int getLoses() {
        return loses;
    }

    public void addMember(ArenaPlayer arenaPlayer){
        memberList.put(arenaPlayer.getUUID(), arenaPlayer);
    }

    public void setMemberList(HashMap<UUID, ArenaPlayer> arenaPlayers){
        memberList = arenaPlayers;
    }

    public void removeMember(UUID uuid){
        memberList.remove(uuid);
    }

    public boolean isDead(){
        return Collections.frequency(getArenaPlayerList().stream().map(ArenaPlayer::isDead).collect(Collectors.toList()), false) <= 0;
    }
}
