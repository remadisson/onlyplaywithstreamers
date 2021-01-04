package de.remadisson.opws.listener;

import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.files;
import de.remadisson.opws.manager.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class JoinAndQuitListener implements Listener {

    ArrayList<UUID> streamer = files.streamerManager.getStreamer();
    ArrayList<UUID> allowed = files.streamerManager.getAllowed();

    private static final String prefix = files.prefix;

    @EventHandler
    public void onPreJoin(PlayerLoginEvent e){

        System.out.println(files.debug + "WHITELISTED >" + e.getPlayer().isWhitelisted());

        if(files.state == ServerState.ERROR) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§4Please inform an Admin:\n§cThe server encountered an error!");
            return;
        }


        if(!streamer.contains(e.getPlayer().getUniqueId()) && !e.getPlayer().isOp()){
            if(files.state == ServerState.CLOSED){
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§4Du wurdest gekickt!\n§cEs befindet sich derzeit kein Streamer auf dem Server!\n§bBitte komm später vorbei um dem Server beizutreten!");
            }
        }

        /* if(files.state == ServerState.CLOSED){

            if(!streamer.contains(e.getPlayer().getUniqueId()) && !e.getPlayer().isOp()) {
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§4Du wurdest gekickt!\n§cEs befindet sich derzeit kein Streamer auf dem Server!\n§bBitte komm später vorbei um dem Server beizutreten!");
            }

        } else if(files.state == ServerState.ERROR){
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§4Please inform a Admin:\n§cThe server encountered an error!");
        } else if(files.state == ServerState.OPEN){

            if(Bukkit.getOnlinePlayers().size() >= (Bukkit.getMaxPlayers()-1)){
                if(e.getPlayer().isOp() || streamer.contains(e.getPlayer().getUniqueId())){
                    e.allow();
                }
            }
        }*/

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(e.getPlayer().isOp() && !streamer.contains(e.getPlayer().getUniqueId())){
            e.setJoinMessage(null);
        } else {
            e.setJoinMessage(prefix + "§a+ " + files.getColor(e.getPlayer().getUniqueId()) + e.getPlayer().getName());
        }

        if(streamer.contains(e.getPlayer().getUniqueId())){
            if(files.state == ServerState.CLOSED){
                files.state = ServerState.OPEN;
            }
        }

        for(Player online : Bukkit.getOnlinePlayers()) {
            updateHeaderAndFooter(online);
        }

        TablistManager.getInstance().updateTeam(e.getPlayer(), files.getPrefix(e.getPlayer().getUniqueId()), files.getColor(e.getPlayer().getUniqueId()), "", files.getLevel(e.getPlayer().getUniqueId()));

        if(files.namecache.containsKey(e.getPlayer().getUniqueId())){
            files.namecache.remove(e.getPlayer().getUniqueId());
        }

        files.loadPermissions(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){

        if(e.getPlayer().isOp() && !streamer.contains(e.getPlayer().getUniqueId())){
            e.setQuitMessage(null);
        } else {
            e.setQuitMessage(prefix + "§c- " + files.getColor(e.getPlayer().getUniqueId()) + e.getPlayer().getName());
        }


        if(!files.namecache.containsKey(e.getPlayer().getUniqueId())){
            files.namecache.put(e.getPlayer().getUniqueId(), e.getPlayer().getName());
        }

        e.getPlayer().removeAttachment(files.permissionAttachment.get(e.getPlayer().getUniqueId()));
        files.permissionAttachment.remove(e.getPlayer().getUniqueId());
        files.pool.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            for (Player online : Bukkit.getOnlinePlayers()){
                updateHeaderAndFooter(online);
            }

        });
    }

    public static void updateHeaderAndFooter(Player p){
        p.setPlayerListHeader("§6§lCommunity-Server\n§c§lMaikEagle\n§7Online: §e" + Bukkit.getOnlinePlayers().size() + "§8/§7" + Bukkit.getServer().getMaxPlayers() + "\n ");
        p.setPlayerListFooter(" \n§eYouTube§7: §eMaikEagle"+ "\n§5Twitch§7: §5MaikEaglee");
    }

}
