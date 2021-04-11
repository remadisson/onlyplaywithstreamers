package de.remadisson.opws.listener;

import de.remadisson.opws.arena.ArenaManager;
import de.remadisson.opws.files;
import de.remadisson.opws.main;
import de.remadisson.opws.manager.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;


public class WorldListener {

    private static Duration temp_farmwelt;
    private static Duration temp_nether;
    private static boolean farmWeltStatus = false;
    private static boolean netherStatus = false;
    private static int interval = 24;

    public static boolean WorldCycle(){
        WorldManager wm = new WorldManager("farmwelt", WorldType.NORMAL, World.Environment.NORMAL, true);
        files.worldManager.put(wm.getWorldName(), wm);

        Calendar cal_old = Calendar.getInstance();

        cal_old.setTimeInMillis(wm.getMillis());

        Calendar cal_new = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

        Duration distance = Duration.between(cal_old.getTime().toInstant(), cal_new.getTime().toInstant());

        if(temp_farmwelt == null || distance.toDays() == (temp_farmwelt.toDays() + 1) || ((distance.toHours() % interval) == 0)) {
            if(!farmWeltStatus) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
                System.out.println(files.debug + "§dFarmwelt Exists since: " + sdf.format(cal_old.getTime()) + " §7- §eAlready existing Days §b" + distance.toDays() + "§8/§e7 §8(§b" + distance.toHours() + " Hours§8)");
                }, 20*4);
                farmWeltStatus = true;
            }
        } else {
            farmWeltStatus = false;
        }

        temp_farmwelt = distance;

        if(distance.toDays() > 6) {

            System.out.println(files.debug + "Resetting " + wm.get().getName());
            files.sendDiscordWorldReset(wm.get());
            wm.delete(World.Environment.NORMAL);
            WorldCycle();

            return true;
        }

        WorldManager test = new WorldManager("testworld", WorldType.FLAT, World.Environment.NORMAL, false);
        files.worldManager.put(test.getWorldName(), test);
        WorldManager heaven = new WorldManager ("heaven", WorldType.FLAT, World.Environment.NORMAL, false);
        files.worldManager.put(heaven.getWorldName(), heaven);

        return false;
    }

    public static boolean NetherCycle(){
        WorldManager wm =  new WorldManager("nether", WorldType.NORMAL, World.Environment.NETHER, false);
        files.worldManager.put(wm.getWorldName(), wm);

        Calendar cal_old = Calendar.getInstance();

        cal_old.setTimeInMillis(wm.getMillis());

        Calendar cal_new = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

        Duration distance = Duration.between(cal_old.getTime().toInstant(), cal_new.getTime().toInstant());

        if(temp_nether == null || distance.toDays() == (temp_nether.toDays() + 1) || ((distance.toHours() % interval) == 0)) {
            if(!netherStatus) {
                    System.out.println(files.debug + "§dNether Exists since: " + sdf.format(cal_old.getTime()) + " §7- §eAlready existing Days §b" + distance.toDays() + "§8/§e30 §8(§b" + distance.toHours() + " Hours§8)");
                netherStatus = true;
            }
        } else {
            netherStatus = false;
        }

        temp_nether = distance;

        if(distance.toDays() > 29) {
            System.out.println(files.debug + "Resetting " + wm.get().getName());
            files.sendDiscordWorldReset(wm.get());
            wm.delete(World.Environment.NETHER);
            NetherCycle();

            return true;
        }


        return false;
    }

    public static void CheckArenaReset(){
        long lastReset = ArenaManager.lastResetMillis;
        Calendar old = Calendar.getInstance();
        old.setTimeInMillis(lastReset);
        Calendar newcal = Calendar.getInstance();

        if(newcal.get(Calendar.YEAR) == old.get(Calendar.YEAR)
                && newcal.get(Calendar.MONTH) == old.get(Calendar.MONTH)
                && newcal.get(Calendar.DAY_OF_MONTH) != old.get(Calendar.DAY_OF_MONTH)){
            ArenaManager.alreadyArenaPlayedPlayer.clear();
            ArenaManager.lastResetMillis = System.currentTimeMillis();
            System.out.println(files.debug + "§bArenaPlayer have been resettet!");
        }
    }

}
