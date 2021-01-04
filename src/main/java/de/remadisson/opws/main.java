package de.remadisson.opws;

import de.remadisson.opws.commands.*;
import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.listener.*;
import de.remadisson.opws.manager.TablistManager;
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
        files.loadFiles();
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

        CheckStreamerManager.doCycle();

        for(Player online : Bukkit.getOnlinePlayers()){
            UUID uuid = online.getUniqueId();
            JoinAndQuitListener.updateHeaderAndFooter(online);
            TablistManager.getInstance().updateTeam(online, files.getPrefix(uuid), files.getColor(uuid), "", files.getLevel(uuid));
        }

        files.streamerManager.syncWhitelist();
        files.state = ServerState.CLOSED;
    }


    /**
     * Init shutdown sequence
     */
    @Override
    public void onDisable() {
        files.state = ServerState.ERROR;
        files.disableFiles();

        /**
         * Sends Debug to Console.
         */
        Bukkit.getConsoleSender().sendMessage(console + "§cOnlyPlayWithStreamers deactivated!");
    }


    /**
     * Registers Commands to the (Minecraft) PluginManager
     */
    public void registerCommands(){
        Bukkit.getPluginCommand("streamer").setExecutor(new StreamerCommand());
        Bukkit.getPluginCommand("streamer").setTabCompleter(new StreamerCommand());

        Bukkit.getPluginCommand("allowed").setExecutor(new AllowedCommand());
        Bukkit.getPluginCommand("allowed").setTabCompleter(new AllowedCommand());

        Bukkit.getPluginCommand("whitelist").unregister(null);
        Bukkit.getPluginCommand("whitelist").setExecutor(new WhitelistCommand());
        Bukkit.getPluginCommand("whitelist").setTabCompleter(new WhitelistCommand());

        Bukkit.getPluginCommand("warp").setExecutor(new WarpCommand());
        Bukkit.getPluginCommand("warp").setTabCompleter(new WarpCommand());

        Bukkit.getPluginCommand("gamemode").unregister(null);
        Bukkit.getPluginCommand("gamemode").setExecutor(new GameModeCommand());
        Bukkit.getPluginCommand("gamemode").setTabCompleter(new GameModeCommand());

        //Bukkit.getPluginCommand("setup").setExecutor(new SetupCommand());
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

    }


    public static main getInstance(){
        return plugin;
    }

}
