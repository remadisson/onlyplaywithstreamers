package de.remadisson.opws.manager;

import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.enums.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class CityManager {

    private final FileAPI api;
    private HashMap<String, Warp> cities = new HashMap<>();

    public CityManager(FileAPI fileAPI){
        api = fileAPI;
    }

    public CityManager addCity(Warp city){
        cities.put(city.getName(), city);
        return this;
    }

    public CityManager removeCity(String cityname){
        cities.remove(cityname);
        api.getSection("cities").set(cityname, null);
        return this;
    }

    public boolean isOwner(UUID uuid){
        return cities.values().stream().map(Warp::getOwner).collect(Collectors.toList()).contains(uuid);
    }

    public boolean contains(String citiename){
        return cities.get(citiename) != null;
    }

    public Warp getCity(String citiename){
        return cities.get(citiename);
    }

    public HashMap<String, Warp> getCities(){
        return cities;
    }


    public CityManager save(){

        ConfigurationSection cs = api.getSection("cities");

        for(Map.Entry<String, Warp> entry : cities.entrySet()){
            String key = entry.getKey();
                cs.set(key + ".location.x", entry.getValue().getLocation().getX());
                cs.set(key + ".location.y", entry.getValue().getLocation().getY());
                cs.set(key + ".location.z", entry.getValue().getLocation().getZ());
                cs.set(key + ".location.world", entry.getValue().getLocation().getWorld().getName());
                cs.set(key + ".location.pitch", entry.getValue().getLocation().getPitch());
                cs.set(key + ".location.yaw", entry.getValue().getLocation().getYaw());
                cs.set(key + ".owner", entry.getValue().getOwner().toString());
                cs.set(key + ".available", entry.getValue().getAvailable());

        }

        api.save();

        return this;
    }

    public CityManager load(){
        ConfigurationSection cs = api.getSection("cities");
        if(cs.getKeys(false) != null) {
            for (String entry : cs.getKeys(false)) {
                if (!cities.containsKey(entry)) {
                    cities.put(
                            entry,
                            new Warp(
                                    entry,
                                    new Location(Bukkit.getWorld(Objects.requireNonNull(cs.getString(entry + ".location.world"))),
                                            cs.getDouble(entry + ".location.x"),
                                            cs.getDouble(entry + ".location.y"),
                                            cs.getDouble(entry + ".location.z"),
                                            Float.parseFloat(Objects.requireNonNull(cs.getString(entry + ".location.yaw"))),
                                            Float.parseFloat(Objects.requireNonNull(cs.getString(entry + ".location.pitch")))),
                                    UUID.fromString(api.getValue("cities." + entry + ".owner").toString()),
                                    cs.getBoolean(  entry + ".available")
                            ));
                }
            }
        }
        return this;
    }
}
