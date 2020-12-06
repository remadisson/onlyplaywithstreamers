package de.remadisson.opws;

import de.remadisson.opws.commands.AllowedCommand;
import de.remadisson.opws.commands.StreamerCommand;
import de.remadisson.opws.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin {

    private final String console = files.console;

    private static main plugin;
    /**
     * Init start sequence
     */
    @Override
    public void onEnable() {
        plugin = this;

        files.loadStreamer();

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
            JoinAndQuitListener.updateHeaderAndFooter(online);
        }
    }


    /**
     * Init shutdown sequence
     */
    @Override
    public void onDisable() {

        files.disableStreamer();

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
    }


    public static main getInstance(){
        return plugin;
    }

}
