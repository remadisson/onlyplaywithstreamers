package de.remadisson.opws.enums;

import org.bukkit.Location;

import java.util.UUID;

public class Warp {

    private final String name;
    private final Location location;
    private final UUID owner;
    private boolean available = true;

    public Warp(String name, Location location, UUID owner, boolean available){
        this.name = name.toLowerCase();
        this.location = location;
        this.owner = owner;
        this.available = available;
    }

    public String getName(){
        return name;
    }

    public String getFirstUpperName(){
        return name.replaceFirst(name.substring(0,1), name.substring(0,1).toUpperCase());
    }

    public Location getLocation(){
        return location;
    }

    public UUID getOwner(){
        return owner;
    }

    public boolean getAvailable(){
        return available;
    }

    public void setAvailable(boolean available){
        this.available = available;
    }

}
