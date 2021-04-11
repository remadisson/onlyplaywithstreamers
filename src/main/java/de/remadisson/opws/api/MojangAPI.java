package de.remadisson.opws.api;

import de.remadisson.opws.files;
import de.remadisson.opws.mojang.JsonUtils;
import de.remadisson.opws.mojang.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MojangAPI {

    @Nullable
    public static PlayerProfile getPlayerProfile(String name){
        if(files.namecache.containsValue(name)){
            UUID uuid = null;
            for(Map.Entry<UUID, PlayerProfile> entrys : files.namecache.entrySet()){
                if(entrys.getValue().getName().equalsIgnoreCase(name)){
                    uuid = entrys.getKey();
                }
            }
            assert uuid != null;
            return new PlayerProfile(name, uuid);
        }

        HashMap<String, String> values = JsonUtils.getPlayerInJson(name);
        try {
            PlayerProfile profile = new PlayerProfile(values.get("name"), UUID.fromString(addDashes(values.get("id"))));
            files.namecache.put(UUID.fromString(addDashes(values.get("id"))), profile);
            return profile;
        }catch(NullPointerException e){
            return null;
        }
    }

    @Nullable
    public static PlayerProfile getPlayerProfile(UUID uuid){
        if(files.namecache.containsKey(uuid)){
            return new PlayerProfile(files.namecache.get(uuid).getName(), uuid);
        }

        if(Bukkit.getOfflinePlayer(uuid) != null && Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()){
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            assert offlinePlayer.getName() != null;
            return new PlayerProfile(offlinePlayer.getName(), offlinePlayer.getUniqueId());
        }

        HashMap<String, String> values = JsonUtils.getPlayerInJson(uuid);

        try{
            PlayerProfile profile = new PlayerProfile(values.get("name"), uuid);
            files.namecache.put(uuid, profile);
            return profile;
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
