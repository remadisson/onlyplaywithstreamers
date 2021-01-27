package de.remadisson.opws.enums;

import org.bukkit.ChatColor;

public enum TeamEnum {

    RED("Rot", ChatColor.RED), BLUE("Blau", ChatColor.BLUE), SPECTATOR("Spec", ChatColor.GRAY);

    public String name;
    public ChatColor color;

    private TeamEnum(String name, ChatColor color){
        this.name = name;
        this.color = color;
    }

    public String getName(){
        return name;
    }

    public ChatColor getColor(){
        return color;
    }

}
