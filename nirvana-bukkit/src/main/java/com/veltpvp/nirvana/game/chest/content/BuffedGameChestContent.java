package com.veltpvp.nirvana.game.chest.content;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class BuffedGameChestContent implements GameChestContent {
    
    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> toReturn = new ArrayList<>();

        toReturn.add(new ItemStack(Material.COOKED_BEEF, 64));

        toReturn.add(new ItemStack(Material.GOLDEN_APPLE, 1));
        toReturn.add(new ItemStack(Material.GOLDEN_APPLE, 2));
        toReturn.add(new ItemStack(Material.GOLDEN_APPLE, 1));
        toReturn.add(new ItemStack(Material.GOLDEN_APPLE, 2));

        toReturn.add(new ItemStack(Material.ENDER_PEARL, 1));
        toReturn.add(new ItemStack(Material.ENDER_PEARL, 2));
        toReturn.add(new ItemStack(Material.ENDER_PEARL, 1));
        toReturn.add(new ItemStack(Material.ENDER_PEARL, 2));

        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16453).amount(2).build().getItemStack()); // splash healing I
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16453).amount(3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16453).amount(4).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8229).amount(2).build().getItemStack()); // drink healing II
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8229).amount(3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8229).amount(4).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8266).build().getItemStack()); // speed ii
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8194).build().getItemStack()); // speed i
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8194).amount(2).build().getItemStack()); // speed i
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16418).build().getItemStack()); // speed i

        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8227).build().getItemStack()); // fire res
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8259).build().getItemStack()); // speed i

        toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 4).build().getItemStack());

        toReturn.add(new ItemStack(Material.EXP_BOTTLE, 12));
        toReturn.add(new ItemStack(Material.EXP_BOTTLE, 24));
        toReturn.add(new ItemStack(Material.EXP_BOTTLE, 32));

        toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_FALL, 4).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.DURABILITY, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_FIRE, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_PROJECTILE, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_EXPLOSIONS, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_FALL, 4).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.DURABILITY, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_FIRE, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_PROJECTILE, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_EXPLOSIONS, 1).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.IRON_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.IRON_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.IRON_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.IRON_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).build().getItemStack());

        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 2));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 2));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 3));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 3));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 1));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 1));

        return toReturn;
    }
    
    
    
}
