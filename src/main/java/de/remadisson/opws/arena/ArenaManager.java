package de.remadisson.opws.arena;

import com.destroystokyo.paper.Title;
import de.remadisson.opws.enums.ArenaState;
import de.remadisson.opws.enums.CountdownEnum;
import de.remadisson.opws.enums.TeamEnum;
import de.remadisson.opws.events.ArenaPlayerLeaveEvent;
import de.remadisson.opws.events.ArenaScoreboardUpdateEvent;
import de.remadisson.opws.events.PlayerChangePermissionEvent;
import de.remadisson.opws.files;
import de.remadisson.opws.heaven.HeavenManager;
import de.remadisson.opws.main;
import de.remadisson.opws.api.Hologram;
import de.remadisson.opws.manager.StreamerManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaManager {

    public static ArrayList<UUID> alreadyArenaPlayedPlayer = new ArrayList<>();
    public static ArrayList<UUID> infiniteAllowedPlay = new ArrayList<>();
    public static long lastResetMillis = System.currentTimeMillis();
    public static boolean infitePlay = false;

    private String name;
    private final Location viewerSpawn;
    private final Location deadPlayerSpawn;
    private final Location exitSpawn;
    private final Location JoinTeam1;
    private final Location JoinTeam2;
    private final Location SpawnTeam1;
    private final Location SpawnTeam2;
    private final Location center;

    private ArenaState arenaState = ArenaState.LOBBY;
    private int roundsPlayed = 0;
    private final int maximalPlayingRounds = 3;

    private final int maximalTeamSize = 3;

    private HashMap<TeamEnum, Hologram> holograms = new HashMap<>();
    private HashMap<TeamEnum, TeamManager> teamMap = new HashMap<>();
    private ArenaScoreboard arenaScoreboard;
    private int scheduler = 0;
    private int lobbyMessage = 0;
    private int figthingPlayers = 0;

    private Material[] prizes = new Material[]{Material.DIAMOND, Material.NETHERITE_INGOT};

    private HashMap<CountdownEnum, Integer> countDownInstance = new HashMap<>();
    private HashMap<CountdownEnum, Integer> temp_countDownInstance;
    private CountdownEnum activeCountdown;
    private ArrayList<UUID> needToTeleport = new ArrayList<>();
    private HashMap<UUID, ItemStack> playerPrizeMap = new HashMap<>();

    private boolean inited = false;

    public ArenaManager(String name, Location viewerSpawn, Location deadPlayerSpawn, Location exitSpawn, Location JoinTeam1, Location JoinTeam2, Location SpawnTeam1, Location SpawnTeam2, Location center) {
        this.name = name;
        this.viewerSpawn = viewerSpawn;
        this.deadPlayerSpawn = deadPlayerSpawn;
        this.exitSpawn = exitSpawn;
        this.JoinTeam1 = JoinTeam1;
        this.JoinTeam2 = JoinTeam2;
        this.SpawnTeam1 = SpawnTeam1;
        this.SpawnTeam2 = SpawnTeam2;
        this.center = center;

        holograms.put(TeamEnum.RED, new Hologram(new String[]{"§e" + getName(), TeamEnum.RED.getColor() + "Team " + TeamEnum.RED.getName(), "§e" + getPlayerTeamRed().size() + "§7/" + "§6" + getMaxTeamSize(), (arenaState == ArenaState.LOBBY ? "§bBeitreten" : "§cLäuft")}, this.JoinTeam1));
        holograms.put(TeamEnum.BLUE, new Hologram(new String[]{"§e" + getName(), TeamEnum.BLUE.getColor() + "Team " + TeamEnum.BLUE.getName(), "§e" + getPlayerTeamBlue().size() + "§7/" + "§6" + getMaxTeamSize(), (arenaState == ArenaState.LOBBY ? "§bBeitreten" : "§cLäuft")}, this.JoinTeam2));

        arenaScoreboard = new ArenaScoreboard(this);
        teamMap.put(TeamEnum.RED, new TeamManager(TeamEnum.RED));
        teamMap.put(TeamEnum.BLUE, new TeamManager(TeamEnum.BLUE));
        teamMap.put(TeamEnum.SPECTATOR, new TeamManager(TeamEnum.SPECTATOR));

        countDownInstance.put(CountdownEnum.LOBBY, 13);
        countDownInstance.put(CountdownEnum.PREFIGHT, 10);
        countDownInstance.put(CountdownEnum.FIGHT, 900);
        countDownInstance.put(CountdownEnum.AFTERFIGHT, 5);
        countDownInstance.put(CountdownEnum.WIN, 13);

        temp_countDownInstance = new HashMap<>(countDownInstance);
    }

    public void runLobby() {
        this.inited = true;
        setActiveCountdown(CountdownEnum.LOBBY);
    }

    /**
     * This Methods Ports the Players in their Teams onto the SpawnPositions.
     *
     * @return
     */
    public void initiateFightSequence() {
        if (!inited) return;

        arenaState = ArenaState.FIGHT;
        sendAdMessage();
        figthingPlayers = getFightersList().size();
        prefight();
        Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
            updateHolos();

            for(Player fighters : getFightersList()){
                fighters.setGameMode(GameMode.SURVIVAL);
            }

            for (Player player : getPlayers()) {
                arenaScoreboard.removeScoreboard(player);
                arenaScoreboard.setScoreboard(player);
            }
        }, 0);
    }

    public void prefight() {
        if (!inited) return;
        reset();
        roundsPlayed++;
        setActiveCountdown(CountdownEnum.PREFIGHT);

        Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () ->{
            Bukkit.getPluginManager().callEvent(new ArenaScoreboardUpdateEvent(this));
        }, 0);

    }

    /**
     * This Methods Starts the Fight Countdown and Initiates the Fight
     *
     * @return
     */
    public void fight() {
        if (!inited) return;
        sendArenaMessage("§eDer Kampf beginnt jetzt!");
        setActiveCountdown(CountdownEnum.FIGHT);
    }

    public void afterFight() {
        if (!inited) return;
        setActiveCountdown(CountdownEnum.AFTERFIGHT);
        for (ArenaPlayer fighter : getArenaFightersList()) {
            if (!fighter.isDead()) {
                Player winner = fighter.getPlayer();
                winner.getInventory().clear();
                winner.setHealth(20);
                winner.setFoodLevel(25);
            }
        }

    }

    public void win(TeamEnum winner) {
        if (!inited) return;
        setActiveCountdown(CountdownEnum.WIN);
        sendArenaMessage(winner.getTeamString() + " §egewinnt diese Arena!");
        loadPlayerPrizes(winner);

        Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
            for (ArenaPlayer arenaPlayer : getArenaFightersList()) {
                if (arenaPlayer.isDead()) {
                    arenaPlayer.setDead(false);
                }
                arenaPlayer.getPlayer().teleport(getCenter());
                arenaPlayer.getPlayer().getInventory().clear();
                arenaPlayer.getPlayer().setHealth(20);
                arenaPlayer.getPlayer().setFoodLevel(25);
                arenaPlayer.getPlayer().playSound(arenaPlayer.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3, 1);
            }
        }, 0);

    }

    /**
     * If a Team has the required Wins to Win the whole Arena-Fight, then this Sequence will be triggert
     *
     * @return
     */
    public void finish() {
        for (TeamManager team : teamMap.values()) {
            if (team.getTeamEnum() == TeamEnum.SPECTATOR) continue;
            team.setLoses(0);
            team.setWins(0);
            for (ArenaPlayer arenaPlayer : team.getArenaPlayerList()) {
                alreadyArenaPlayedPlayer.add(arenaPlayer.getPlayer().getUniqueId());
                removePlayer(arenaPlayer.getPlayer(), false);

                if(team.getTeamEnum() != getWinnerTeam()) continue;

                if(files.streamerManager.getStreamer().contains(arenaPlayer.getPlayer().getUniqueId()) || (!arenaPlayer.getPlayer().isOp() && !files.streamerManager.getWorker().contains(arenaPlayer.getPlayer().getUniqueId())) ) {
                    files.heavenManager.setHeavenPlayer(arenaPlayer.getPlayer(), HeavenManager.HeavenDuration.ARENA);
                }
            }
        }

        figthingPlayers = 0;
        roundsPlayed = 0;
        inited = false;
        arenaState = ArenaState.LOBBY;

        Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () ->{
            updateHolos();
            for(Player player : getViewer()){
                arenaScoreboard.removeScoreboard(player);
                arenaScoreboard.setScoreboard(player);
                player.sendMessage(files.prefix + "§7Mit §a/arena leave §7kannst du die Arena verlassen.");
            }
        }, 0);
    }

    /**
     * This Method resets all Players to their SpawnPositions and shows the starts the Fight Sequence again.
     *
     * @return
     */
    public void reset() {

        if (roundsPlayed > 0) {
            for (Player player : getFightersList()) {
                player.sendTitle(Title.builder().title(TeamEnum.RED.getColor() + "" + getTeamRed().getWins() + " §8| " + TeamEnum.BLUE.getColor() + "" + getTeamBlue().getWins()).subtitle("§7Zwischenstand nach §bRunde " + roundsPlayed).fadeIn(20).fadeOut(20).stay(20 * 3).build());
            }
        }

        for (Player player : getFightersList()) {
            ArenaPlayer arenaPlayer = getArenaPlayer(player.getUniqueId());
            assert arenaPlayer != null;
            if (arenaPlayer.isDead()) {
                arenaPlayer.setDead(false);
            }
            player.setFoodLevel(25);
            player.setHealth(20);
            player.getInventory().clear();
            player.setExp(0);
            player.setLevel(0);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
                player.teleport(getSpawnLocations().get(arenaPlayer.getTeam()));
                ArenaKits.standardKit(player, arenaPlayer.getTeam());
            }, 0);
        }

    }


    public ArenaManager addPlayer(Player p, TeamEnum team) {
        ArenaPlayer arenaPlayer = new ArenaPlayer(p.getUniqueId(), p.getInventory().getContents(), p.getLevel(), team);
        teamMap.get(arenaPlayer.getTeam()).addMember(arenaPlayer);
        updateHolos();

        p.teleport(getViewerSpawn());
        p.setHealth(20);
        p.setFoodLevel(25);
        p.setGameMode(GameMode.ADVENTURE);

        arenaScoreboard.setScoreboard(p);
        Bukkit.getPluginManager().callEvent(new ArenaScoreboardUpdateEvent(this));
        Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());

        initScheduler();

        sendFightersMessage(arenaPlayer.getTeam().getColor() + p.getName() + " §7hat die Arena §abetreten!");


        return this;
    }

    public ArenaManager removePlayer(Player p, boolean isDisable) {
        ArenaPlayer arenaPlayer = getArenaPlayer(p);

        teamMap.get(arenaPlayer.getTeam()).removeMember(p.getUniqueId());
        sendFightersMessage(arenaPlayer.getTeam().getColor() + p.getName() + " §7hat die Arena §4verlassen!");
        for (int slot = 0; slot < arenaPlayer.getInventory().length; slot++) {
            p.getInventory().setItem(slot, arenaPlayer.getInventory()[slot]);
        }

        p.giveExpLevels(arenaPlayer.getXP());
        if(!isDisable) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
                updateHolos();
                Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
                Bukkit.getPluginManager().callEvent(new ArenaPlayerLeaveEvent(this, arenaPlayer, p));
                Bukkit.getPluginManager().callEvent(new ArenaScoreboardUpdateEvent(this));

                p.teleport(exitSpawn);
                arenaScoreboard.removeScoreboard(p);
            }, 0);
        }

        if (getFightersList().size() < 1) {
            deInitScheduler();
        }

        return this;
    }

    public ArenaManager addViewer(Player p, TeamEnum team) {
        p.setHealth(20);
        p.setFoodLevel(25);
        p.setGameMode(GameMode.ADVENTURE);

        ArenaPlayer arenaPlayer = new ArenaPlayer(p.getUniqueId(), p.getInventory().getContents(), p.getLevel(), team);
        arenaPlayer.setJoinPoint(p.getLocation());
        p.teleport(getViewerSpawn());
        teamMap.get(arenaPlayer.getTeam()).addMember(arenaPlayer);
        arenaScoreboard.setScoreboard(p);
        Bukkit.getPluginManager().callEvent(new ArenaScoreboardUpdateEvent(this));
        p.getInventory().clear();
        p.setLevel(0);
        p.setExp(0);

        Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());

        return this;
    }

    public ArenaManager removeViewer(Player p, boolean disable) {
        ArenaPlayer arenaPlayer = getArenaPlayer(p);

        teamMap.get(arenaPlayer.getTeam()).removeMember(p.getUniqueId());
        for (int slot = 0; slot < arenaPlayer.getInventory().length; slot++) {
            p.getInventory().setItem(slot, arenaPlayer.getInventory()[slot]);
        }

        arenaScoreboard.removeScoreboard(p);
        Bukkit.getPluginManager().callEvent(new ArenaScoreboardUpdateEvent(this));
        if(!disable) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), () -> {
                assert arenaPlayer.getJoinPoint() != null;
                p.teleport(arenaPlayer.getJoinPoint());
                p.giveExpLevels(arenaPlayer.getXP());
                Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
            }, 0);
        }

        return this;
    }

    public ArenaPlayer getPlayer(UUID uuid) {
        return getArenaPlayers().get(uuid);
    }

    public ArenaPlayer getPlayer(Player p) {
        return getArenaPlayers().get(p.getUniqueId());
    }


    public boolean containsUUID(UUID uuid) {
        return getArenaPlayers().containsKey(uuid);
    }

    public void removeArena() {
        files.arenaManager.remove(name);

        for (Hologram hologram : getHolograms().values()) {
            hologram.remove();
        }

        files.arenaFile.getSection("arena").set(name, null);

        Bukkit.getPluginManager().callEvent(new PlayerChangePermissionEvent());
    }

    public HashMap<TeamEnum, TeamManager> getTeamList() {
        return teamMap;
    }

    public ArrayList<Player> getFightersList() {
        ArrayList<Player> playerList = new ArrayList<>();
        try {

            playerList.addAll(getTeamList().get(TeamEnum.BLUE).getPlayerList());
            playerList.addAll(getTeamList().get(TeamEnum.RED).getPlayerList());

        } catch (NullPointerException ex) {
            return playerList;
        }
        return playerList;
    }

    public ArrayList<ArenaPlayer> getArenaFightersList() {
        ArrayList<ArenaPlayer> playerList = new ArrayList<>();
        try {

            playerList.addAll(getTeamList().get(TeamEnum.BLUE).getArenaPlayerList());
            playerList.addAll(getTeamList().get(TeamEnum.RED).getArenaPlayerList());

        } catch (NullPointerException ex) {
            return playerList;
        }
        return playerList;
    }

    public ArrayList<Player> getViewer() {
        ArrayList<Player> arenaPlayers = new ArrayList<>();
        try {
            arenaPlayers.addAll(getTeamList().get(TeamEnum.SPECTATOR).getPlayerList());
        } catch (NullPointerException ex) {
            return arenaPlayers;
        }
        return arenaPlayers;
    }

    public ArrayList<ArenaPlayer> getArenaViewer() {
        ArrayList<ArenaPlayer> arenaPlayers = new ArrayList<>();
        try {
            arenaPlayers.addAll(getTeamList().get(TeamEnum.SPECTATOR).getArenaPlayerList());
        } catch (NullPointerException ex) {
            return arenaPlayers;
        }
        return arenaPlayers;
    }

    public TeamManager getTeamRed() {
        return teamMap.get(TeamEnum.RED);
    }

    public ArrayList<Player> getPlayerTeamRed() {
        ArrayList<Player> arenaPlayers = new ArrayList<>();
        try {
            arenaPlayers.addAll(getTeamList().get(TeamEnum.RED).getPlayerList());
        } catch (NullPointerException ex) {
            return arenaPlayers;
        }
        return arenaPlayers;
    }

    public ArrayList<ArenaPlayer> getArenaTeamRed() {
        ArrayList<ArenaPlayer> arenaPlayers = new ArrayList<>();
        try {
            arenaPlayers.addAll(getTeamList().get(TeamEnum.RED).getArenaPlayerList());
        } catch (NullPointerException ex) {
            return arenaPlayers;
        }
        return arenaPlayers;
    }

    public TeamManager getTeamBlue() {
        return teamMap.get(TeamEnum.BLUE);
    }

    public ArrayList<Player> getPlayerTeamBlue() {
        ArrayList<Player> arenaPlayers = new ArrayList<>();
        try {
            arenaPlayers.addAll(getTeamList().get(TeamEnum.BLUE).getPlayerList());
        } catch (NullPointerException ex) {
            return arenaPlayers;
        }
        return arenaPlayers;
    }

    public ArrayList<ArenaPlayer> getArenaTeamBlue() {
        ArrayList<ArenaPlayer> arenaPlayers = new ArrayList<>();
        try {
            arenaPlayers.addAll(getTeamList().get(TeamEnum.BLUE).getArenaPlayerList());
        } catch (NullPointerException ex) {
            return arenaPlayers;
        }
        return arenaPlayers;
    }

    public Player getLeadingPlayer(ArrayList<Player> team){
        Player leadingPlayer = null;
        StreamerManager streamerManager = files.streamerManager;
        for (Player player : team) {
            if(leadingPlayer != null){
                if(streamerManager.getStreamer().contains(player)){
                    leadingPlayer = player;
                    continue;
                }

                if(streamerManager.getStreamer().contains(leadingPlayer)){
                    continue;
                }

                if(player.isOp() && leadingPlayer.isOp()){
                    continue;
                }

                if(player.isOp() && !leadingPlayer.isOp()){
                    leadingPlayer = player;
                    continue;
                }

                if(!player.isOp() && leadingPlayer.isOp()){
                    continue;
                }

                if(streamerManager.getWorker().contains(leadingPlayer)){
                    continue;
                }

                if(streamerManager.getWorker().contains(player)){
                    leadingPlayer = player;
                    continue;
                }

                leadingPlayer = team.stream().sorted(Comparator.comparing(Player::getName)).collect(Collectors.toList()).get(0);

            } else {
                leadingPlayer = player;
            }
        }

        return leadingPlayer;
    }

    public void sendAdMessage(){
        TeamManager teamRed = getTeamRed();
        TeamManager teamBlue = getTeamBlue();
        String message = files.prefix + "§7In der Arena §b" + name + " §7kämpfen das Team von " + teamRed.getTeamEnum().getColor() + getLeadingPlayer(teamRed.getPlayerList()).getName() + " §7gegen das Team von " + teamBlue.getTeamEnum().getColor() + getLeadingPlayer(teamBlue.getPlayerList()).getName() + "!";

        TextComponent textComponent = new TextComponent(files.prefix + "§7Um zuzuschauen §8[");
        TextComponent mainComponent = new TextComponent("§bKlick hier");
        TextComponent endComponent = new TextComponent("§8]");
        mainComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arena view " + name));
        mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(teamRed.getTeamEnum().getColor() + getLeadingPlayer(teamRed.getPlayerList()).getName() + " §7vs " + teamBlue.getTeamEnum().getColor() + getLeadingPlayer(teamBlue.getPlayerList()).getName()).create()));
        textComponent.addExtra(mainComponent);
        textComponent.addExtra(endComponent);

        for(Player online : Bukkit.getOnlinePlayers()){
            if(!getPlayers().contains(online)){
                online.sendMessage(message);
                online.spigot().sendMessage(textComponent);
            }
        }
    }

    public void sendArenaMessage(String message) {
        String fullMesssage = "§8| " + ChatColor.YELLOW + getName() + " " + files.prefix + message;
        for (TeamManager team : teamMap.values()) {
            for (Player player : team.getPlayerList()) {
                player.sendMessage(fullMesssage);
            }
        }
    }

    public void sendFightersMessage(String message) {
        String fullMesssage = "§8| " + ChatColor.YELLOW + getName() + " " + files.prefix + message;
        for (Player player : getFightersList()) {
            if (player != null) {
                player.sendMessage(fullMesssage);
            }
        }
    }

    public ArrayList<ArenaPlayer> getReadyPlayers() {
        ArrayList<ArenaPlayer> isReady = new ArrayList<>();

        for (ArenaPlayer arenaPlayer : getArenaFightersList()) {
            if (arenaPlayer.isReady()) isReady.add(arenaPlayer);
        }

        return isReady;
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> player = new ArrayList<>();

        if (getPlayerTeamRed().size() > 0) {
            player.addAll(getPlayerTeamRed());
        }
        if (getPlayerTeamBlue().size() > 0) {
            player.addAll(getPlayerTeamBlue());
        }
        if (getViewer().size() > 0) {
            player.addAll(getViewer());
        }

        return player;
    }

    public HashMap<UUID, ArenaPlayer> getArenaPlayers() {
        HashMap<UUID, ArenaPlayer> hashMap = new HashMap<>();

        for (ArenaPlayer arenaPlayer : getArenaViewer()) {
            hashMap.put(arenaPlayer.getUUID(), arenaPlayer);
        }

        for (ArenaPlayer arenaPlayer : getArenaTeamRed()) {
            hashMap.put(arenaPlayer.getUUID(), arenaPlayer);
        }

        for (ArenaPlayer arenaPlayer : getArenaTeamBlue()) {
            hashMap.put(arenaPlayer.getUUID(), arenaPlayer);
        }

        return hashMap;
    }

    public HashMap<TeamEnum, Hologram> getHolograms() {
        return holograms;
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public String getName() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
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

    public Location getCenter() {
        return center;
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
        locations.put("Center", getCenter());
        return locations;
    }

    public HashMap<TeamEnum, Location> getSpawnLocations() {
        HashMap<TeamEnum, Location> joinsMap = new HashMap<>();

        joinsMap.put(TeamEnum.RED, getSpawnTeam1());
        joinsMap.put(TeamEnum.BLUE, getSpawnTeam2());
        return joinsMap;
    }

    public HashMap<TeamEnum, Location> getJoinLocations() {
        HashMap<TeamEnum, Location> joinsMap = new HashMap<>();

        joinsMap.put(TeamEnum.RED, getJoinTeam1());
        joinsMap.put(TeamEnum.BLUE, getJoinTeam2());
        return joinsMap;
    }

    public static ArenaManager getPlayerArena(UUID uuid) {
        for (ArenaManager arenaManager : files.arenaManager.values()) {
            if (arenaManager.containsUUID(uuid)) {
                return arenaManager;
            }
        }
        return null;
    }

    public static boolean containsPlayer(UUID uuid) {
        return getPlayerArena(uuid) != null;
    }

    public static boolean containsPlayer(Player player) {
        return getPlayerArena(player.getUniqueId()) != null;
    }

    @Nullable
    public static ArenaManager isNeedToTeleport(UUID uuid){
        for (ArenaManager arenaManager : files.arenaManager.values()) {
            if (arenaManager.getNeedToTeleport().contains(uuid)) {
                return arenaManager;
            }
        }

        return null;
    }

    @Nullable
    public static ItemStack hasPrize(UUID uuid){
        ItemStack stack = null;
        for (ArenaManager arenaManager : files.arenaManager.values()) {
            if (arenaManager.getPlayerPrizeMap().containsKey(uuid)) {
                stack = arenaManager.getPlayerPrizeMap().get(uuid);
                arenaManager.getPlayerPrizeMap().remove(uuid);
            }
        }
        return stack;
    }

    public void loadPlayerPrizes(TeamEnum winner){
        final int figthingPlayers = this.figthingPlayers;
        int prize = 0;

        ArrayList<Player> winnerPlayers = teamMap.get(winner).getPlayerList();
        ItemStack prizeItem = null;
        if(prize == 0){
            prizeItem = new ItemStack(prizes[0], (int) Math.floor(figthingPlayers/winnerPlayers.size()));
        } else {
            if(figthingPlayers <= 2){
                prizeItem = new ItemStack(prizes[0], (int) Math.floor(figthingPlayers/winnerPlayers.size()));
            } else {
                prizeItem = new ItemStack(prizes[1], (int) Math.floor(figthingPlayers/winnerPlayers.size()/2));
            }
        }

        for (Player winnerPlayer : winnerPlayers) {
            System.out.println(files.debug + "§e" + winnerPlayer.getName() + " §7hat durch die Arena §e" + name + " §b" + prizeItem.getAmount() + "x " + prizeItem.getType().name().toUpperCase() + " §7bekommen.");
            Bukkit.getScheduler().scheduleAsyncDelayedTask(main.getInstance(), () -> {
                winnerPlayer.sendMessage(" ");
                winnerPlayer.sendMessage(files.prefix + "§4WICHTIG!");
                winnerPlayer.sendMessage(files.prefix + "§7Du kannst deinen Preis mit §a/arena prize §7entgegenehmen!");
                winnerPlayer.sendMessage(files.prefix + "§cWichtig§8: §7Du bekommst nur einmalig zugang zu diesem Inventar!");
                winnerPlayer.sendMessage(" ");
            }, 20*15);
            playerPrizeMap.put(winnerPlayer.getUniqueId(), prizeItem);
        }
    }

    public static ArenaPlayer getArenaPlayer(UUID uuid) {
        ArenaManager arenaManager = getPlayerArena(uuid);

        if (arenaManager == null) {
            return null;
        }

        return arenaManager.containsUUID(uuid) ? arenaManager.getPlayer(uuid) : null;
    }

    public static ArenaPlayer getArenaPlayer(Player player) {
        return getArenaPlayer(player.getUniqueId());
    }

    public CountdownEnum getActiveCountdown() {
        return activeCountdown;
    }

    public String getActiveCountDownString() {
        if (getActiveCountdown() == null) {
            return null;
        }
        int countDown = getCountDownInstance().get(getActiveCountdown());
        int minutes = (int) Math.floor(countDown / 60);
        int seconds = countDown % 60;
        return (minutes > 9 ? "" + minutes : "0" + minutes) + ":" + (seconds > 9 ? "" + seconds : "0" + seconds);
    }

    public Integer setActiveCountdown(CountdownEnum countdown) {

        activeCountdown = countdown;
        return temp_countDownInstance.get(countdown);
    }

    public void resetActiveCountdown() {
        temp_countDownInstance.put(getActiveCountdown(), countDownInstance.get(getActiveCountdown()));
        setActiveCountdown(null);
    }

    public HashMap<CountdownEnum, Integer> getCountDownInstance() {
        return temp_countDownInstance;
    }

    public Integer getRoundsPlayed() {
        return roundsPlayed;
    }

    public Integer getMaximalPlayingRounds() {
        return maximalPlayingRounds;
    }

    public Integer getMaxTeamSize() {
        return maximalTeamSize;
    }

    public ArrayList<UUID> getNeedToTeleport(){
        return needToTeleport;
    }

    public void setNeedToTeleport(ArrayList<UUID> needToTeleport){
        this.needToTeleport = needToTeleport;
    }

    public ArenaScoreboard getArenaScoreboard(){
        return arenaScoreboard;
    }

    public HashMap<UUID, ItemStack> getPlayerPrizeMap(){
        return playerPrizeMap;
    }

    public TeamEnum getWinnerTeam() {
        if (getTeamRed().getWins() < getTeamBlue().getWins() && getTeamBlue().getWins() >= Math.floor(maximalPlayingRounds / 2) + 1) {
            return TeamEnum.BLUE;
        } else if (getTeamBlue().getWins() < getTeamRed().getWins() && getTeamRed().getWins() >= Math.floor(maximalPlayingRounds / 2) + 1) {
            return TeamEnum.RED;
        }

        return null;
    }

    private void updateHolos(){
        for (Map.Entry<TeamEnum, Hologram> hologram : holograms.entrySet()) {
            if (hologram.getKey() == TeamEnum.RED) {
                hologram.getValue().updateLine(0, "§e" + getName());
                hologram.getValue().updateLine(1, hologram.getKey().getTeamString());
                hologram.getValue().updateLine(2, "§e" + getPlayerTeamRed().size() + "§7/" + "§6" + getMaxTeamSize());
                hologram.getValue().updateLine(3, (arenaState == ArenaState.LOBBY ? (getPlayerTeamRed().size() > getMaxTeamSize() ? "§cVoll" : "§bBeitreten") : "§cLäuft"));
            }
            if (hologram.getKey() == TeamEnum.BLUE) {
                hologram.getValue().updateLine(0, "§e" + getName());
                hologram.getValue().updateLine(1, hologram.getKey().getTeamString());
                hologram.getValue().updateLine(2, "§e" + getPlayerTeamBlue().size() + "§7/" + "§6" + getMaxTeamSize());
                hologram.getValue().updateLine(3, (arenaState == ArenaState.LOBBY ? (getPlayerTeamBlue().size() > getMaxTeamSize() ? "§cVoll" : "§bBeitreten") : "§cLäuft"));
            }
        }
    }

    private void initScheduler() {
        if (Bukkit.getScheduler().isQueued(scheduler)) {
            return;
        }

        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(main.getInstance(), () -> {

            try {

                int countDown = temp_countDownInstance.get(getActiveCountdown());

                if (countDown != 0) {
                    countDown--;
                    temp_countDownInstance.put(getActiveCountdown(), countDown);
                }

                if (getActiveCountdown() == CountdownEnum.FIGHT) {
                    for (Player player : getPlayers()) {
                        player.sendActionBar(TeamEnum.RED.getTeamString() + " §8(" + TeamEnum.RED.getColor() + getTeamRed().getWins() + "§8) §f§lvs §8(" + TeamEnum.BLUE.getColor() + getTeamBlue().getWins() + "§8) " + TeamEnum.BLUE.getTeamString());
                    }
                } else if (getActiveCountdown() == CountdownEnum.PREFIGHT) {
                    String[] colors = {"§aGO", "§21", "§e2", "§c3", "§44", "§55"};
                    if (countDown <= 5 && countDown >= 0) {
                        for (Player player : getFightersList()) {
                            player.sendTitle(Title.builder().title(colors[countDown]).fadeOut(2).fadeIn(2).stay(16).build());
                        }
                    }
                } else if(getActiveCountdown() == CountdownEnum.LOBBY || getActiveCountdown() == CountdownEnum.AFTERFIGHT || getActiveCountdown() == CountdownEnum.WIN){
                    for (Player player : getPlayers()){
                        player.sendActionBar("§7Timer §8» §b" + countDown);
                    }
                }


                if ((countDown == 60 || countDown == 30 || countDown == 10 || countDown == 20 || countDown == 5 || countDown == 3 || countDown == 2 || countDown == 1)) {
                    if (getActiveCountdown() == CountdownEnum.LOBBY) {
                        sendArenaMessage("§7Die Arena startet in §b" + countDown + " Sekunden.");
                        for (Player player : getPlayers()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 3, countDown / 10);
                        }
                    } else if (getActiveCountdown() == CountdownEnum.PREFIGHT) {
                        sendArenaMessage("§7Runde §b" + getRoundsPlayed() + " §7startet in §b" + countDown + " Sekunden");
                        for (Player player : getPlayers()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 3, 4);
                        }
                    } else if (getActiveCountdown() == CountdownEnum.FIGHT) {
                        sendArenaMessage("§7Es verbleiben §b" + countDown + " Sekunden §7um den Kampf zu entscheiden!");
                        for (Player player : getPlayers()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 3, 1);
                        }
                    } else if (getActiveCountdown() == CountdownEnum.WIN) {
                        sendArenaMessage("§7Die Arena wird in §b" + countDown + " Sekunden §7geschlossen!");
                    }
                }

                if (countDown == 0) {

                    switch (getActiveCountdown()) {
                        case LOBBY:
                            resetActiveCountdown();
                            initiateFightSequence();

                            return;
                        case PREFIGHT:
                            resetActiveCountdown();
                            fight();

                            return;
                        case FIGHT:
                            resetActiveCountdown();
                            sendFightersMessage("§4Es konnte kein Sieger festgestellt werden! §7Diese Runde wird wiederholt!");
                            roundsPlayed--;
                            afterFight();

                            return;
                        case AFTERFIGHT:
                            resetActiveCountdown();
                            if (getWinnerTeam() != null) {
                                win(getWinnerTeam());
                            } else {
                                prefight();
                            }

                            return;
                        case WIN:
                            resetActiveCountdown();
                            finish();
                    }

                    resetActiveCountdown();
                }
            } catch (NullPointerException ignored) {

            }

            arenaScoreboard.updateTime();

                if(arenaState == ArenaState.LOBBY){
                    lobbyMessage++;
                    if(getFightersList().size() != getReadyPlayers().size()){
                        if(getActiveCountdown() != null){
                            resetActiveCountdown();
                            inited = false;
                            sendArenaMessage("§4Arena wurde angehalten weil nicht jeder Spieler die Arena angenommen hat.");
                            lobbyMessage = 0;
                        }

                        int notAccepted = getFightersList().size() - getReadyPlayers().size();
                        if(lobbyMessage == 60){
                            if (notAccepted < 2) {
                                sendArenaMessage("§7Es hat §b" + (notAccepted) + " Spieler §7noch nicht accepted!");
                            } else {
                                sendArenaMessage("§7Es haben §b" + (notAccepted) + " Spieler §7noch nicht accepted!");
                            }
                            lobbyMessage = 0;
                        }

                        for (Player player : getPlayers()) {
                            if (player == null) continue;
                            ArenaPlayer arenaPlayer = getArenaPlayer(player);
                            player.sendActionBar("§7Du bist in " + arenaPlayer.getTeam().getTeamString() + " §f| §a" + getReadyPlayers().size() + "§8/§7" + getFightersList().size());
                        }
                        return;
                    }

                    if(getPlayerTeamRed().size() <= 0 || getPlayerTeamBlue().size() <= 0){
                        if(getActiveCountdown() != null){
                            resetActiveCountdown();
                            inited = false;
                            sendArenaMessage("§4Arena wurde angehalten aufgrund von zu wenig Spielern.");
                            lobbyMessage = 0;
                        }

                        for (Player player : getPlayers()) {
                            if (player == null) continue;
                            ArenaPlayer arenaPlayer = getArenaPlayer(player);
                            player.sendActionBar("§7Du bist in " + arenaPlayer.getTeam().getTeamString() + " §f| §aWarten auf Spieler..");
                        }

                        if (lobbyMessage == 90) {
                            sendArenaMessage("§7Warten auf §bSpieler...");
                            lobbyMessage = 0;
                        }

                        return;
                    }

                    if(getPlayerTeamBlue().size() != getPlayerTeamRed().size()) {
                        int difference;
                        TeamEnum biggerTeam;

                        if (getPlayerTeamRed().size() < getPlayerTeamBlue().size()) {
                            difference = getPlayerTeamBlue().size() - getPlayerTeamRed().size();
                            biggerTeam = TeamEnum.BLUE;
                        } else {
                            difference = getPlayerTeamRed().size() - getPlayerTeamBlue().size();
                            biggerTeam = TeamEnum.RED;
                        }

                        if (difference > 1) {
                            if (getActiveCountdown() != null) {
                                resetActiveCountdown();
                                inited = false;
                                sendArenaMessage("§4Arena wurde angehalten, da die Differenz der Teams größer als 1 ist.");
                                lobbyMessage = 0;
                            }

                            if (lobbyMessage == 60) {
                                sendArenaMessage("§7Das " + biggerTeam.getTeamString() + " §7ist um §b" + difference + " Spieler §7größer, es gibt nur eine toleranz von §b1 Spieler §7unterschied!");
                                lobbyMessage = 0;
                            }
                            return;
                        }
                    }

                    if(!inited){
                        runLobby();
                    }


                }



        }, 20, 20);
    }

    private void deInitScheduler() {
        Bukkit.getScheduler().cancelTask(scheduler);
        setActiveCountdown(null);
    }

}
