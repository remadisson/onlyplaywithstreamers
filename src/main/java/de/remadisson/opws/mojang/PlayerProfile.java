package de.remadisson.opws.mojang;

import java.util.UUID;

public class PlayerProfile {

    private final String name;
    private final UUID uuid;

    public PlayerProfile(String name, UUID uuid){
        this.name = name;
        this.uuid = uuid;
    }

    public String getName(){
        return name;
    }

    public UUID getUUID(){
        return uuid;
    }

    public String getUUIDString(){
        return uuid.toString();
    }

    public String getUUIDwithoutDashes(){
        return uuid.toString().replace("-", "");
    }


}
