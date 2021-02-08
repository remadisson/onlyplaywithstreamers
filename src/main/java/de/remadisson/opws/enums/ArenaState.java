package de.remadisson.opws.enums;

public enum ArenaState {
    LOBBY ("Lobby"), FIGHT("Fight");

    private String name;

    ArenaState(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
