package de.remadisson.opws.arena;

import de.remadisson.opws.commands.SetupCommand;
import de.remadisson.opws.enums.ArenaState;
import de.remadisson.opws.enums.CountdownEnum;
import de.remadisson.opws.enums.TeamEnum;
import de.remadisson.opws.events.ArenaPlayerDieEvent;
import de.remadisson.opws.events.ArenaPlayerLeaveEvent;
import de.remadisson.opws.files;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ArenaEvents implements Listener {

    private String prefix = files.prefix;

    @EventHandler
    public void onEntityInteract(EntityInteractEvent e) {
        if (e.getEntityType().equals(EntityType.ARMOR_STAND)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL && !SetupCommand.ArenaTest.contains(e.getPlayer())) {
            for (ArenaManager arena : files.arenaManager.values()) {
                HashMap<TeamEnum, Location> joinsMap = arena.getJoinLocations();

                Player player = e.getPlayer();
                try {
                    Location interaction = Objects.requireNonNull(e.getClickedBlock()).getLocation();

                    for (Map.Entry<TeamEnum, Location> entry : joinsMap.entrySet()) {
                        if (entry.getValue().equals(interaction)) {
                            if (SetupCommand.ArenaSetup.containsKey(player)) {
                                player.sendMessage(prefix + "§cThis Location is already in use by §3" + arena.getName() + " §7: " + entry.getKey().getColor() + "Team" + entry.getKey().getName());
                                return;
                            }

                            if (ArenaManager.containsPlayer(player.getUniqueId())) {
                                ArenaPlayer arenaPlayer = ArenaManager.getArenaPlayer(player.getUniqueId());
                                player.sendMessage(prefix + "§cDu bist bereits in " + arenaPlayer.getTeam().getColor() + "Team " + arenaPlayer.getTeam().getName() + "!");
                                return;
                            }

                            TeamEnum team = entry.getKey();

                            if (arena.getArenaState() != ArenaState.LOBBY) {
                                player.sendMessage(prefix + "Diese Arena läuft bereits!");
                                return;
                            }

                            if (arena.getFightersList().size() >= arena.getMaxTeamSize() * 2) {
                                player.sendMessage(prefix + "§cDiese Arena ist voll, bitte komm nächste Runde wieder!");
                                return;
                            }

                            if ((arena.getArenaTeamRed().size() >= arena.getMaxTeamSize() && team == TeamEnum.RED) || (team == TeamEnum.BLUE && arena.getArenaTeamBlue().size() >= arena.getMaxTeamSize())) {
                                player.sendMessage(prefix + "§cDas Team ist voll, bitte gehe in das andere!");
                                return;
                            }

                            ChatColor color = team.getColor();
                            String teamName = team.getName();

                            arena.addPlayer(player, entry.getKey());
                            player.sendMessage(prefix + "§eDu bist " + color + "Team " + teamName + "§e beigetreten");
                            player.sendMessage(prefix + "§7Benutze §a/arena accept§7 damit der Countdown starten kann!");

                        }
                    }

                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    player.sendMessage(prefix + "§4ERROR");
                    return;
                }

            }
        }

        if (SetupCommand.ArenaSetup.containsKey(e.getPlayer())) {
            if (e.getAction() == Action.PHYSICAL) {

                Player player = e.getPlayer();
                ArenaSetup setup = SetupCommand.ArenaSetup.get(e.getPlayer());
                switch (setup.getStep()) {
                    case 6:
                        setup.setJoinTeam1(e.getClickedBlock().getLocation());
                        player.sendMessage(prefix + "§eNow step on a pressure plate, where §6Team 2 §ewill join!");
                        setup.nextStep();
                        return;
                    case 7:
                        setup.setJoinTeam2(e.getClickedBlock().getLocation());
                        player.sendMessage(prefix + "§eNow go to the Center of the Arena an type 'next'!");
                        setup.nextStep();
                        return;

                    default:
                        player.sendMessage(prefix + "§4An error encountered! We don't know how, and don't even know what happend. Sorry :(");
                }
            }
        }

    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (ArenaManager.containsPlayer(p.getUniqueId())) {
            if (ArenaManager.getPlayerArena(p.getUniqueId()).getActiveCountdown() == CountdownEnum.PREFIGHT) {
                if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (ArenaManager.containsPlayer(player.getUniqueId())) {
                ArenaManager arenaManager = ArenaManager.getPlayerArena(player.getUniqueId());
                ArenaPlayer arenaPlayer = ArenaManager.getArenaPlayer(player.getUniqueId());

                if (arenaPlayer.getTeam() == TeamEnum.SPECTATOR) {
                    e.setFoodLevel(20);
                }

                if (arenaManager.getActiveCountdown() != CountdownEnum.FIGHT) {
                    e.setFoodLevel(20);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if (e.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                Player victim = (Player) e.getEntity();
                Player damager = (Player) event.getDamager();

                if (ArenaManager.containsPlayer(victim) && ArenaManager.containsPlayer(damager)) {
                    ArenaPlayer ArenaVictim = ArenaManager.getArenaPlayer(victim.getUniqueId());
                    ArenaPlayer ArenaDamager = ArenaManager.getArenaPlayer(victim.getUniqueId());

                    if (ArenaManager.getPlayerArena(victim.getUniqueId()) != ArenaManager.getPlayerArena(damager.getUniqueId())) {
                        event.setCancelled(true);
                    }

                    if (ArenaVictim.getTeam() != TeamEnum.SPECTATOR) {
                        event.setCancelled(true);
                    }

                    ArenaManager arenaManager = ArenaManager.getPlayerArena(victim.getUniqueId());

                    if (ArenaVictim.getTeam() == ArenaDamager.getTeam()) {
                        event.setCancelled(true);
                    }

                    if (arenaManager.getActiveCountdown() == CountdownEnum.FIGHT) {
                        event.setCancelled(false);
                        if ((victim.getHealth() - event.getFinalDamage()) <= 0) {
                            Bukkit.getPluginManager().callEvent(new ArenaPlayerDieEvent(arenaManager, victim, damager));
                        }
                    }

                } else {

                    if (!ArenaManager.containsPlayer(victim) && !ArenaManager.containsPlayer(damager)) {
                        event.setCancelled(false);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }

        } else {
            if (e.getEntity() instanceof Player) {
                Player victim = (Player) e.getEntity();
                if (ArenaManager.containsPlayer(victim)) {
                    ArenaManager arenaManager = ArenaManager.getPlayerArena(victim.getUniqueId());

                    if (arenaManager.getActiveCountdown() != CountdownEnum.FIGHT) {
                        e.setCancelled(true);
                    }

                    if ((victim.getHealth() - e.getFinalDamage()) <= 0) {
                        Bukkit.getPluginManager().callEvent(new ArenaPlayerDieEvent(arenaManager, victim, null));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArenaPlayerDie(ArenaPlayerDieEvent e) {
        ArenaManager arenaManager = e.getArenaManager();
        ArenaPlayer victim = ArenaManager.getArenaPlayer(e.getVictim().getUniqueId());
        assert victim != null;
        if (e.getKiller() != null) {
            ArenaPlayer damager = ArenaManager.getArenaPlayer(e.getKiller().getUniqueId());
            assert damager != null;
            victim.setDead(true);
            damager.addKills();

            arenaManager.sendArenaMessage("§e" + damager.getTeam().getColor() + e.getKiller().getName() + "§7 hat " + victim.getTeam().getColor() + e.getVictim().getName() + "§7 getötet!");

        } else {
            victim.setDead(true);
            victim.addDeaths();
            arenaManager.sendArenaMessage(victim.getTeam().getColor() + e.getVictim().getName() + " §7ist gestorben!");
        }

        TeamManager victimTeam = arenaManager.getTeamList().get(victim.getTeam());
        if (victimTeam.isDead()) {
            TeamEnum opposite = victimTeam.getTeamEnum() == TeamEnum.RED ? TeamEnum.BLUE : TeamEnum.RED;
            arenaManager.getTeamList().get(opposite).addWin();
            victimTeam.addLose();
            arenaManager.resetActiveCountdown();
            arenaManager.setActiveCountdown(CountdownEnum.AFTERFIGHT);
            arenaManager.sendArenaMessage(opposite.getTeamString() + " §ehat §bRunde " + arenaManager.getRoundsPlayed() + " §efür sich entschieden!");
        }
    }

    @EventHandler
    public void onPickUp(PlayerAttemptPickupItemEvent e){
        Player player = e.getPlayer();
        if(ArenaManager.containsPlayer(player.getUniqueId())){
            ArenaManager arenaManager = ArenaManager.getPlayerArena(player.getUniqueId());
            ArenaPlayer arenaPlayer = ArenaManager.getArenaPlayer(player.getUniqueId());

            if(arenaManager.getActiveCountdown() != CountdownEnum.FIGHT || arenaPlayer.getTeam() == TeamEnum.SPECTATOR){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        Player player = e.getPlayer();
        if(ArenaManager.containsPlayer(player.getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (ArenaManager.containsPlayer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (ArenaManager.containsPlayer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onArenaLeave(ArenaPlayerLeaveEvent e) {
        ArenaManager arenaManager = e.getArenaManager();
        if (arenaManager.isInited()) {
            TeamEnum playerTeam = e.getArenaPlayer().getTeam();
            TeamManager teamManager = arenaManager.getTeamList().get(playerTeam);
            TeamEnum opposite = teamManager.getTeamEnum() == TeamEnum.RED ? TeamEnum.BLUE : TeamEnum.RED;

            if (teamManager.getPlayerList().size() <= 0) {
                arenaManager.resetActiveCountdown();
                arenaManager.win(opposite);
            }
        }

    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if(ArenaManager.containsPlayer(player)){
            ArenaPlayer arenaPlayer = ArenaManager.getArenaPlayer(player);
            ArenaManager arenaManager = ArenaManager.getPlayerArena(player.getUniqueId());
            assert arenaManager != null;
            if(arenaPlayer.getTeam() != TeamEnum.SPECTATOR){
                arenaManager.removePlayer(e.getPlayer());
            } else {
                arenaManager.removeViewer(e.getPlayer());
            }
            arenaManager.getNeedToTeleport().add(e.getPlayer().getName());
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e){
        ArenaManager arenaManager = ArenaManager.isNeedToTeleport(e.getPlayer().getName());
        if(arenaManager != null){
            e.getPlayer().teleport(arenaManager.getExitSpawn());
            e.getPlayer().sendMessage(prefix + "§7Du musstest teleportiert werden!");
            arenaManager.getNeedToTeleport().remove(e.getPlayer().getName());
        }

    }

}
