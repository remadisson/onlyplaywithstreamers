package de.remadisson.opws;

import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.enums.Warp;
import de.remadisson.opws.enums.WorkerState;
import de.remadisson.opws.manager.*;
import net.minecraft.server.v1_16_R2.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;


import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class files {

    public static Executor pool = Executors.newFixedThreadPool(10);

    public static String prefix = "§8» §r";
    public static String console = "§eOPWS " + prefix;
    public static String debug = "§7[§dDEBUG§7] " + console;

    public static ServerState state = ServerState.STARTUP;

    private static final String folder = "./plugins/OnlyPlayWithStreamers";
    private static final String dataFolder = folder + "/data";
    public static final FileAPI config = new FileAPI("config.yml", folder);
    public static final FileAPI players = new FileAPI("players.yml", dataFolder);
    public static final FileAPI warps = new FileAPI("warps.yml", dataFolder);
    public static final FileAPI arenaFile = new FileAPI("arenas.yml", dataFolder);

    public static StreamerManager streamerManager = new StreamerManager(players);
    public static CityManager cityManager = new CityManager(warps);
    public static WarpManager warpManager = new WarpManager(warps);
    public static HashMap<String, WorldManager> worldManager = new HashMap<>();
    public static HashMap<String, ArenaManager> arenaManager = new HashMap<>();

    public static final HashMap<UUID, String> namecache = new HashMap<>();

    public static WorkerState workerState = WorkerState.valueOf(config.getDefault("workerState", "maintenance").toString().toUpperCase());
    public static boolean maintenance = Boolean.parseBoolean(config.getDefault("maintenance", false).toString());

    public static void loadFiles(){
        streamerManager.load();
        warpManager.load();
        cityManager.load();
        ArenaManager.load();
    }

    public static void disableFiles(){
        streamerManager.save();
        warpManager.save();
        cityManager.save();
        ArenaManager.save();

        config.set("maintenance", maintenance);
        config.set("workerState", workerState.name());
        config.save();
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
                return adminprefix;
            }
            return adminprefix;
        } else if(streamerManager.getWorker().contains(uuid)){
            if(uuid.equals(MaikEagle)){
                return allowedprefix;
            }
            return allowedprefix;
        } else if(streamerManager.getStreamer().contains(uuid)){
            if(uuid.equals(MaikEagle)){
                return "§5§lS§d§lT§5§lR§d§lE§5§lA§d§lM§5§lE§d§lR §5";
            }
            return streamerprefix;
        } else {
            return playerprefix;
        }
    }

    public static EnumChatFormat getColor(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(player.isOp() && (!player.getUniqueId().equals(MaikEagle) || !streamerManager.getStreamer().contains(uuid))){
            return EnumChatFormat.DARK_RED;
        } else if(streamerManager.getWorker().contains(uuid)){
            return EnumChatFormat.AQUA;
        } else if(streamerManager.getStreamer().contains(uuid)){

            if(uuid.equals(MaikEagle)){
               return EnumChatFormat.DARK_PURPLE;
            }

            return EnumChatFormat.DARK_PURPLE;
        } else {
            return EnumChatFormat.GREEN;
        }
    }

    public static Integer getLevel(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(player.isOp() && (!player.getUniqueId().equals(MaikEagle) || !streamerManager.getStreamer().contains(uuid))){
            return 0;
        } else if(streamerManager.getWorker().contains(uuid)){
            if(uuid.equals(MaikEagle)){
                return 8;
            }
            return 10;
        } else if(streamerManager.getStreamer().contains(uuid)){

            if(uuid.equals(MaikEagle)){
                return 18;
            }

            return 20;
        } else {

            if(uuid.equals(MaikEagle)){
                return 28;
            }

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
        ArrayList<String> permissions = attachment.getPermissible().getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).collect(Collectors.toCollection(ArrayList::new));


        if(streamerManager.getWorker().contains(uuid) && !player.isOp()) {
            attachment.setPermission("minecraft.command.whitelist", true);
            attachment.setPermission("minecraft.command.ban", true);
            attachment.setPermission("minecraft.command.banlist", true);
            attachment.setPermission("minecraft.command.gamemode", true);
            attachment.setPermission("minecraft.command.teleport", true);
            attachment.setPermission("opws.city", true);
            attachment.setPermission("worldedit.*", true);

        } else if(streamerManager.getStreamer().contains(uuid) && !player.isOp()){

            attachment.setPermission("minecraft.command.whitelist", true);
            attachment.setPermission("opws.city", true);

            if(permissions.contains("minecraft.command.ban")) {
                attachment.setPermission("minecraft.command.ban", false);
            }

            if(permissions.contains("minecraft.command.banlist")) {
                attachment.setPermission("minecraft.command.banlist", false);
            }

            if(permissions.contains("minecraft.command.gamemode")) {
                attachment.setPermission("minecraft.command.gamemode", false);
            }

            if(permissions.contains("minecraft.command.teleport")) {
                attachment.setPermission("minecraft.command.teleport", false);
            }

            if(permissions.contains("worldedit.*")){
                attachment.setPermission("worldedit.*", false);
            }

            // Default Commands, that shouldn't be allowed
            if(permissions.contains("bukkit.command.plugins")) {
                attachment.setPermission("bukkit.command.plugins", false);
            }
            if(permissions.contains("bukkit.command.version")) {
                attachment.setPermission("bukkit.command.version", false);
            }
            if(permissions.contains("bukkit.command.help")) {
                attachment.setPermission("bukkit.command.help", false);
            }
            if(permissions.contains("bukkit.command.me")) {
                attachment.setPermission("bukkit.command.me", false);
            }

        } else if(!player.isOp()){

            if(permissions.contains("opws.city")){
                attachment.setPermission("opws.city", false);
            }

            if(permissions.contains("minecraft.command.whitelist")) {
                attachment.setPermission("minecraft.command.whitelist", false);
            }

            if(permissions.contains("minecraft.command.ban")) {
                attachment.setPermission("minecraft.command.ban", false);
            }

            if(permissions.contains("minecraft.command.banlist")) {
                attachment.setPermission("minecraft.command.banlist", false);
            }

            if(permissions.contains("minecraft.command.gamemode")) {
                attachment.setPermission("minecraft.command.gamemode", false);
            }

            if(permissions.contains("minecraft.command.teleport")) {
                attachment.setPermission("minecraft.command.teleport", false);
            }

            if(permissions.contains("worldedit.*")){
                attachment.setPermission("worldedit.*", false);
            }

            // Default Commands, that shouldn't be allowed

            if(attachment.getPermissible().hasPermission("bukkit.command.plugins")) {
                attachment.setPermission("bukkit.command.plugins", false);
            }
            if(permissions.contains("bukkit.command.version")) {
                attachment.setPermission("bukkit.command.version", false);
            }
            if(permissions.contains("bukkit.command.help")) {
                attachment.setPermission("bukkit.command.help", false);
            }
            if(permissions.contains("minecraft.command.me")) {
                attachment.setPermission("minecraft.command.me", false);
            }

        }

        permissionAttachment.put(uuid, attachment);
    }

    public static void initateWarp(){
        if(!warpManager.contains("spawn")){
            warpManager.addWarp(new Warp("spawn", Bukkit.getWorlds().get(0).getSpawnLocation(), MojangAPI.getPlayerProfile("remadisson").getUUID(), true));
        }

        for(Map.Entry<String, WorldManager> wm : worldManager.entrySet()){
            if(!warpManager.contains(wm.getKey())){
                warpManager.addWarp(new Warp(wm.getValue().get().getName().split("_")[0], wm.getValue().getSpawnPoint(), wm.getValue().get().getUID(), wm.getValue().createWarp()));
            }
        }
    }



}
