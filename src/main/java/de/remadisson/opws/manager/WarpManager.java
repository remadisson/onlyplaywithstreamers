package de.remadisson.opws.manager;

import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.enums.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpManager {

    private final FileAPI api;
    private HashMap<String, Warp> warps = new HashMap<>();

    public WarpManager(FileAPI fileAPI){
        api = fileAPI;
    }

    public WarpManager addWarp(Warp warp){
        warps.put(warp.getName(), warp);
        return this;
    }

    public WarpManager removeWarp(String warpname){
        warps.remove(warpname);
        return this;
    }

    public Warp getWarp(String warpname){
        return warps.get(warpname);
    }

    public HashMap<String, Warp> getWarps(){
        return warps;
    }

    public HashMap<String, Warp> ConfigToList(){
        return null;
    }

    public WarpManager save(){

        for(Map.Entry<String, Warp> entry : warps.entrySet()){
            api.set("warps." + entry.getKey() + ".location.x", entry.getValue().getLocation().getBlockX());
            api.set("warps." + entry.getKey() + ".location.y", entry.getValue().getLocation().getBlockY());
            api.set("warps." + entry.getKey() + ".location.z", entry.getValue().getLocation().getBlockZ());
            api.set("warps." + entry.getKey() + ".world", entry.getValue().getLocation().getWorld().getName());
            api.set("warps." + entry.getKey() + ".pitch", entry.getValue().getLocation().getPitch());
            api.set("warps." + entry.getKey() + ".yaw", entry.getValue().getLocation().getYaw());
            api.set("warps." + entry.getKey() + ".owner", entry.getValue().getOwner().toString());
        }
            api.save();
        return this;
    }

    public WarpManager load(){
        for(String entry : api.getStringList("warps")){
            if(!warps.containsKey(entry)){
                warps.put(
                        entry,
                        new Warp(
                                entry,
                                new Location(Bukkit.getWorld(api.getValue("warps." + entry + ".world").toString()),
                                            Double.parseDouble(api.getValue("warps." + entry + ".location.x").toString()),
                                            Double.parseDouble(api.getValue("warps." + entry + ".location.y").toString()),
                                            Double.parseDouble(api.getValue("warps." + entry + ".location.z").toString()),
                                            Float.parseFloat(api.getValue("warps." + entry + ".pitch").toString()),
                                            Float.parseFloat(api.getValue("warps." + entry + ".pitch").toString())),
                                UUID.fromString(api.getValue("warps." + entry + ".owner").toString())
                        ));
            }
        }
        return this;
    }
}
