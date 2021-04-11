package de.remadisson.opws.manager;

import java.util.Locale;
import java.util.UUID;

public class BannedPlayer {

    private UUID uuid;
    private String reason;
    private UUID creator;

    public BannedPlayer(UUID uuid, String reason, UUID creator){
        this.uuid = uuid;
        this.reason = reason;
        this.creator = creator;
    }

    public UUID getUUID(){
        return uuid;
    }

    public String getReason(){
        return reason;
    }

    public String getCorrectFormalReason(){
        return reason.substring(0,1).toUpperCase(Locale.ROOT) + reason.substring(1);
    }

    public UUID getCreator(){
        return creator;
    }

}
