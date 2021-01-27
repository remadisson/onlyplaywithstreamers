package de.remadisson.opws.manager;

import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.events.PlayerChangePermissionEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class StreamerManager {

    private FileAPI api;
    private ArrayList<UUID> worker = new ArrayList<>();
    private ArrayList<UUID> streamer = new ArrayList<>();

    public StreamerManager(FileAPI fileapi){
        api = fileapi;
    }

    public StreamerManager addStreamer(UUID uuid){
        streamer.add(uuid);
        if(!Bukkit.getOfflinePlayer(uuid).isWhitelisted()) {
            Bukkit.getOfflinePlayer(uuid).setWhitelisted(true);
        }
        Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
        return this;
    }

    public StreamerManager removeStreamer(UUID uuid){
        streamer.remove(uuid);
        Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
        return this;
    }

    public ArrayList<UUID> getStreamer(){
        return streamer;
    }

    public StreamerManager addWorker(UUID uuid){
        worker.add(uuid);
        if(!Bukkit.getOfflinePlayer(uuid).isWhitelisted()){
            Bukkit.getOfflinePlayer(uuid).setWhitelisted(true);
        }
        Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
        return this;
    }

    public StreamerManager removeWorker(UUID uuid){
        worker.remove(uuid);
        Bukkit.getOfflinePlayer(uuid).setWhitelisted(false);

        Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
        return this;
    }

    public ArrayList<UUID> getWorker(){
        return worker;
    }

    public boolean load(){

        streamer = ConfigToList("streamer");
        worker = ConfigToList("worker");

        return false;
    }

    public boolean save(){

        api.setList("streamer", ListToConfig(streamer));
        api.setList("worker", ListToConfig(worker));

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

    public StreamerManager syncWhitelist(){
        for(OfflinePlayer streamer : getStreamer().stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList())){
            if(!streamer.isWhitelisted()){
                streamer.setWhitelisted(true);
                Bukkit.getWhitelistedPlayers().add(streamer);
            }
        }

        for(OfflinePlayer worker : getWorker().stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList())){
            if(!worker.isWhitelisted()){
                worker.setWhitelisted(true);
                Bukkit.getWhitelistedPlayers().add(worker);
            }
        }
        return this;
    }
}
