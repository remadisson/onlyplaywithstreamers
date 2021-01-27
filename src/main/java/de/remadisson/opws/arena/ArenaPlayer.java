package de.remadisson.opws.arena;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class ArenaPlayer {

    private Player player;
    private UUID uuid;
    private HashMap<Integer, ItemStack> inventory;

    public ArenaPlayer(UUID uuid, HashMap<Integer, ItemStack> inventory){
        this.uuid = uuid;
        this.inventory = inventory;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public Player getPlayer(){
        return player;
    }

    public UUID getUUID(){
        return uuid;
    }

    public HashMap<Integer, ItemStack>  getInventory(){
        return inventory;
    }

}
