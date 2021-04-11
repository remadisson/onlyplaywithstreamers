package de.remadisson.opws.api;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class Hologram {

    private final HashMap<Integer, ArmorStand> entityList = new HashMap<>();
    private final String[] lines;
    private final Location location;
    private Location temp_location;
    private final double distance = 0.25;

    public Hologram(String[] Text, Location location){
        lines = Text;
        this.location = new Location(location.getWorld(), location.getX() + 0.5, location.getY() + 0.25, location.getZ() + 0.5);

        createAll();
    }

    public void createAll(){
        temp_location = getLocation();

        if(!entityList.isEmpty()){
            remove();
        }

        for (int line = lines.length-1; line > -1; line--) {
            entityList.put(line, create(temp_location, lines[line]));
            temp_location.add(0, distance, 0);
        }
    }

    public ArmorStand create(Location location, String line){
        ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        entity.setVisible(false);
        entity.setCustomNameVisible(true);
        entity.setCustomName(line);
        entity.setGravity(false);
        entity.setBasePlate(false);
        entity.setRemoveWhenFarAway(false);
        entity.setSmall(false);
        return entity;
    }

    public void remove(){
        for (ArmorStand armorStand : entityList.values()) {
            armorStand.remove();
        }
    }

    public Location getLocation(){
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
    }

    public void update(){
        remove();
        createAll();
    }

    public void updateLine(int line, String lineContext){
        ArmorStand armorStand = entityList.get(line);
        Location location = armorStand.getLocation();
        armorStand.remove();
        entityList.put(line, create(location, lineContext));
    }
}
