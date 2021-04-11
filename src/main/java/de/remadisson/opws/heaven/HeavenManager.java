package de.remadisson.opws.heaven;

import de.remadisson.opws.files;
import de.remadisson.opws.main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class HeavenManager {

    public static ArrayList<UUID> needToTeleport = new ArrayList<>();

    private Location spawn;
    private HashMap<UUID, HeavenPlayer> heavenPlayerMap = new HashMap<>();

    public HeavenManager(Location spawn) {
        this.spawn = spawn;
    }

    public Location getSpawn() {
        return spawn;
    }

    public HashMap<UUID, HeavenPlayer> getHeavenPlayerMap() {
        return heavenPlayerMap;
    }

    public void setHeavenPlayer(Player player, HeavenDuration durationMillis) {
        HeavenPlayer heavenPlayer = new HeavenPlayer(player.getUniqueId(), new ItemStack[]{} /*player.getInventory().getContents()*/, System.currentTimeMillis(), durationMillis.getDuration());
        getHeavenPlayerMap().put(player.getUniqueId(), heavenPlayer);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
        player.getInventory().clear();
        player.sendMessage(files.prefix + "§7Du befindest dich im §bHimmel,§7 du musst noch §b" + heavenPlayer.getDistanceAsString() + "§7warten!");
        }, 0);
    }

    public boolean checkHeaven(){
            Iterator iterator = new HashMap<>(getHeavenPlayerMap()).entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, HeavenPlayer> entry = (Map.Entry<UUID, HeavenPlayer>) iterator.next();

                if(entry.getValue().isFreeByNow()){
                    removeHeavenPlayer(entry.getValue());
                }

                iterator.remove();
            }

            return getHeavenPlayerMap().isEmpty();
    }

    public void removeHeavenPlayer(HeavenPlayer heavenPlayer){

        Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
        Player player = Bukkit.getPlayer(heavenPlayer.getUUID());

        if(player == null || !player.isOnline()){
            getHeavenPlayerMap().remove(heavenPlayer.getUUID());
            return;
        }
        
        player.getInventory().setContents(heavenPlayer.getContents());
        player.teleport(files.warpManager.getWarp("spawn").getLocation());
        player.sendMessage(files.prefix + "§7Du bist nun wieder am §bLeben§7 sei vorsichtig!");
        player.setGameMode(GameMode.SURVIVAL);
        getHeavenPlayerMap().remove(heavenPlayer.getUUID());

        }, 0);

    }

    public class HeavenPlayer {

        private UUID uuid;
        private ItemStack[] contents;
        private long timeStamp;
        private long durationMillis;

        public HeavenPlayer(UUID uuid, ItemStack[] contents, long timeStamp, long durationMillis) {
            this.uuid = uuid;
            this.contents = contents;
            this.timeStamp = timeStamp;
            this.durationMillis = durationMillis;
        }

        public UUID getUUID() {
            return uuid;
        }

        public ItemStack[] getContents() {
            return contents;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public long getDurationMillis() {
            return durationMillis;
        }

        public boolean isFreeByNow(){
            long finishedTime = timeStamp + durationMillis;
            getDistanceAsString();
            return (finishedTime - System.currentTimeMillis()) <= 0;
        }

        public String getDistanceAsString(){
            long finishedTime = getTimeStamp() + getDurationMillis();
            Calendar old = Calendar.getInstance();
            old.setTimeInMillis(finishedTime);

            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());

            Duration distance = Duration.between(now.toInstant(), old.toInstant());

            int minutes = Math.round(distance.toMinutes());
            int seconds = Math.round(distance.getSeconds() - (minutes * 60L));

            if(minutes <= 0 && seconds <= 0){
                return "§aWarten auf Wiederbelebung..";
            }

            if(minutes <= 0){
                return seconds + " Sekunde" + (seconds > 1 ? "n " : " ");
            }

            if(seconds <= 0){
                return minutes + " Minute" + (minutes > 1 ? "n " : " ");
            }

            return minutes + " Minuten und " + seconds + " Sekunden ";
        }

    }

    public enum HeavenDuration {
        NORMAL(30 * 60 * 1000), ARENA(15 * 60 * 1000);
        private long duration;

        HeavenDuration(long duration) {
            this.duration = duration;
        }

        public long getDuration() {
            return duration;
        }

        public HeavenDuration setCustomDuration(long duration) {
            this.duration = duration;
            return this;
        }
    }


    public void saveHeavenNeedToTeleport(){
        needToTeleport.addAll(getHeavenPlayerMap().keySet());
        files.heavenFile.setList("needToTeleport", ListToConfig(needToTeleport));
        files.heavenFile.save();
    }

    public void loadHeavenNeedToTeleport(){
        needToTeleport = ConfigToList("needToTeleport");
    }

    public ArrayList<UUID> ConfigToList(String list){
        ArrayList<UUID> arrayList = new ArrayList<>();

        for(String userString : files.heavenFile.getStringList(list)){
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



