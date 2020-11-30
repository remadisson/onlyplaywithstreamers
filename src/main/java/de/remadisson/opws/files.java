package de.remadisson.opws;

import de.remadisson.opws.api.FileAPI;
import de.remadisson.opws.enums.ServerState;
import de.remadisson.opws.manager.StreamerManager;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class files {

    public static Executor pool = Executors.newCachedThreadPool();

    public static String prefix = "§8» §r";
    public static String console = "§eOPWS " + prefix;
    public static String debug = "§7[§dDEBUG§7] " + console;

    public static ServerState state = ServerState.CLOSED;

    public static FileAPI fileAPI = new FileAPI("streamer.yml", "./plugins/OnlyPlayWithStreamers");
    public static StreamerManager streamerManager = new StreamerManager(fileAPI);

    public static void loadStreamer(){
        streamerManager.load();
    }

    public static void disableStreamer(){
        streamerManager.save();
    }

}
