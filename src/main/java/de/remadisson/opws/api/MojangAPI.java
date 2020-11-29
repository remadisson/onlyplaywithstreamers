package de.remadisson.opws.api;

import de.remadisson.opws.mojang.JsonUtils;
import de.remadisson.opws.mojang.PlayerProfile;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class MojangAPI {

    @Nullable
    public static PlayerProfile getPlayerProfile(String name){
        HashMap<String, String> values = JsonUtils.getPlayerInJson(name);
        try {
            return new PlayerProfile(values.get("name"), UUID.fromString(addDashes(values.get("id"))));
        }catch(NullPointerException e){
            return null;
        }
    }

    @Nullable
    public static PlayerProfile getPlayerProfile(UUID uuid){
        HashMap<String, String> values = JsonUtils.getPlayerInJson(uuid);

        try{
            return new PlayerProfile(values.get("name"), uuid);
        }catch(NullPointerException ex){
            return null;
        }
    }

    public static String addDashes(String uuid){
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");
        return sb.toString();
    }

}
