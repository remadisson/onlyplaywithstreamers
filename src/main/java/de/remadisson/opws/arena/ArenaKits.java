package de.remadisson.opws.arena;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class ArenaKits {

    public static void standardKit(Player p){

        PlayerInventory inventory = p.getInventory();

        ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET,1);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        helmet.addEnchantment(Enchantment.DURABILITY, 3);

        inventory.setHelmet(helmet);


        ItemStack chestnut = new ItemStack(Material.NETHERITE_CHESTPLATE, 1);

        inventory.setChestplate(chestnut);


        ItemStack legs = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
        legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        legs.addEnchantment(Enchantment.DURABILITY, 3);

        inventory.setLeggings(legs);


        ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        boots.addEnchantment(Enchantment.DURABILITY, 3);

        inventory.addItem(new ItemStack(Material.STONE_SWORD, 1));
        inventory.addItem(new ItemStack(Material.CROSSBOW, 1));
        inventory.addItem(new ItemStack(Material.STONE_AXE, 1));
        inventory.addItem(new ItemStack(Material.BREAD, 16));
        inventory.addItem(new ItemStack(Material.ARROW, 16));
        inventory.setItemInOffHand(new ItemStack(Material.SHIELD, 1));
        inventory.addItem(new ItemStack(Material.SUSPICIOUS_STEW, 3));
    }

}
