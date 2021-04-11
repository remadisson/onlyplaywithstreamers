package de.remadisson.opws.listener;

import de.remadisson.opws.api.MojangAPI;
import de.remadisson.opws.commands.StaffCommand;
import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.enums.WorkerState;
import de.remadisson.opws.files;
import de.remadisson.opws.heaven.HeavenManager;
import de.remadisson.opws.main;
import de.remadisson.opws.manager.BannedPlayer;
import de.remadisson.opws.manager.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.UUID;


public class JoinAndQuitListener implements Listener {

    private static final String prefix = files.prefix;

    @EventHandler
    public void onPreJoin(PlayerLoginEvent e) {

        ArrayList<UUID> worker = files.streamerManager.getWorker();
        ArrayList<UUID> streamer = files.streamerManager.getStreamer();

        if(files.whitelistToggle && !files.whitelist.contains(e.getPlayer().getUniqueId())){
            if(e.getPlayer().isOp()){
                e.allow();
                return;
            }

            e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You are not whitelisted on this server!");
            return;
        }

        if(files.bannedPlayersMap.containsKey(e.getPlayer().getUniqueId())){
            BannedPlayer bannedPlayer = files.bannedPlayersMap.get(e.getPlayer().getUniqueId());
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED, "§cDu wurdest gebannt!\n§7von: " + files.getPrefix(bannedPlayer.getCreator()) + MojangAPI.getPlayerProfile(bannedPlayer.getCreator()).getName()  + "\n§7Grund: §b" + bannedPlayer.getCorrectFormalReason() + "\n\n§7Melde dich bei einem Admin, falls dies ein Fehler sein sollte.");
            return;
        }

        if (files.maintenance) {
            if (!e.getPlayer().isOp() && ((worker.contains(e.getPlayer().getUniqueId()) && files.workerState == WorkerState.OPENED) || !worker.contains(e.getPlayer().getUniqueId()))) {
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cMaintenance §7- §cWartungsarbeiten\n§eWir arbeiten derzeit an dem Server, bitte komm später vorbei!");
            } else {
                e.allow();
            }
            return;
        }

        if (files.state == ServerState.ERROR) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§4Please inform an Admin:\n§cThe server encountered an error!");
            return;
        }

        if (files.state == ServerState.STARTUP) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cPlease wait, the server is currently starting!");
            return;
        }



        if (!streamer.contains(e.getPlayer().getUniqueId()) && !e.getPlayer().isOp()) {
            if (files.state == ServerState.CLOSED) {
                if(worker.contains(e.getPlayer().getUniqueId()) && files.workerState == WorkerState.ALWAYS){
                    e.allow();
                    return;
                }
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§4Du wurdest gekickt!\n§cEs befindet sich derzeit kein Streamer auf dem Server!\n§bBitte komm später vorbei um dem Server beizutreten!");
            }
        }

        if((e.getPlayer().isOp() || streamer.contains(e.getPlayer().getUniqueId()) || worker.contains(e.getPlayer().getUniqueId())) && Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()){
            e.allow();
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ArrayList<UUID> worker = files.streamerManager.getWorker();
        ArrayList<UUID> streamer = files.streamerManager.getStreamer();

        for (Player player : StaffCommand.vanishPlayer) {
            e.getPlayer().hidePlayer(main.getInstance(), player);
        }

        if(files.heavenManager.getHeavenPlayerMap().containsKey(e.getPlayer().getUniqueId())){
            e.getPlayer().getInventory().clear();
            e.getPlayer().teleport(files.heavenManager.getSpawn());
            e.getPlayer().sendMessage(files.prefix + "§7Du befindest dich im §bHimmel,§7 du musst noch §b" + files.heavenManager.getHeavenPlayerMap().get(e.getPlayer().getUniqueId()).getDistanceAsString() + "§7warten!");
        }

        if(HeavenManager.needToTeleport.contains(e.getPlayer().getUniqueId())){
            e.getPlayer().teleport(files.warpManager.getWarp("spawn").getLocation());
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
        }

        if(e.getPlayer().isOp() && !streamer.contains(e.getPlayer().getUniqueId()) && !worker.contains(e.getPlayer().getUniqueId())){
            StaffCommand.setVanishPlayer(e.getPlayer());
            e.getPlayer().sendMessage(prefix + "§7Du bist §bunbsichtbar §7für §aSpieler§7 und §5Streamer!");
            e.getPlayer().setGameMode(GameMode.CREATIVE);
        }

        if(HeavenManager.needToTeleport.contains(e.getPlayer().getUniqueId())){
            e.getPlayer().teleport(files.warpManager.getWarp("spawn").getLocation());
            HeavenManager.needToTeleport.remove(e.getPlayer().getUniqueId());
        }

        if (e.getPlayer().getGameMode() != Bukkit.getDefaultGameMode() && !files.maintenance) {
            if ((!e.getPlayer().isOp() && !worker.contains(e.getPlayer().getUniqueId())) || streamer.contains(e.getPlayer().getUniqueId())) {
                e.getPlayer().setGameMode(Bukkit.getDefaultGameMode());
                e.getPlayer().sendMessage(prefix + "§eYour gamemode has been changed to default!");
            }
        }

        if (e.getPlayer().isOp() && !streamer.contains(e.getPlayer().getUniqueId())) {
            e.setJoinMessage(null);
        } else {
            e.setJoinMessage(prefix + "§a+ " + files.getColor(e.getPlayer().getUniqueId()) + e.getPlayer().getName());
        }

        if (streamer.contains(e.getPlayer().getUniqueId())) {
            if (files.state == ServerState.CLOSED) {
                files.state = ServerState.OPEN;
            }
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            updateHeaderAndFooter(online);
        }

        TablistManager.getInstance().updateTeam(e.getPlayer(), files.getPrefix(e.getPlayer().getUniqueId()), files.getColor(e.getPlayer().getUniqueId()), "", files.getLevel(e.getPlayer().getUniqueId()));

        if (files.namecache.containsKey(e.getPlayer().getUniqueId())) {
            files.namecache.remove(e.getPlayer().getUniqueId());
        }

        files.loadPermissions(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        ArrayList<UUID> streamer = files.streamerManager.getStreamer();
        ArrayList<UUID> worker = files.streamerManager.getWorker();

        if(files.heavenManager.getHeavenPlayerMap().containsKey(e.getPlayer().getUniqueId())){
            e.getPlayer().getInventory().setContents(files.heavenManager.getHeavenPlayerMap().get(e.getPlayer().getUniqueId()).getContents());
        }

        if(StaffCommand.vanishPlayer.contains(e.getPlayer())){
            StaffCommand.vanishPlayer.remove(e.getPlayer());
        }

        if (e.getPlayer().isOp() && !streamer.contains(e.getPlayer().getUniqueId()) && !worker.contains(e.getPlayer().getUniqueId())) {
            e.setQuitMessage(null);
        } else {
            e.setQuitMessage(prefix + "§c- " + files.getColor(e.getPlayer().getUniqueId()) + e.getPlayer().getName());
        }

        if (!files.namecache.containsKey(e.getPlayer().getUniqueId())) {
            files.namecache.put(e.getPlayer().getUniqueId(), MojangAPI.getPlayerProfile(e.getPlayer().getUniqueId()));
        }
        if(files.permissionAttachment.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().removeAttachment(files.permissionAttachment.get(e.getPlayer().getUniqueId()));
        }
        files.permissionAttachment.remove(e.getPlayer().getUniqueId());
        files.pool.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                updateHeaderAndFooter(online);
            }

        });
    }

    public static void updateHeaderAndFooter(Player p) {
        int vanished = StaffCommand.vanishPlayer.size();
        if(StaffCommand.vanishPlayer.contains(p)) {
            p.setPlayerListHeader("§6§lCommunity-Server\n§c§lMaikEagle\n§7Online: §e" + (Bukkit.getOnlinePlayers().size() - vanished) + "§7(§b"+ vanished +"§7)§8/§7" + Bukkit.getServer().getMaxPlayers() + "\n ");
        } else {
            p.setPlayerListHeader("§6§lCommunity-Server\n§c§lMaikEagle\n§7Online: §e" + (Bukkit.getOnlinePlayers().size() - vanished) + "§8/§7" + Bukkit.getServer().getMaxPlayers() + "\n ");
        }
        p.setPlayerListFooter(" \n§eYouTube§7: §eMaikEagle" + "\n§5Twitch§7: §5MaikEaglee");
    }

}
