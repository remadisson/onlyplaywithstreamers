package de.remadisson.opws;

import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.manager.StreamerManager;
import net.minecraft.server.v1_16_R2.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class files {

    public static Executor pool = Executors.newCachedThreadPool();

    public static String prefix = "§8» §r";
    public static String console = "§eOPWS " + prefix;
    public static String debug = "§7[§dDEBUG§7] " + console;

    public static ServerState state = ServerState.CLOSED;

    public static FileAPI fileAPI = new FileAPI("streamer.yml", "./plugins/OnlyPlayWithStreamers");
    public static StreamerManager streamerManager = new StreamerManager(fileAPI);

    public static void loadStreamer(){
        streamerManager.load();
    }

    public static void disableStreamer(){
        streamerManager.save();
    }

    public static final String adminprefix = "§4§lADMIN §4";
    public static final String allowedprefix = "§b§lWORKER §b";
    public static final String streamerprefix = "§5§lSTREAMER §5";
    public static final String playerprefix = "§a";

    public static String getPrefix(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(player.isOp()){
            return adminprefix;
        } else if(streamerManager.getStreamer().contains(uuid)){
            return streamerprefix;
        } else if(streamerManager.getAllowed().contains(uuid)){
            return allowedprefix;
        } else {
            return playerprefix;
        }
    }

    public static EnumChatFormat getColor(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(player.isOp()){
            return EnumChatFormat.DARK_RED;
        } else if(streamerManager.getStreamer().contains(uuid)){
            return EnumChatFormat.DARK_PURPLE;
        } else if(streamerManager.getAllowed().contains(uuid)){
            return EnumChatFormat.AQUA;
        } else {
            return EnumChatFormat.GREEN;
        }
    }

    public static Integer getLevel(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if(player.isOp()){
            return 0;
        } else if(streamerManager.getStreamer().contains(uuid)){
            return 20;
        } else if(streamerManager.getAllowed().contains(uuid)){
            return 10;
        } else {
            return 30;
        }
    }

}
