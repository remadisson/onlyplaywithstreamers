package de.remadisson.opws.arena;

import de.remadisson.opws.enums.TeamEnum;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class ArenaPlayer {

    private Player player;
    private UUID uuid;
    private ItemStack[] inventory;
    private TeamEnum team;
    private boolean isDead = false;
    private boolean isReady = false;
    private int deaths = 0;
    private int kills = 0;
    private int xp;
    private Location joinPoint;


    public ArenaPlayer(UUID uuid, ItemStack[] inventory, int xp, TeamEnum team){
        this.uuid = uuid;
        this.inventory = inventory;
        this.team = team;
        this.player = Bukkit.getPlayer(uuid);
        this.xp = xp;
    }

    public Player getPlayer(){
        return player;
    }

    public UUID getUUID(){
        return uuid;
    }

    public ItemStack[]  getInventory(){
        return inventory;
    }

    public TeamEnum getTeam(){
        return team;
    }

    public ArenaPlayer setDead(boolean isDead){
        this.isDead = isDead;

        ArenaManager arenaManager = ArenaManager.getPlayerArena(uuid);
        Player player = Bukkit.getPlayer(uuid);

        if(isDead){
            player.setHealth(20);
            player.setFoodLevel(100);
            player.teleport(arenaManager.getDeadPlayerSpawn());
            player.getInventory().clear();
        }

        return this;
    }

    public Boolean isDead(){
        return isDead;
    }

    public Integer getDeaths(){
        return deaths;
    }

    public void addDeaths(){
        deaths++;
    }

    public Integer getKills(){
        return kills;
    }

    public void addKills(){
        kills++;
    }

    public void setReady(boolean ready){
        isReady = ready;
    }

    public Boolean isReady(){
        return isReady;
    }

    public Integer getXP(){
        return xp;
    }

    public void setJoinPoint(Location location){
        joinPoint = location;
    }

    @Nullable
    public Location getJoinPoint(){
        return joinPoint;
    }

}
