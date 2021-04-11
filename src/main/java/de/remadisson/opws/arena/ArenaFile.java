package de.remadisson.opws.arena;

import de.remadisson.opws.files;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArenaFile {

    /**
     * Independent Method, to Load all Arena Positions form the Config File.
     */

    public static void load() {
        ConfigurationSection cs = files.arenaFile.getSection("arena");
        if (cs.getKeys(false).isEmpty()) return;

        for (String arenaName : cs.getKeys(false)) {
            if (files.arenaManager.containsKey(arenaName)) continue;

            Location viewerSpawn = new Location(Bukkit.getWorld(cs.getString(arenaName + ".viewerSpawn.world")),
                    cs.getDouble(arenaName + ".viewerSpawn.x"),
                    cs.getDouble(arenaName + ".viewerSpawn.y"),
                    cs.getDouble(arenaName + ".viewerSpawn.z"),
                    cs.getLong(arenaName + ".viewerSpawn.yaw"),
                    cs.getLong(arenaName + ".viewerSpawn.pitch"));

            Location deadPlayerSpawn = new Location(Bukkit.getWorld(cs.getString(arenaName + ".deadPlayerSpawn.world")),
                    cs.getDouble(arenaName + ".deadPlayerSpawn.x"),
                    cs.getDouble(arenaName + ".deadPlayerSpawn.y"),
                    cs.getDouble(arenaName + ".deadPlayerSpawn.z"),
                    cs.getLong(arenaName + ".deadPlayerSpawn.yaw"),
                    cs.getLong(arenaName + ".deadPlayerSpawn.pitch"));

            Location exitSpawn = new Location(Bukkit.getWorld(cs.getString(arenaName + ".exitSpawn.world")),
                    cs.getDouble(arenaName + ".exitSpawn.x"),
                    cs.getDouble(arenaName + ".exitSpawn.y"),
                    cs.getDouble(arenaName + ".exitSpawn.z"),
                    cs.getLong(arenaName + ".exitSpawn.yaw"),
                    cs.getLong(arenaName + ".exitSpawn.pitch"));

            Location joinTeam1 = new Location(Bukkit.getWorld(cs.getString(arenaName + ".joinTeam1.world")),
                    cs.getDouble(arenaName + ".joinTeam1.x"),
                    cs.getDouble(arenaName + ".joinTeam1.y"),
                    cs.getDouble(arenaName + ".joinTeam1.z"));

            Location joinTeam2 = new Location(Bukkit.getWorld(cs.getString(arenaName + ".joinTeam2.world")),
                    cs.getDouble(arenaName + ".joinTeam2.x"),
                    cs.getDouble(arenaName + ".joinTeam2.y"),
                    cs.getDouble(arenaName + ".joinTeam2.z"));

            Location SpawnTeam1 = new Location(Bukkit.getWorld(cs.getString(arenaName + ".spawnTeam1.world")),
                    cs.getDouble(arenaName + ".spawnTeam1.x"),
                    cs.getDouble(arenaName + ".spawnTeam1.y"),
                    cs.getDouble(arenaName + ".spawnTeam1.z"),
                    cs.getLong(arenaName + ".spawnTeam1.yaw"),
                    cs.getLong(arenaName + ".spawnTeam1.pitch"));

            Location SpawnTeam2 = new Location(Bukkit.getWorld(cs.getString(arenaName + ".spawnTeam2.world")),
                    cs.getDouble(arenaName + ".spawnTeam2.x"),
                    cs.getDouble(arenaName + ".spawnTeam2.y"),
                    cs.getDouble(arenaName + ".spawnTeam2.z"),
                    cs.getLong(arenaName + ".spawnTeam2.yaw"),
                    cs.getLong(arenaName + ".spawnTeam2.pitch"));

            Location Center = new Location(Bukkit.getWorld(cs.getString(arenaName + ".center.world")),
                    cs.getDouble(arenaName + ".center.x"),
                    cs.getDouble(arenaName + ".center.y"),
                    cs.getDouble(arenaName + ".center.z"),
                    cs.getLong(arenaName + ".center.yaw"),
                    cs.getLong(arenaName + ".center.pitch"));

            ArenaManager arenaManager = new ArenaManager(arenaName, viewerSpawn, deadPlayerSpawn, exitSpawn, joinTeam1, joinTeam2, SpawnTeam1, SpawnTeam2, Center);
            arenaManager.setNeedToTeleport((ArrayList<UUID>) cs.getStringList(arenaName + ".needToTeleport").stream().map(UUID::fromString).collect(Collectors.toList()));
            files.arenaManager.put(arenaName, arenaManager);
        }
    }

    /**
     * Independent Method, to save all Arena Data into a config.
     */
    public static void save() {
        ConfigurationSection cs = files.arenaFile.getSection("arena");

        for (Map.Entry<String, ArenaManager> arena : files.arenaManager.entrySet()) {
            String key = arena.getKey();
            ArenaManager aM = arena.getValue();
            cs.set(key + ".viewerSpawn.world", aM.getViewerSpawn().getWorld().getName());
            cs.set(key + ".viewerSpawn.x", aM.getViewerSpawn().getX());
            cs.set(key + ".viewerSpawn.y", aM.getViewerSpawn().getY());
            cs.set(key + ".viewerSpawn.z", aM.getViewerSpawn().getZ());
            cs.set(key + ".viewerSpawn.pitch", aM.getViewerSpawn().getPitch());
            cs.set(key + ".viewerSpawn.yaw", aM.getViewerSpawn().getYaw());

            cs.set(key + ".deadPlayerSpawn.world", aM.getDeadPlayerSpawn().getWorld().getName());
            cs.set(key + ".deadPlayerSpawn.x", aM.getDeadPlayerSpawn().getX());
            cs.set(key + ".deadPlayerSpawn.y", aM.getDeadPlayerSpawn().getY());
            cs.set(key + ".deadPlayerSpawn.z", aM.getDeadPlayerSpawn().getZ());
            cs.set(key + ".deadPlayerSpawn.pitch", aM.getDeadPlayerSpawn().getPitch());
            cs.set(key + ".deadPlayerSpawn.yaw", aM.getDeadPlayerSpawn().getYaw());

            cs.set(key + ".exitSpawn.world", aM.getExitSpawn().getWorld().getName());
            cs.set(key + ".exitSpawn.x", aM.getExitSpawn().getX());
            cs.set(key + ".exitSpawn.y", aM.getExitSpawn().getY());
            cs.set(key + ".exitSpawn.z", aM.getExitSpawn().getZ());
            cs.set(key + ".exitSpawn.pitch", aM.getExitSpawn().getPitch());
            cs.set(key + ".exitSpawn.yaw", aM.getExitSpawn().getYaw());

            cs.set(key + ".joinTeam1.world", aM.getJoinTeam1().getWorld().getName());
            cs.set(key + ".joinTeam1.x", aM.getJoinTeam1().getX());
            cs.set(key + ".joinTeam1.y", aM.getJoinTeam1().getY());
            cs.set(key + ".joinTeam1.z", aM.getJoinTeam1().getZ());

            cs.set(key + ".joinTeam2.world", aM.getJoinTeam2().getWorld().getName());
            cs.set(key + ".joinTeam2.x", aM.getJoinTeam2().getX());
            cs.set(key + ".joinTeam2.y", aM.getJoinTeam2().getY());
            cs.set(key + ".joinTeam2.z", aM.getJoinTeam2().getZ());

            cs.set(key + ".spawnTeam1.world", aM.getSpawnTeam1().getWorld().getName());
            cs.set(key + ".spawnTeam1.x", aM.getSpawnTeam1().getX());
            cs.set(key + ".spawnTeam1.y", aM.getSpawnTeam1().getY());
            cs.set(key + ".spawnTeam1.z", aM.getSpawnTeam1().getZ());
            cs.set(key + ".spawnTeam1.pitch", aM.getSpawnTeam1().getPitch());
            cs.set(key + ".spawnTeam1.yaw", aM.getSpawnTeam1().getYaw());

            cs.set(key + ".spawnTeam2.world", aM.getSpawnTeam2().getWorld().getName());
            cs.set(key + ".spawnTeam2.x", aM.getSpawnTeam2().getX());
            cs.set(key + ".spawnTeam2.y", aM.getSpawnTeam2().getY());
            cs.set(key + ".spawnTeam2.z", aM.getSpawnTeam2().getZ());
            cs.set(key + ".spawnTeam2.pitch", aM.getSpawnTeam2().getPitch());
            cs.set(key + ".spawnTeam2.yaw", aM.getSpawnTeam2().getYaw());

            cs.set(key + ".center.world", aM.getCenter().getWorld().getName());
            cs.set(key + ".center.x", aM.getCenter().getX());
            cs.set(key + ".center.y", aM.getCenter().getY());
            cs.set(key + ".center.z", aM.getCenter().getZ());
            cs.set(key + ".center.pitch", aM.getCenter().getPitch());
            cs.set(key + ".center.yaw", aM.getCenter().getYaw());

            cs.set(key + ".needToTeleport", aM.getNeedToTeleport().stream().map(UUID::toString).collect(Collectors.toList()));
        }


        files.arenaFile.save();
    }

}
