package de.remadisson.opws;

import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.manager.StreamerManager;
import net.minecraft.server.v1_16_R2.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.yaml.snakeyaml.events.CollectionStartEvent;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class files {

    public static Executor pool = Executors.newCachedThreadPool();

    public static String prefix = "§8» §r";
    public static String console = "§eOPWS " + prefix;
    public static String debug = "§7[§dDEBUG§7] " + console;

    public static ServerState state = ServerState.CLOSED;

    public static FileAPI fileAPI = new FileAPI("streamer.yml", "./plugins/OnlyPlayWithStreamers");
    public static StreamerManager streamerManager = new StreamerManager(fileAPI);

    public static final HashMap<UUID, String> namecache = new HashMap<>();

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

    public static HashMap<UUID,PermissionAttachment> permissionAttachment = new HashMap<UUID,PermissionAttachment>();

    public static final UUID MaikEagle = UUID.fromString("9e944d6e-f797-4268-bbe0-b0937af502ca");

    public static String getPrefix(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(player.isOp() && (!player.getUniqueId().equals(MaikEagle) || !streamerManager.getStreamer().contains(uuid))){
            if(uuid.equals(MaikEagle)){
                return adminprefix + "§r§l";
            }
            return adminprefix + "§r";
        } else if(streamerManager.getAllowed().contains(uuid)){
            if(uuid.equals(MaikEagle)){
                return allowedprefix + "§r§l";
            }
            return allowedprefix + "§r";
        } else if(streamerManager.getStreamer().contains(uuid)){
            if(uuid.equals(MaikEagle)){
                return streamerprefix + "§r§l";
            }
            return streamerprefix + "§r";
        } else {
            return playerprefix + "§r";
        }
    }

    public static EnumChatFormat getColor(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(player.isOp() && (!player.getUniqueId().equals(MaikEagle) || !streamerManager.getStreamer().contains(uuid))){
            return EnumChatFormat.DARK_RED;
        } else if(streamerManager.getAllowed().contains(uuid)){
            return EnumChatFormat.AQUA;
        } else if(streamerManager.getStreamer().contains(uuid)){
            return EnumChatFormat.DARK_PURPLE;
        } else {
            return EnumChatFormat.GREEN;
        }
    }

    public static Integer getLevel(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(player.isOp() && (!player.getUniqueId().equals(MaikEagle) || !streamerManager.getStreamer().contains(uuid))){
            return 0;
        } else if(streamerManager.getAllowed().contains(uuid)){
            return 10;
        } else if(streamerManager.getStreamer().contains(uuid)){
            return 20;
        } else {
            return 30;
        }
    }

    public static String getChatFormat(UUID uuid){
        Player p = Bukkit.getPlayer(uuid);
        assert p != null;
        return getPrefix(uuid) + p.getName();
    }

    public static void loadPermissions(Player player){
        UUID uuid = player.getUniqueId();

        if(permissionAttachment.containsKey(uuid)) {
            player.removeAttachment(permissionAttachment.get(uuid));
            files.permissionAttachment.remove(uuid);
        }

        PermissionAttachment attachment = player.addAttachment(main.getInstance());
        System.out.println(player.getName() + ": " + (streamerManager.getStreamer().contains(uuid) && !player.isOp()));
        if(streamerManager.getAllowed().contains(uuid) && !player.isOp()) {
            attachment.setPermission("minecraft.command.whitelist", true);
            attachment.setPermission("minecraft.command.ban", true);
            attachment.setPermission("minecraft.command.banlist", true);
            attachment.setPermission("minecraft.command.gamemode", true);
            attachment.setPermission("minecraft.command.teleport", true);

        } else if(streamerManager.getStreamer().contains(uuid) && !player.isOp()){
            attachment.setPermission("minecraft.command.whitelist", true);
            attachment.unsetPermission("minecraft.command.ban");
            attachment.unsetPermission("minecraft.command.banlist");
            attachment.unsetPermission("minecraft.command.gamemode");
            attachment.unsetPermission("minecraft.command.teleport");

        } else if(!player.isOp()){
            attachment.unsetPermission("minecraft.command.whitelist");
            attachment.unsetPermission("minecraft.command.ban");
            attachment.unsetPermission("minecraft.command.banlist");
            attachment.unsetPermission("minecraft.command.gamemode");
            attachment.unsetPermission("minecraft.command.teleport");
        }

        permissionAttachment.put(uuid, attachment);
    }

}
