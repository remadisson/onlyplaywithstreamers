package de.remadisson.opws.enums;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum TeamEnum {

    RED("Rot", ChatColor.RED, DyeColor.RED), BLUE("Blau", ChatColor.BLUE, DyeColor.BLUE), SPECTATOR("Viewer", ChatColor.GRAY, DyeColor.GRAY);

    public String name;
    public ChatColor color;
    public DyeColor dyecolor;

    TeamEnum(String name, ChatColor color, DyeColor dyecolor){
        this.name = name;
        this.color = color;
        this.dyecolor = dyecolor;
    }

    public String getName(){
        return name;
    }

    public DyeColor getDyeColor(){
        return dyecolor;
    }

    public ChatColor getColor(){
        return color;
    }

    public String getTeamString(){
        if(getName().toLowerCase() == "viewer"){
            return getColor() + "" + getName();
        }
        return getColor() + "Team " + getName();
    }

}
