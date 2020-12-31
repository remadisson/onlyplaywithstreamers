package de.remadisson.opws.listener;

import de.remadisson.opws.files;
import de.remadisson.opws.manager.WorldManager;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;


public class WorldListener {

    private static int half_hours = 0;

    public static void WorldCycle(){
        WorldManager wm =  new WorldManager("farmwelt", WorldType.NORMAL, World.Environment.NORMAL);
        files.worldManager.put(wm.get().getName(), wm);
        Calendar cal_old = Calendar.getInstance();
        cal_old.setTimeInMillis(wm.getMillis());
        Calendar cal_new = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Duration distance = Duration.between(cal_old.getTime().toInstant(), cal_new.getTime().toInstant());
        if(half_hours == 0 || half_hours == 23 || half_hours == 47) {
            System.out.println(files.debug + "§dFarmwelt Exists since: " + sdf.format(cal_old.getTime()) + " §7- §eAlready existing Days §b" + distance.toDays() + "§8/§e7 §8(§b"+distance.toHours()+" Hours§8)");
        }
        if(distance.toDays() > 6) {
            System.out.println(files.debug + "Resetting " + wm.get().getName());
            wm.delete();
            WorldCycle();
        }

        half_hours++;
    }

}
