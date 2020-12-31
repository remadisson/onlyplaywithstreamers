package de.remadisson.opws.manager;

import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.enums.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

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
        api.getSection("warps").set(warpname, null);
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
                                    new Location(Bukkit.getWorld(cs.getString( entry + ".location.world")),
                                            cs.getDouble(entry + ".location.x"),
                                            cs.getDouble(entry + ".location.y"),
                                            cs.getDouble(entry + ".location.z"),
                                            Float.parseFloat(cs.getString(entry + ".location.pitch")),
                                            Float.parseFloat(cs.getString(entry + ".location.yaw"))),
                                    UUID.fromString(api.getValue("warps." + entry + ".owner").toString())
                            ));
                }
            }
        }
        return this;
    }
}
