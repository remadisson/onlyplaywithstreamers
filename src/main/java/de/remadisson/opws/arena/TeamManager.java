package de.remadisson.opws.arena;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.enums.TeamEnum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamManager {

    private final TeamEnum teamEnum;
    private final HashMap<UUID, ArenaPlayer> memberMap;
    private int wins = 0;
    private int loses = 0;

    public TeamManager(TeamEnum teamEnum, HashMap<UUID, ArenaPlayer> memberMap){
        this.teamEnum = teamEnum;
        this.memberMap = memberMap;
    }

    public TeamEnum getTeamEnum() {
        return teamEnum;
    }

    public ArrayList<Player> getPlayerList(){
        return memberMap.values().stream().map(ArenaPlayer::getPlayer).collect(Collectors.toCollection(ArrayList::new));
    }

    public Inventory getPlayerInventory(String playerName){
        PlayerInventory playerInventory = (PlayerInventory) Bukkit.createInventory(null, InventoryType.PLAYER);
        memberMap.get(MojangAPI.getPlayerProfile(playerName).getUUID()).getInventory().forEach((key, value) -> playerInventory.setItem(key, value));
        return playerInventory;
    }

    public boolean containsPlayer(UUID uuid){
        return getPlayerList().stream().map(Player::getUniqueId).anyMatch(item -> item.equals(uuid));
    }

    public boolean containsPlayer(String name){
        return getPlayerList().stream().map(Player::getName).anyMatch(item -> item.equalsIgnoreCase(name));
    }

    public HashMap<UUID, ArenaPlayer> getMemberMap(){
        return memberMap;
    }

    public boolean isAlive(){
        boolean isAlive = false;

        for(Player player : getPlayerList()){
            if(!player.isDead()){
                isAlive = true;
            }
        }

        return isAlive;
    }

    public TeamManager addWin(){
        wins++;
        return this;
    }

    public TeamManager setWins(int wins){
        this.wins = wins;
        return this;
    }

    public int getWins(){
        return wins;
    }

    public TeamManager addLose(){
        loses++;
        return this;
    }

    public TeamManager setLoses(int loses){
        this.loses = loses;
        return this;
    }

    public int getLoses() {
        return loses;
    }
}
