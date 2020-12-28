package de.remadisson.opws.listener;

import de.remadisson.opws.manager.WorldManager;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorldListener {

    public static void WorldCycle(){
        WorldManager wm = new WorldManager("farmwelt", WorldType.NORMAL, World.Environment.NORMAL);

        Calendar cal_old = Calendar.getInstance();
        cal_old.setTimeInMillis(wm.getMillis());
        Calendar cal_new = Calendar.getInstance();
        long distance = TimeUnit.MILLISECONDS.toDays((cal_new.getTimeInMillis() - cal_old.getTimeInMillis()));
        if(distance == 7) {
            wm.delete();
            WorldCycle();
        }
    }

}
//1609190322476