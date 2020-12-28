package de.remadisson.opws.manager;

import de.remadisson.opws.files;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class WorldManager {

    private String prefix = files.prefix;

    private String directory = "./plugins/../worlds/";
    private String worldname;
    private World.Environment environment;
    private WorldType worldType;
    private int resetDays = 7;
    private long millis = new Date().getTime();

    private World world = null;

    public WorldManager(String worldname, WorldType worldType, World.Environment environment){
        this.worldname = worldname;
        this.environment = environment;
        this.worldType = worldType;

        File dir = new File(directory);
        List<String> folder = Arrays.stream(Objects.requireNonNull(dir.listFiles())).map(File::getName).collect(Collectors.toList());

        if(folder.stream().anyMatch(item -> item.toLowerCase().startsWith(worldname.toLowerCase()))){
            this.worldname = folder.stream().filter(item -> item.toLowerCase().startsWith(worldname.toLowerCase())).collect(Collectors.toList()).get(0);
            millis = Long.parseLong(this.worldname.split("_")[1]);
            world = create(millis, worldname);

        } else {
            long millis = new Date().getTime();
            this.millis = millis;
            world = create(millis, worldname);
            Bukkit.getConsoleSender().sendMessage(files.console + worldname + " : World has been created!");
        }
    }

    private World create(long millis, String worldname){

        worldname += "_" + millis;

        WorldCreator worldCreator = new WorldCreator(worldname);
            worldCreator.environment(environment);
            worldCreator.type(worldType);
        return Bukkit.createWorld(worldCreator);
    }

    public boolean unload(){
        for(Player wp : world.getPlayers()){
            wp.sendMessage(prefix + "§cDie Welt wird entladen!");
            wp.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        Bukkit.getServer().unloadWorld(world, false);
        return Bukkit.getWorld(this.worldname) == null;
    }

    public boolean delete(){
        if(unload()){
            File world_file = new File(directory, worldname);

                for(String one : Arrays.stream(Objects.requireNonNull(world_file.listFiles())).map(File::getName).collect(Collectors.toList())){
                    File inside = new File(world_file, one);
                    if(inside.isDirectory()){
                        for(String two : Arrays.stream(Objects.requireNonNull(inside.listFiles())).map(File::getName).collect(Collectors.toList())){
                            try {
                                Files.deleteIfExists(new File(inside, two).toPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Files.deleteIfExists(inside.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            try {
                Files.deleteIfExists(world_file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }

    public WorldManager setGameRule(GameRule<Object> gameRule, Object value){
        world.setGameRule(gameRule, value);
        return this;
    }

    public World get(){
        return world;
    }

    public Location getSpawnPoint(){
        return world.getSpawnLocation();
    }

    public long getMillis(){
        return millis;
    }

}
