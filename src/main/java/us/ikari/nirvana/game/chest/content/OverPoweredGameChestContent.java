package us.ikari.nirvana.game.chest.content;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class OverPoweredGameChestContent implements GameChestContent {
    
    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> toReturn = new ArrayList<>();

        toReturn.add(new ItemStack(Material.GOLDEN_APPLE, 1));
        toReturn.add(new ItemStack(Material.GOLDEN_APPLE, 2));
        toReturn.add(new ItemStack(Material.GOLDEN_APPLE, 3));
        toReturn.add(new ItemStack(Material.GOLDEN_APPLE, 4));
        toReturn.add(new ItemStack(Material.GOLDEN_APPLE, 5));

        toReturn.add(new ItemStack(Material.ENDER_PEARL, 4));
        toReturn.add(new ItemStack(Material.ENDER_PEARL, 6));
        toReturn.add(new ItemStack(Material.ENDER_PEARL, 8));
        toReturn.add(new ItemStack(Material.ENDER_PEARL, 16));

        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(2).build().getItemStack()); // splash healing II
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(4).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(5).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(6).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(4).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(4).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).amount(2).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8229).amount(2).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8229).amount(2).build().getItemStack()); // drink healing II
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8229).amount(3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8229).amount(4).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8229).amount(5).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.POTION).durability(8227).build().getItemStack()); // fire res

        toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 4).enchantment(Enchantment.ARROW_FIRE, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 5).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).enchantment(Enchantment.FIRE_ASPECT, 2).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).enchantment(Enchantment.FIRE_ASPECT, 1).build().getItemStack());

        toReturn.add(new MenuItemBuilder(Material.FISHING_ROD).enchantment(Enchantment.KNOCKBACK, 4).enchantment(Enchantment.DURABILITY, 10).build().getItemStack());

        return toReturn;
    }
    
    
    
}
