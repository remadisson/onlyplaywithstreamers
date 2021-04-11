package de.remadisson.opws.listener;

import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.files;
import de.remadisson.opws.heaven.HeavenManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;

public class GameEvents implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntityType() == EntityType.PRIMED_TNT) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalUseEvent(PlayerPortalEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            World world = e.getPlayer().getWorld();
            e.setCancelled(true);
            if (world.getEnvironment() != World.Environment.NETHER) {
                e.getPlayer().teleport(files.worldManager.get("nether").getSpawnPoint());
            } else {
                e.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
        }

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        if(files.heavenManager.getHeavenPlayerMap().containsKey(e.getPlayer().getUniqueId())){
            e.setRespawnLocation(files.heavenManager.getSpawn());
            e.getPlayer().setGameMode(GameMode.ADVENTURE);
            return;
        }

        if(e.getPlayer().getBedSpawnLocation() == null){
            e.setRespawnLocation(files.warpManager.getWarp("spawn").getLocation());
        }
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) {
        if (!ArenaManager.containsPlayer(e.getEntity())) {
            e.setDeathMessage("§8» " + files.getColor(e.getEntity().getUniqueId()) + e.getEntity().getName() + " §7ist gestorben!");

            if (files.streamerManager.getStreamer().contains(e.getEntity().getUniqueId()) || (!e.getEntity().isOp() && !files.streamerManager.getWorker().contains(e.getEntity().getUniqueId()))) {
                if(!files.heavenManager.getHeavenPlayerMap().containsKey(e.getEntity().getUniqueId())) {
                    files.heavenManager.setHeavenPlayer(e.getEntity(), HeavenManager.HeavenDuration.NORMAL);
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (files.heavenManager.getHeavenPlayerMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (files.heavenManager.getHeavenPlayerMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(PlayerHarvestBlockEvent e) {
        Player p = e.getPlayer();
        if (files.heavenManager.getHeavenPlayerMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (files.heavenManager.getHeavenPlayerMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        Player p = e.getPlayer();
        if (files.heavenManager.getHeavenPlayerMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickUp(PlayerAttemptPickupItemEvent e){
        Player p = e.getPlayer();
        if (files.heavenManager.getHeavenPlayerMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
