package de.remadisson.opws;

import de.remadisson.opws.api.DiscordWebhook;
import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.arena.ArenaFile;
import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.enums.*;
import de.remadisson.opws.manager.*;
import io.netty.handler.codec.http.HttpResponse;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R2.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import sun.net.www.http.HttpClient;


import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
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
    public static boolean allowWarp = Boolean.parseBoolean(config.getDefault("allowWarp", true).toString());

    public static void loadFiles(){
        streamerManager.load();
        warpManager.load();
        cityManager.load();
        ArenaFile.load();
    }

    public static void disableFiles(){
        streamerManager.save();
        warpManager.save();
        cityManager.save();
        ArenaFile.save();

        config.set("maintenance", maintenance);
        config.set("workerState", workerState.name());
        config.set("allowWarp", allowWarp);
        config.save();
    }

    public static void despawnHolograms(){
        arenaManager.forEach((key, value) -> {
            for (Hologram hologram : value.getHolograms().values()) {
                hologram.remove();
            }
        });
    }

    public static final String adminprefix = "§4§lADMIN §4";
    public static final String allowedprefix = "§b§lWORKER §b";
    public static final String streamerprefix = "§5§lSTREAMER §5";
    public static final String playerprefix = "§a";

    public static HashMap<UUID,PermissionAttachment> permissionAttachment = new HashMap<UUID,PermissionAttachment>();

    public static final UUID MaikEagle = UUID.fromString("9e944d6e-f797-4268-bbe0-b0937af502ca");

    public static String getPrefix(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if(ArenaManager.containsPlayer(uuid)){
            TeamEnum team = ArenaManager.getArenaPlayer(uuid).getTeam();
            return team.getColor() + "§l" + team.getName().toUpperCase() + " ";
        }

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

        if(ArenaManager.containsPlayer(uuid)){
            TeamEnum team = ArenaManager.getArenaPlayer(uuid).getTeam();
            return EnumChatFormat.valueOf(team.getColor().name());
        }

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

        if(ArenaManager.containsPlayer(uuid)){
            TeamEnum team = ArenaManager.getArenaPlayer(uuid).getTeam();

            if(team == TeamEnum.RED){
             return 60;
            }

            if(team == TeamEnum.BLUE){
                return 61;
            }

            if(team == TeamEnum.SPECTATOR){
                return 62;
            }
        }
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

        if(ArenaManager.containsPlayer(uuid)) {
            ArenaManager arenaManager = ArenaManager.getPlayerArena(uuid);
            TeamEnum team = ArenaManager.getArenaPlayer(uuid).getTeam();
            return ChatColor.AQUA + arenaManager.getName() + " §8| " + team.getColor() + ChatColor.BOLD + team.getName().toUpperCase() + "§r " + team.getColor() + p.getName();
        }

        return getPrefix(uuid) + p.getName();
    }

    public static void loadPermissions(Player player){
        UUID uuid = player.getUniqueId();


        try {
            if (permissionAttachment.containsKey(uuid)) {
                player.removeAttachment(permissionAttachment.get(uuid));
                files.permissionAttachment.remove(uuid);
            }
        } catch(IllegalArgumentException ignored){}

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

    public static void sendDiscordWebhook(DiscordWebHookState webHookState, String initator){
        DiscordWebhook discord = new DiscordWebhook("https://discord.com/api/webhooks/808432353457995806/ZLpR_H5K78k466CEgmFKhBV2Mn4Xsfrqz_f7WazYxcWtgIhlgFqtfIok-jEJ_gAd_VmX");
        switch (webHookState){
            case OPEN:
                discord.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Server-Status")
                        .setAuthor(initator, null, null).setColor(Color.GREEN).addField("Status", webHookState.name(), true).addField("Nachricht", "Der Server kann kann nun von allen betreten werden!", false));
                break;
            case CLOSED:
                discord.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Server-Status")
                        .setAuthor(initator, null, null).setColor(Color.RED).addField("Status", webHookState.name(), true).addField("Nachricht", "Der Server ist nun geschlossen, da kein Streamer mehr online ist!", false));
                break;
            case MAINTENANCE:
                discord.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Server-Status")
                        .setAuthor(initator, null, null).setColor(Color.YELLOW).addField("Status", webHookState.name(), true).addField("Nachricht", "Der Server steht nun unter Wartungsarbeiten!", false));
                break;
            case READY:
                discord.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Server-Status")
                        .setAuthor(initator, null, null).setColor(Color.GREEN).addField("Status", webHookState.name(), true).addField("Nachricht", "Der Server kann nun wieder von einem Streamer geöffnet werden!", false));
        }

        try {
            discord.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
