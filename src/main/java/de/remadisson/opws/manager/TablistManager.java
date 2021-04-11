package de.remadisson.opws.manager;


import de.remadisson.opws.files;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TablistManager {

    private static TablistManager instance;
    private Scoreboard scoreboard;
    private HashMap<UUID, String> teams;

    public TablistManager(){
        scoreboard = new Scoreboard();
        teams = new HashMap<>();
    }

    public void updateTeam(Player p, String prefix, EnumChatFormat color, String suffix, int level){
        String s = level + p.getUniqueId().toString().substring(1, 6);
        if(p.getName().startsWith("§r")) return;

        if(scoreboard.getTeam(s) != null){
            scoreboard.removeTeam(scoreboard.getTeam(s));
        }

        ScoreboardTeam team = scoreboard.createTeam(s);

        if(p.getUniqueId().equals(files.MaikEagle)){
            team.setPrefix(new ChatComponentText("§l" + prefix));
        } else {
            team.setPrefix(new ChatComponentText(prefix));
        }

        team.setColor(color);

        team.setSuffix(new ChatComponentText(suffix));

        teams.put(p.getUniqueId(), s);
        update();
    }

    private void update(){
        for(Player online : Bukkit.getOnlinePlayers()){
            if(online.getName().startsWith("§r")) continue;

            if(!scoreboard.getTeam(teams.get(online.getUniqueId())).getPlayerNameSet().contains(online.getName())){
                scoreboard.getTeam(teams.get(online.getUniqueId())).getPlayerNameSet().add(online.getName());
            }

            sendPacket(new PacketPlayOutScoreboardTeam(scoreboard.getTeam(teams.get(online.getUniqueId())), 1));
            sendPacket(new PacketPlayOutScoreboardTeam(scoreboard.getTeam(teams.get(online.getUniqueId())), 0));
        }
    }

    private void sendPacket(Packet<?> packet){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CraftPlayer p =  (CraftPlayer) onlinePlayer;
            p.getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static TablistManager getInstance(){

        if(instance == null){
            instance = new TablistManager();
        }

        return instance;
    }


}
