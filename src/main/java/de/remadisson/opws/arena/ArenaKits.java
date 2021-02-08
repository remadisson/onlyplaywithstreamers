package de.remadisson.opws.arena;

import de.remadisson.opws.enums.TeamEnum;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class ArenaKits {

    public static void standardKit(Player p, TeamEnum team) {

        PlayerInventory inventory = p.getInventory();

        ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET, 1);
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

        inventory.setBoots(boots);

        inventory.addItem(new ItemStack(Material.STONE_SWORD, 1));
        inventory.addItem(new ItemStack(Material.CROSSBOW, 1));
        inventory.addItem(new ItemStack(Material.STONE_AXE, 1));
        inventory.addItem(new ItemStack(Material.BREAD, 16));
        inventory.addItem(new ItemStack(Material.ARROW, 16));

        ItemStack shield = new ItemStack(Material.SHIELD, 1);
        ItemMeta shieldmeta = shield.getItemMeta();
        BlockStateMeta shieldblockmeta = (BlockStateMeta) shieldmeta;
        Banner shieldstate = (Banner) shieldblockmeta.getBlockState();
        shieldstate.setBaseColor(team.getDyeColor());
        shieldstate.update();
        shieldblockmeta.setBlockState(shieldstate);
        shield.setItemMeta(shieldblockmeta);

        inventory.setItemInOffHand(shield);

        ItemStack stew = new ItemStack(Material.SUSPICIOUS_STEW, 1);
        SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) stew.getItemMeta();
        stewMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1, false, false), false);
        stew.setItemMeta(stewMeta);
        inventory.addItem(stew);
        inventory.addItem(stew);
        inventory.addItem(stew);

    }

}
