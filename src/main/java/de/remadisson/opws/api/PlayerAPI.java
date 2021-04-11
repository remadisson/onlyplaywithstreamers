package de.remadisson.opws.api;

import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.files;
import net.minecraft.server.v1_16_R2.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerAPI {

    /**
     * Returns Groups as capitilized String
     * @param uuid
     * @return
     */
    public String getGroup(UUID uuid){
        return files.getGroup(uuid);
    }

    /**
     * Returns Prefix as capititlized String
     * @param uuid
     * @return
     */
    public String getPrefix(UUID uuid){
        return files.getPrefix(uuid);
    }

    /**
     * Returns Enum to transfer the real color
     * @param uuid
     * @return
     */
    public EnumChatFormat getColor(UUID uuid){
        return files.getColor(uuid);
    }

    /**
     * For Preferences of listing experiences
     * @param uuid
     * @return
     */
    public Integer getLevel(UUID uuid){
        return files.getLevel(uuid);
    }

    /**
     * @return
     */
    public ArrayList<UUID> getStreamer(){
        return files.streamerManager.getStreamer();
    }

    /**
     * @return
     */
    public ArrayList<UUID> getWorker(){
        return files.streamerManager.getWorker();
    }

    /**
     * @return
     */
    public ArrayList<UUID> getAdmins(){
        return Bukkit.getOperators().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @param uuid
     * @return
     */
    public boolean isInArena(UUID uuid){
        return ArenaManager.containsPlayer(uuid);
    }

}
