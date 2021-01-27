package de.remadisson.opws.arena;

import de.remadisson.opws.enums.TeamEnum;
import de.remadisson.opws.files;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaManager {

    private final HashMap<ArenaPlayer, Inventory> waitingPlayers = new HashMap<>();

    // List of external Viewers
    private final ArrayList<Player> viewerList = new ArrayList<>();

    // List of dead Players that are spectating
    private ArrayList<Player> deadPlayers = new ArrayList<>();

    private String name;

    private final Location viewerSpawn;
    private final Location deadPlayerSpawn;
    private final Location exitSpawn;
    private final Location JoinTeam1;
    private final Location JoinTeam2;
    private final Location SpawnTeam1;
    private final Location SpawnTeam2;

    private HashMap<TeamEnum, TeamManager> teamMap;

    private boolean inited = false;

    public ArenaManager(String name, Location viewerSpawn, Location deadPlayerSpawn, Location exitSpawn, Location JoinTeam1, Location JoinTeam2, Location SpawnTeam1, Location SpawnTeam2) {
        this.name = name;
        this.viewerSpawn = viewerSpawn;
        this.deadPlayerSpawn = deadPlayerSpawn;
        this.exitSpawn = exitSpawn;
        this.JoinTeam1 = JoinTeam1;
        this.JoinTeam2 = JoinTeam2;
        this.SpawnTeam1 = SpawnTeam1;
        this.SpawnTeam2 = SpawnTeam2;
    }

    /**
     * This Methods Ports the Players in their Teams onto the SpawnPositions.
     *
     * @param teamMap
     * @return
     */
    public ArenaManager init(HashMap<TeamEnum, TeamManager> teamMap) {
        this.teamMap = teamMap;

        inited = true;
        return this;
    }

    /**
     * This Methods Starts the Fight Countdown and Initiates the Fight
     *
     * @return
     */
    public boolean fight() {
        if (!inited) return false;

        return true;
    }


    /**
     * This Method resets all Players to their SpawnPositions and and shows the starts the Fight Sequence again.
     *
     * @return
     */
    public boolean reset() {

        return true;
    }


    /**
     * If a Team has the required Wins to Win the whole Arena-Fight, then this Sequence will be triggert
     *
     * @return
     */
    public boolean finish() {

        inited = false;
        this.teamMap = null;

        return true;
    }



    public void removeArena(){
        files.arenaManager.remove(name);
        files.arenaFile.getSection("arena").set(name, null);
    }

    public HashMap<TeamEnum, TeamManager> getTeamList() {
        return teamMap;
    }

    public ArrayList<Player> getPlayerList(){
        ArrayList<Player> playerList = new ArrayList<>();

        playerList.addAll(getTeamList().get(TeamEnum.BLUE).getPlayerList());
        playerList.addAll(getTeamList().get(TeamEnum.RED).getPlayerList());

        return playerList;
    }

    public String getName() {
        return name;
    }

    public Location getViewerSpawn() {
        return viewerSpawn;
    }

    public Location getDeadPlayerSpawn() {
        return deadPlayerSpawn;
    }

    public Location getExitSpawn() {
        return exitSpawn;
    }

    public Location getJoinTeam1() {
        return JoinTeam1;
    }

    public Location getJoinTeam2() {
        return JoinTeam2;
    }

    public Location getSpawnTeam1() {
        return SpawnTeam1;
    }

    public Location getSpawnTeam2() {
        return SpawnTeam2;
    }

    public boolean isInited() {
        return inited;
    }

    public LinkedHashMap<String, Location> getLocationsAsList() {
        LinkedHashMap<String, Location> locations = new LinkedHashMap<>();
        locations.put("viewerSpawn", getViewerSpawn());
        locations.put("deadPlayerSpawn", getDeadPlayerSpawn());
        locations.put("exitSpawn", getExitSpawn());
        locations.put("joinTeam1", getJoinTeam1());
        locations.put("joinTeam2", getJoinTeam2());
        locations.put("SpawnTeam1", getSpawnTeam1());
        locations.put("SpawnTeam2", getSpawnTeam2());
        return locations;
    }

    public HashMap<Location, TeamEnum> getJoinLocations(){
        HashMap<Location, TeamEnum> joinsMap = new HashMap<>();

        joinsMap.put(getJoinTeam1(), TeamEnum.RED);
        joinsMap.put(getJoinTeam2(), TeamEnum.BLUE);
        return joinsMap;
    }

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

            ArenaManager arenaManager = new ArenaManager(arenaName, viewerSpawn, deadPlayerSpawn, exitSpawn, joinTeam1, joinTeam2, SpawnTeam1, SpawnTeam2);

            boolean inited = cs.getBoolean(arenaName + ".inited");
            if (inited) {

                HashMap<TeamEnum, TeamManager> teams = new HashMap<>();

                for (TeamEnum team : cs.getStringList(arenaName + ".teams").stream().map(TeamEnum::valueOf).collect(Collectors.toList())) {
                    HashMap<UUID, ArenaPlayer> memberMap = new HashMap<>();

                    int wins = cs.getInt(arenaName + "." + team.name + ".wins");
                    int loses = cs.getInt(arenaName + "." + team.name + ".loses");

                    for (String stringUUID : cs.getStringList(arenaName + "." + team.name + ".player")) {
                        UUID uuid = UUID.fromString(stringUUID);
                        HashMap<Integer, ItemStack> items = new HashMap<>();

                        for (Integer slot : cs.getIntegerList(arenaName + "." + team.name + ".player." + uuid.toString() + ".items")) {
                            items.put(slot, cs.getItemStack(arenaName + "." + team.name + ".player." + uuid.toString() + ".items." + slot));
                        }


                        ArenaPlayer arenaPlayer = new ArenaPlayer(uuid, items);

                        memberMap.put(uuid, arenaPlayer);
                    }

                    TeamManager teamManager = new TeamManager(team, memberMap);
                    teamManager.setWins(wins);
                    teamManager.setLoses(loses);
                    teams.put(team, teamManager);
                }

                arenaManager.init(teams);
            }
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

            cs.set(key + ".inited", aM.isInited());

            if (aM.isInited()) {
                for (Map.Entry<TeamEnum, TeamManager> teamManager : aM.getTeamList().entrySet()) {
                    cs.set(key + ".teams", teamManager.getValue().getTeamEnum().name);
                    cs.set(key + "." + teamManager.getValue().getTeamEnum().name + ".loses", teamManager.getValue().getLoses());
                    cs.set(key + "." + teamManager.getValue().getTeamEnum().name + ".wins", teamManager.getValue().getWins());

                    for (Player player : teamManager.getValue().getPlayerList()) {
                        UUID uuid = player.getUniqueId();

                        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {

                            if (player.getInventory().getItem(slot) == null) {
                                continue;
                            }
                            cs.set(key + "." + teamManager.getValue().getTeamEnum().name + ".player." + uuid.toString() + ".item." + slot, player.getInventory().getItem(slot));
                        }
                    }

                }
            }


        }


        files.arenaFile.save();
    }

}
