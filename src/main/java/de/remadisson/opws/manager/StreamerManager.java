package de.remadisson.opws.manager;

import de.remadisson.opws.api.FileAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class StreamerManager {

    private FileAPI api;
    private ArrayList<UUID> allowed = new ArrayList<>();
    private ArrayList<UUID> streamer = new ArrayList<>();
    private Set<OfflinePlayer> whitelist = Bukkit.getWhitelistedPlayers();

    public StreamerManager(FileAPI fileapi){
        api = fileapi;
    }

    public StreamerManager addStreamer(UUID uuid){
        streamer.add(uuid);
        return this;
    }

    public StreamerManager removeStreamer(UUID uuid){
        streamer.remove(uuid);
        return this;
    }

    public ArrayList<UUID> getStreamer(){
        return streamer;
    }

    public StreamerManager addAllowed(UUID uuid){
        allowed.add(uuid);
        return this;
    }

    public StreamerManager removeAllowed(UUID uuid){
        allowed.remove(uuid);
        return this;
    }

    public ArrayList<UUID> getAllowed(){
        return allowed;
    }

    public boolean load(){

        streamer = ConfigToList("streamer");
        allowed = ConfigToList("allowed");

        return false;
    }

    public boolean save(){

        api.setList("streamer", ListToConfig(streamer));
        api.setList("allowed", ListToConfig(allowed));

        api.save();

        return true;
    }

    public StreamerManager reload(){
        save();
        api.reload();
        load();
        return this;
    }

    public ArrayList<UUID> ConfigToList(String list){
        ArrayList<UUID> arrayList = new ArrayList<>();

        for(String userString : api.getStringList(list)){
            UUID uuid = UUID.fromString(userString);

            if(!arrayList.contains(uuid)) {
                arrayList.add(uuid);
            }
        }

        return arrayList;
    }

    public ArrayList<String> ListToConfig(ArrayList<UUID> uuids){
        ArrayList<String> arrayList = new ArrayList<>();

        for(UUID uuid : uuids){
            String uuidString = uuid.toString();
            if(!arrayList.contains(uuidString)){
                arrayList.add(uuidString);
            }
        }
     return arrayList;
    }
}
