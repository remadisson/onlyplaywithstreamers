package de.remadisson.opws.arena;

import org.bukkit.Location;

public class ArenaSetup {

    private int step;
    private Location viewerSpawn;
    private Location deadPlayerSpawn;
    private Location exitSpawn;
    private Location joinTeam1;
    private Location joinTeam2;
    private Location spawnTeam1;
    private Location spawnTeam2;
    private String name;

    public ArenaSetup(Integer step, String name){
        this.step = step;
        this.name = name;
    }

    public ArenaSetup nextStep(){
        step++;
        return this;
    }

    public Integer getStep(){
        return step;
    }

    public ArenaSetup setViewerSpawn(Location location){
        viewerSpawn = location;
        return this;
    }

    public ArenaSetup setDeadPlayerSpawn(Location location){
        deadPlayerSpawn = location;
        return this;
    }

    public ArenaSetup setExitSpawn(Location location){
        exitSpawn = location;
        return this;
    }

    public ArenaSetup setJoinTeam1(Location location){
        joinTeam1 = location;
        return this;
    }

    public ArenaSetup setJoinTeam2(Location location){
        joinTeam2 = location;
        return this;
    }
    public ArenaSetup setSpawnTeam1(Location location){
        spawnTeam1 = location;
        return this;
    }

    public ArenaSetup setSpawnTeam2(Location location){
        spawnTeam2 = location;
        return this;
    }

    public Location getViewerSpawn(){
        return viewerSpawn;
    }

    public Location getDeadPlayerSpawn(){
        return deadPlayerSpawn;
    }

    public Location getExitSpawn(){
        return exitSpawn;
    }

    public Location getJoinTeam1(){
        return joinTeam1;
    }

    public Location getJoinTeam2(){
        return joinTeam2;
    }

    public Location getSpawnTeam1(){
        return spawnTeam1;
    }

    public Location getSpawnTeam2(){
        return spawnTeam2;
    }

    public String getName(){
        return name;
    }

}
