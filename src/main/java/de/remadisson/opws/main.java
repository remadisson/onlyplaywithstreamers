package de.remadisson.opws;

import de.remadisson.opws.arena.ArenaEvents;
import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.commands.*;
import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.listener.*;
import de.remadisson.opws.manager.TablistManager;
import de.remadisson.opws.commands.WarpCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class main extends JavaPlugin {

    private final String console = files.console;

    private static main plugin;

    /**
     * Init start sequence
     */

    @Override
    public void onEnable() {
        plugin = this;

        /**
         * Registers Commands to the (Minecraft) PluginManager
         */
        registerCommands();

        /**
         * Registers Listeners/Events to the PluginManager
         */
        registerListeners();

        /**
         * Sends Debug to the Console.
         */
        Bukkit.getConsoleSender().sendMessage(console + "§aOnlyPlayWithStreamers started!");

        GeneralSchedulerManager.doCycle();

        for(Player online : Bukkit.getOnlinePlayers()){
            UUID uuid = online.getUniqueId();
            JoinAndQuitListener.updateHeaderAndFooter(online);
            TablistManager.getInstance().updateTeam(online, files.getPrefix(uuid), files.getColor(uuid), "", files.getLevel(uuid));
        }

        Bukkit.setWhitelist(false);
    }


    /**
     * Init shutdown sequence
     */
    @Override
    public void onDisable() {
        files.state = ServerState.ERROR;

        for(ArenaManager arenaManager : files.arenaManager.values()){
            for (Player player : arenaManager.getFightersList()) {
                arenaManager.removePlayer(player, true);
                arenaManager.getNeedToTeleport().add(player.getUniqueId());
            }

            for(Player player : arenaManager.getViewer()){
                arenaManager.removeViewer(player, true);
                arenaManager.getNeedToTeleport().add(player.getUniqueId());
            }
        }

        files.disableFiles();
        files.despawnHolograms();

        /**
         * Sends Debug to Console.
         */
        Bukkit.getConsoleSender().sendMessage(console + "§cOnlyPlayWithStreamers deactivated!");
    }


    /**
     * Registers Commands to the (Minecraft) PluginManager
     */
    public void registerCommands(){
        getCommand("streamer").setExecutor(new StreamerCommand());

        getCommand("worker").setExecutor(new WorkerCommand());

        getCommand("whitelist").unregister(null);
        getCommand("whitelist").setExecutor(new WhitelistCommand());

        getCommand("warp").setExecutor(new WarpCommand());

        getCommand("gamemode").unregister(null);
        getCommand("gamemode").setExecutor(new GameModeCommand());

        getCommand("city").setExecutor(new CityCommand());

        getCommand("setup").setExecutor(new SetupCommand());

        getCommand("arena").setExecutor(new ArenaCommand());

        getCommand("staff").setExecutor(new StaffCommand());

        getCommand("kick").unregister(null);
        getCommand("kick").setExecutor(new KickCommand());

        getCommand("ban").unregister(null);
        getCommand("ban").setExecutor(new BanCommand());

        getCommand("banlist").unregister(null);
        getCommand("banlist").setExecutor(new BanListCommand());

        getCommand("unban").setExecutor(new UnbanCommand());

        Bukkit.setMaxPlayers(40);

    }


    /**
     * Registers Listeners/Events to the PluginManager
     */
    public void registerListeners(){
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new JoinAndQuitListener(), this);
        pm.registerEvents(new PingEvent(), this);
        pm.registerEvents(new UpdateEvents(), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new GameEvents(), this);
        pm.registerEvents(new ArenaEvents(), this);
    }


    public static main getInstance(){
        return plugin;
    }

}
