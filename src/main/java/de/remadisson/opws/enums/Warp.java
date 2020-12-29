package de.remadisson.opws.enums;

import org.bukkit.Location;

import java.util.UUID;

public class Warp {

    private final String name;
    private final Location location;
    private final UUID owner;

    public Warp(String name, Location location, UUID owner){
        this.name = name.toLowerCase();
        this.location = location;
        this.owner = owner;
    }

    public String getName(){
        return name;
    }

    public Location getLocation(){
        return location;
    }

    public UUID getOwner(){
        return owner;
    }

}
