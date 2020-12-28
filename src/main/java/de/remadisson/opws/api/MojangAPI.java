package de.remadisson.opws.api;

import de.remadisson.opws.files;
import de.remadisson.opws.mojang.JsonUtils;
import de.remadisson.opws.mojang.PlayerProfile;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MojangAPI {

    @Nullable
    public static PlayerProfile getPlayerProfile(String name){
        if(files.namecache.containsValue(name)){
            UUID uuid = null;
            for(Map.Entry<UUID, String> entrys : files.namecache.entrySet()){
                if(entrys.getValue().equalsIgnoreCase(name)){
                    uuid = entrys.getKey();
                }
            }
            assert uuid != null;
            return new PlayerProfile(name, uuid);
        }
        HashMap<String, String> values = JsonUtils.getPlayerInJson(name);
        try {
            files.namecache.put(UUID.fromString(addDashes(values.get("id"))), values.get("name"));
            return new PlayerProfile(values.get("name"), UUID.fromString(addDashes(values.get("id"))));
        }catch(NullPointerException e){
            return null;
        }
    }

    @Nullable
    public static PlayerProfile getPlayerProfile(UUID uuid){
        if(files.namecache.containsKey(uuid)){
            return new PlayerProfile(files.namecache.get(uuid), uuid);
        }

        HashMap<String, String> values = JsonUtils.getPlayerInJson(uuid);

        try{
            files.namecache.put(uuid, values.get("name"));
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
