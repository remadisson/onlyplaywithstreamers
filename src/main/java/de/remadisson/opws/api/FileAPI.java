package de.remadisson.opws.api;

import de.remadisson.opws.files;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FileAPI {

    private String filename;
    private String filepath;

    private File file;
    private YamlConfiguration config;

    public FileAPI(String filename, String filepath){
        this.filename = filename;

        File folder = new File(filepath);

        if(!folder.exists()){
            folder.mkdir();
        }

        this.filepath = filepath;

        file = new File(folder, filename);

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Get File Properties
     * @return
     */

    public String getFilename(){
        return filename;
    }

    public String getPath(){
        return filepath;
    }

    public YamlConfiguration getConfig(){
        return config;
    }

    /**
     * Save/Delete FIles
     * @return
     */

    public boolean deleteFile(){
        return file.delete();
    }

    public FileAPI save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Get/Set Values
     * @param key
     * @param value
     * @return
     */
    public FileAPI addDefault(String key, Object value){
        if(config.get(key) == null){
            config.set(key, value);
        }
        return this;
    }

    public FileAPI set(String key, Object value){
        config.set(key, value);
        return this;
    }

    public Object getValue(String key){
        return config.get(key);
    }

    /**
     * Add/Remove/Get List
     * @param list
     * @param key
     * @return
     */

    public FileAPI addList(String list, String key){
        config.getStringList(list).add(key);
        return this;
    }

    public FileAPI removeList(String list, String key){
        config.getStringList(list).remove(key);
        return this;
    }

    public FileAPI setList(String list, ArrayList<String> newList){
        config.set(list, newList);
        return this;
    }

    public ArrayList<String> getStringList(String list){
        return (ArrayList<String>) config.getStringList(list);
    }

    /*
    * Getting ConfigurationSections
    * */

    public ConfigurationSection getSection(String path){
        if(config.getConfigurationSection(path) == null){
            return config.createSection(path);
        } else {
            return config.getConfigurationSection(path);
        }
    }

    public FileAPI reload(){
        config = YamlConfiguration.loadConfiguration(file);
        return this;
    }

}
