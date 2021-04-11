package de.remadisson.opws.mojang;


import de.remadisson.opws.files;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerProfile {

    private final String name;
    private final UUID uuid;
    private String texture = null;
    private String signature = null;

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

    @Nullable
    public String getPlayerTexture(){
        if(texture == null){
            try {
                HashMap<String, String> skinDetails = JsonUtils.getPlayerSkin(uuid);;
                texture = skinDetails.get("texture");
                signature = skinDetails.get("signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return texture;
    }

    public String getPlayerSignature(){
        if(signature == null){
            try {
                HashMap<String, String> skinDetails = JsonUtils.getPlayerSkin(uuid);
                texture = skinDetails.get("texture");
                signature = skinDetails.get("signature");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return signature;
    }

}
