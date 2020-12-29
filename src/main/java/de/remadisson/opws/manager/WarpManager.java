package de.remadisson.opws.manager;

import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.enums.Warp;
import de.remadisson.opws.files;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public boolean contains(String warpname){
        return warps.get(warpname) != null;
    }

    public Warp getWarp(String warpname){
        return warps.get(warpname);
    }

    public HashMap<String, Warp> getWarps(){
        return warps;
    }


    public WarpManager save(){

        ConfigurationSection cs = api.getSection("warps");

        for(Map.Entry<String, Warp> entry : warps.entrySet()){
            String key = entry.getKey();

            cs.set(key + ".location.x", entry.getValue().getLocation().getBlockX());
            cs.set(key + ".location.y", entry.getValue().getLocation().getBlockY());
            cs.set(key + ".location.z", entry.getValue().getLocation().getBlockZ());
            cs.set(key + ".location.world", entry.getValue().getLocation().getWorld().getName());
            cs.set(key + ".location.pitch", entry.getValue().getLocation().getPitch());
            cs.set(key + ".location.yaw", entry.getValue().getLocation().getYaw());
            cs.set(key + ".owner", entry.getValue().getOwner().toString());

        }

        api.save();

        return this;
    }

    public WarpManager load(){
        ConfigurationSection cs = api.getSection("warps");
        if(cs.getKeys(false) != null) {
            for (String entry : cs.getKeys(false)) {
                if (!warps.containsKey(entry)) {
                    warps.put(
                            entry,
                            new Warp(
                                    entry,
                                    new Location(Bukkit.getWorld(api.getValue("warps." + entry + ".location.world").toString()),
                                            Double.parseDouble(api.getValue("warps." + entry + ".location.x").toString()),
                                            Double.parseDouble(api.getValue("warps." + entry + ".location.y").toString()),
                                            Double.parseDouble(api.getValue("warps." + entry + ".location.z").toString()),
                                            Float.parseFloat(api.getValue("warps." + entry + ".location.pitch").toString()),
                                            Float.parseFloat(api.getValue("warps." + entry + ".location.yaw").toString())),
                                    UUID.fromString(api.getValue("warps." + entry + ".owner").toString())
                            ));
                }
            }
        }
        return this;
    }
}
