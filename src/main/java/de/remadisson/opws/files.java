package de.remadisson.opws;

import de.remadisson.opws.api.*;
import de.remadisson.opws.arena.ArenaFile;
import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.enums.*;
import de.remadisson.opws.heaven.HeavenManager;
import de.remadisson.opws.manager.*;
import de.remadisson.opws.mojang.PlayerProfile;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R2.EnumChatFormat;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;


import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class files {

    public static Executor pool = Executors.newFixedThreadPool(10);

    public static String prefix = "§8» §r";
    public static String console = "§7〣 §eSystem " + prefix;
    public static String debug = "§7[§dDEBUG§7] " + console;

    public static ServerState state = ServerState.STARTUP;

    private static final String folder = "./plugins/OnlyPlayWithStreamers";
    private static final String dataFolder = folder + "/data";
    public static final FileAPI config = new FileAPI("config.yml", folder);
    public static final FileAPI whitelistAPI = new FileAPI("whitelist.yml", folder);
    public static final FileAPI bannedPlayerAPI = new FileAPI("bannedPlayers.yml", folder);
    public static final FileAPI players = new FileAPI("players.yml", dataFolder);
    public static final FileAPI warps = new FileAPI("warps.yml", dataFolder);
    public static final FileAPI arenaFile = new FileAPI("arenas.yml", dataFolder);
    public static final FileAPI heavenFile = new FileAPI("heaven.yml", dataFolder);

    public static HeavenManager heavenManager;

    public static StreamerManager streamerManager = new StreamerManager(players);
    public static CityManager cityManager = new CityManager(warps);
    public static WarpManager warpManager = new WarpManager(warps);
    public static HashMap<String, WorldManager> worldManager = new HashMap<>();
    public static HashMap<String, ArenaManager> arenaManager = new HashMap<>();

    public static final HashMap<UUID, PlayerProfile> namecache = new HashMap<>();

    public static boolean whitelistToggle = Boolean.parseBoolean(whitelistAPI.getDefault("whitelistToggle", true).toString());
    public static ArrayList<UUID> whitelist = new ArrayList<>(whitelistAPI.getStringList("whitelistedPlayers").stream().map(UUID::fromString).collect(Collectors.toList()));
    public static HashMap<UUID, BannedPlayer> bannedPlayersMap = new HashMap<>();
    public static WorkerState workerState = WorkerState.valueOf(config.getDefault("workerState", "maintenance").toString().toUpperCase());
    public static boolean maintenance = Boolean.parseBoolean(config.getDefault("maintenance", false).toString());
    public static boolean allowWarp = Boolean.parseBoolean(config.getDefault("allowWarp", true).toString());
    public static boolean allowArenaFight = Boolean.parseBoolean(config.getDefault("allowarenafight", true).toString());

    public static void loadFiles() {
        streamerManager.load();
        warpManager.load();
        cityManager.load();
        ArenaFile.load();
        loadBannedPlayers();

    }

    public static void disableFiles() {
        streamerManager.save();
        warpManager.save();
        cityManager.save();
        ArenaFile.save();

        config.set("maintenance", maintenance);
        config.set("workerState", workerState.name());
        config.set("allowWarp", allowWarp);
        config.save();

        whitelistAPI.set("whitelistToggle", whitelistToggle);
        whitelistAPI.set("whitelistedPlayers", whitelist.stream().map(UUID::toString).collect(Collectors.toList()));
        whitelistAPI.save();

        saveBannedPlayers();
        bannedPlayerAPI.save();

        heavenManager.saveHeavenNeedToTeleport();
    }

    public static void despawnHolograms() {
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

    public static HashMap<UUID, PermissionAttachment> permissionAttachment = new HashMap<UUID, PermissionAttachment>();

    public static final UUID MaikEagle = UUID.fromString("9e944d6e-f797-4268-bbe0-b0937af502ca");

    public static String getGroup(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (player.isOp() && (!player.getUniqueId().equals(MaikEagle) || !streamerManager.getStreamer().contains(uuid))) {
            return "ADMIN";
        } else if (streamerManager.getWorker().contains(uuid)) {
            return "WORKER";
        } else if (streamerManager.getStreamer().contains(uuid)) {
            return "STREAMER";
        } else {
            return "SPIELER";
        }
    }

    public static String getPrefix(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (ArenaManager.containsPlayer(uuid)) {
            TeamEnum team = ArenaManager.getArenaPlayer(uuid).getTeam();
            if (team != TeamEnum.SPECTATOR) {
                return team.getColor() + "§l" + team.getName().toUpperCase() + " ";
            }
        }

        if (player.isOp() && (!player.getUniqueId().equals(MaikEagle) || !streamerManager.getStreamer().contains(uuid))) {
            if (uuid.equals(MaikEagle)) {
                return adminprefix;
            }
            return adminprefix;
        } else if (streamerManager.getWorker().contains(uuid)) {
            if (uuid.equals(MaikEagle)) {
                return allowedprefix;
            }
            return allowedprefix;
        } else if (streamerManager.getStreamer().contains(uuid)) {
            if (uuid.equals(MaikEagle)) {
                return "§5§lS§d§lT§5§lR§d§lE§5§lA§d§lM§5§lE§d§lR §5";
            }
            return streamerprefix;
        } else {
            return playerprefix;
        }
    }

    public static EnumChatFormat getColor(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (ArenaManager.containsPlayer(uuid)) {
            TeamEnum team = ArenaManager.getArenaPlayer(uuid).getTeam();
            if (team != TeamEnum.SPECTATOR) {
                return EnumChatFormat.valueOf(team.getColor().name());
            }
        }

        if (player.isOp() && (!player.getUniqueId().equals(MaikEagle) || !streamerManager.getStreamer().contains(uuid))) {
            return EnumChatFormat.DARK_RED;
        } else if (streamerManager.getWorker().contains(uuid)) {
            return EnumChatFormat.AQUA;
        } else if (streamerManager.getStreamer().contains(uuid)) {

            if (uuid.equals(MaikEagle)) {
                return EnumChatFormat.DARK_PURPLE;
            }

            return EnumChatFormat.DARK_PURPLE;
        } else {
            return EnumChatFormat.GREEN;
        }
    }

    public static Integer getLevel(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (ArenaManager.containsPlayer(uuid)) {
            TeamEnum team = ArenaManager.getArenaPlayer(uuid).getTeam();

            if (team == TeamEnum.RED) {
                return 60;
            }

            if (team == TeamEnum.BLUE) {
                return 61;
            }
        }
        if (player.isOp() && (!player.getUniqueId().equals(MaikEagle) || !streamerManager.getStreamer().contains(uuid))) {
            return 0;
        } else if (streamerManager.getWorker().contains(uuid)) {
            if (uuid.equals(MaikEagle)) {
                return 8;
            }
            return 10;
        } else if (streamerManager.getStreamer().contains(uuid)) {

            if (uuid.equals(MaikEagle)) {
                return 18;
            }
            return 20;
        } else {

            if (uuid.equals(MaikEagle)) {
                return 28;
            }
            return 30;
        }
    }

    public static String getChatFormat(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);

        assert p != null;

        if (ArenaManager.containsPlayer(uuid)) {
            ArenaManager arenaManager = ArenaManager.getPlayerArena(uuid);
            TeamEnum team = ArenaManager.getArenaPlayer(uuid).getTeam();
            if (team != TeamEnum.SPECTATOR) {
                return ChatColor.AQUA + arenaManager.getName() + " §8| " + team.getColor() + ChatColor.BOLD + team.getName().toUpperCase() + "§r " + team.getColor() + p.getName();
            } else {
                return ChatColor.AQUA + arenaManager.getName() + " §8| " + getPrefix(uuid) + p.getName();
            }
        }

        return getPrefix(uuid) + p.getName();
    }

    public static void loadPermissions(Player player) {
        UUID uuid = player.getUniqueId();


        try {
            if (permissionAttachment.containsKey(uuid)) {
                player.removeAttachment(permissionAttachment.get(uuid));
                files.permissionAttachment.remove(uuid);
            }
        } catch (IllegalArgumentException ignored) {
        }

        PermissionAttachment attachment = player.addAttachment(main.getInstance());
        ArrayList<String> permissions = attachment.getPermissible().getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).collect(Collectors.toCollection(ArrayList::new));


        if (streamerManager.getWorker().contains(uuid) && !player.isOp()) {
            attachment.setPermission("minecraft.command.whitelist", true);
            attachment.setPermission("opws.ban", true);
            attachment.setPermission("opws.kick", true);
            attachment.setPermission("opws.banlist", true);
            attachment.setPermission("minecraft.command.gamemode", true);
            attachment.setPermission("minecraft.command.teleport", true);
            attachment.setPermission("opws.city", true);
            attachment.setPermission("opws.staff", true);
            attachment.setPermission("opws.warp", true);

        } else if (streamerManager.getStreamer().contains(uuid) && !player.isOp() && !streamerManager.getWorker().contains(uuid)) {

            attachment.setPermission("minecraft.command.whitelist", true);
            attachment.setPermission("opws.city", true);

            if (permissions.contains("opws.ban")) {
                attachment.setPermission("opws.ban", false);
            }

            if (permissions.contains("opws.warp")) {
                attachment.setPermission("opws.warp", false);
            }

            if (permissions.contains("opws.banlist")) {
                attachment.setPermission("opws.banlist", false);
            }

            if (permissions.contains("minecraft.command.gamemode")) {
                attachment.setPermission("minecraft.command.gamemode", false);
            }

            if (permissions.contains("minecraft.command.teleport")) {
                attachment.setPermission("minecraft.command.teleport", false);
            }

            if (permissions.contains("opws.staff")) {
                attachment.setPermission("opws.staff", false);
            }

            if (permissions.contains("opws.kick")) {
                attachment.setPermission("opws.kick", false);
            }

            // Default Commands, that shouldn't be allowed
            if (permissions.contains("bukkit.command.plugins")) {
                attachment.setPermission("bukkit.command.plugins", false);
            }
            if (permissions.contains("bukkit.command.version")) {
                attachment.setPermission("bukkit.command.version", false);
            }
            if (permissions.contains("bukkit.command.help")) {
                attachment.setPermission("bukkit.command.help", false);
            }
            if (permissions.contains("bukkit.command.me")) {
                attachment.setPermission("bukkit.command.me", false);
            }

        } else if (!player.isOp()) {

            if (permissions.contains("opws.city")) {
                attachment.setPermission("opws.city", false);
            }

            if (permissions.contains("minecraft.command.whitelist")) {
                attachment.setPermission("minecraft.command.whitelist", false);
            }

            if (permissions.contains("opws.ban")) {
                attachment.setPermission("opws.ban", false);
            }

            if (permissions.contains("opws.banlist")) {
                attachment.setPermission("opws.banlist", false);
            }

            if (permissions.contains("minecraft.command.gamemode")) {
                attachment.setPermission("minecraft.command.gamemode", false);
            }

            if (permissions.contains("minecraft.command.teleport")) {
                attachment.setPermission("minecraft.command.teleport", false);
            }

            if (permissions.contains("opws.staff")) {
                attachment.setPermission("opws.staff", false);
            }

            if (permissions.contains("opws.city")) {
                attachment.setPermission("opws.city", false);
            }

            if (permissions.contains("opws.kick")) {
                attachment.setPermission("opws.kick", false);
            }

            // Default Commands, that shouldn't be allowed

            if (attachment.getPermissible().hasPermission("bukkit.command.plugins")) {
                attachment.setPermission("bukkit.command.plugins", false);
            }
            if (permissions.contains("bukkit.command.version")) {
                attachment.setPermission("bukkit.command.version", false);
            }
            if (permissions.contains("bukkit.command.help")) {
                attachment.setPermission("bukkit.command.help", false);
            }
            if (permissions.contains("minecraft.command.me")) {
                attachment.setPermission("minecraft.command.me", false);
            }

        }

        permissionAttachment.put(uuid, attachment);
    }

    public static void initateWarp() {
        if (!warpManager.contains("spawn")) {
            warpManager.addWarp(new Warp("spawn", Bukkit.getWorlds().get(0).getSpawnLocation(), MojangAPI.getPlayerProfile("remadisson").getUUID(), true));
        }

        for (Map.Entry<String, WorldManager> wm : worldManager.entrySet()) {
            if (!warpManager.contains(wm.getKey())) {
                warpManager.addWarp(new Warp(wm.getValue().get().getName().split("_")[0], wm.getValue().getSpawnPoint(), wm.getValue().get().getUID(), wm.getValue().createWarp()));
            }
        }
    }

    public static void sendDiscordServerStatus(DiscordWebHookState webHookState, String initator) {
        DiscordWebhook discord = new DiscordWebhook("https://discord.com/api/webhooks/808432353457995806/ZLpR_H5K78k466CEgmFKhBV2Mn4Xsfrqz_f7WazYxcWtgIhlgFqtfIok-jEJ_gAd_VmX");
        switch (webHookState) {
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

    public static void sendDiscordPunishment(PunishmentEnum punishmentEnum, BannedPlayer bannedPlayer) {
        files.pool.execute(() ->{
        DiscordWebhook discord = new DiscordWebhook("https://discord.com/api/webhooks/811693821141712940/Gu88z0u1cqVuueFUoWx2VxUb-o2vePHefu9ceZjSVqaMT_tJDGc_8sUCphzAD5VicdES");

            PlayerProfile target = MojangAPI.getPlayerProfile(bannedPlayer.getUUID());
            PlayerProfile creator = MojangAPI.getPlayerProfile(bannedPlayer.getCreator());
            String reason = bannedPlayer.getReason();

            switch (punishmentEnum) {
                case BAN:
                    discord.addEmbed(new DiscordWebhook.EmbedObject().setTitle(target.getName() + " has been banned!")
                            .setColor(Color.RED)
                            .addField("PunishmentType", punishmentEnum.name(), true)
                            .addField("Victim", getGroup(target.getUUID()) +  " " + target.getName() + " (" + target.getUUID() + ")", false)
                            .addField("Creator", getGroup(creator.getUUID()) + " " + creator.getName() + " (" + creator.getUUID() + ")", false)
                            .addField("Reason", reason, false).setAuthor(creator.getName(), null, null));
                    break;
                case UNBAN:
                    discord.addEmbed(new DiscordWebhook.EmbedObject().setTitle(target.getName() + " has been unbanned!")
                            .setColor(Color.ORANGE)
                            .addField("PunishmentType", punishmentEnum.name(), true)
                            .addField("Victim", getGroup(target.getUUID()) +  " " + target.getName() + " (" + target.getUUID() + ")", false)
                            .addField("Initiator", getGroup(creator.getUUID()) + " " + creator.getName() + " (" + creator.getUUID() + ")", false).setAuthor(creator.getName(), null, null));
                    break;
                case KICK:
                    discord.addEmbed(new DiscordWebhook.EmbedObject().setTitle(target.getName() + " has been kicked! (" + new SimpleDateFormat("dd/MM/yy - HH:mm:ss").format(new Date()) + ")")
                            .setColor(Color.YELLOW)
                            .addField("PunishmentType", punishmentEnum.name(), true)
                            .addField("Victim", getGroup(target.getUUID()) +  " " + target.getName() + " (" + target.getUUID() + ")", false)
                            .addField("Creator", getGroup(creator.getUUID()) + " " + creator.getName() + " (" + creator.getUUID() + ")", false)
                            .addField("Reason", reason, false).setAuthor(creator.getName(), null, null));
            }

            try {
                discord.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendDiscordWorldReset(World world) {
        files.pool.execute(() ->{
            DiscordWebhook discord = new DiscordWebhook("https://discord.com/api/webhooks/820642728391082014/OeRF0eOwZ1tHRzg-RYYgxvsQqcJSmzP6gQAuFJBejLigJ42Ww0wkZj7EKKh8wULUOOIY");
                    discord.addEmbed(new DiscordWebhook.EmbedObject().setTitle("WorldReset")
                            .setColor(Color.YELLOW)
                            .addField("World", world.getName(), true)
                            .addField("PlayersKicked" , world.getPlayerCount() + "", true)
                            .addField("ChunksLoaded", world.getLoadedChunks().length + "", true)
                            .addField("Seed", world.getSeed() + "" , true)
                            .addField("Loaded Entities", world.getEntities().size() + "", true)
                            .setAuthor("System", null, null));
            try {
                discord.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void loadBannedPlayers() {
        ConfigurationSection section = bannedPlayerAPI.getSection("banned");

        for (String key : section.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            UUID creator = UUID.fromString(Objects.requireNonNull(section.getString(key + ".creator")));
            String reason = Objects.requireNonNull(section.getString(key + ".reason"));
            bannedPlayersMap.put(uuid, new BannedPlayer(uuid, reason, creator));
        }
    }

    public static void saveBannedPlayers() {
        ConfigurationSection section = bannedPlayerAPI.getSection("banned");
        for (BannedPlayer bannedPlayer : bannedPlayersMap.values()) {
            section.set(bannedPlayer.getUUID().toString() + ".creator", bannedPlayer.getCreator().toString());
            section.set(bannedPlayer.getUUID().toString() + ".reason", bannedPlayer.getReason());
        }
    }

}
