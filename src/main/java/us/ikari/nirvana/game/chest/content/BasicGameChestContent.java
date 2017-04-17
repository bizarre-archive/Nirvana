package us.ikari.nirvana.game.chest.content;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class BasicGameChestContent implements GameChestContent {
    
    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> toReturn = new ArrayList<>();
        
        toReturn.add(new ItemStack(Material.COOKED_BEEF, 16));
        toReturn.add(new ItemStack(Material.BAKED_POTATO, 16));
        toReturn.add(new ItemStack(Material.GOLDEN_CARROT, 16));
        toReturn.add(new ItemStack(Material.COOKED_BEEF, 32));
        toReturn.add(new ItemStack(Material.BAKED_POTATO, 32));
        toReturn.add(new ItemStack(Material.GOLDEN_CARROT, 32));

        toReturn.add(new ItemStack(Material.IRON_SWORD));
        toReturn.add(new ItemStack(Material.DIAMOND_SWORD));
        toReturn.add(new MenuItemBuilder(Material.STONE_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.IRON_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.IRON_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.IRON_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.IRON_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());

        toReturn.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
        toReturn.add(new ItemStack(Material.DIAMOND_LEGGINGS));
        toReturn.add(new ItemStack(Material.DIAMOND_BOOTS));
        toReturn.add(new ItemStack(Material.DIAMOND_HELMET));

        toReturn.add(new ItemStack(Material.IRON_CHESTPLATE));
        toReturn.add(new ItemStack(Material.IRON_LEGGINGS));
        toReturn.add(new ItemStack(Material.IRON_BOOTS));
        toReturn.add(new ItemStack(Material.IRON_HELMET));

        toReturn.add(new ItemStack(Material.GLOWSTONE_DUST, 4));
        toReturn.add(new ItemStack(Material.GLOWSTONE_DUST, 6));
        toReturn.add(new ItemStack(Material.GLOWSTONE_DUST, 8));
        toReturn.add(new ItemStack(Material.GLOWSTONE_DUST, 16));
        toReturn.add(new ItemStack(Material.GLOWSTONE, 2));
        toReturn.add(new ItemStack(Material.GLOWSTONE, 4));
        toReturn.add(new ItemStack(Material.GLOWSTONE, 8));

        toReturn.add(new ItemStack(Material.SULPHUR, 2));
        toReturn.add(new ItemStack(Material.SULPHUR, 4));
        toReturn.add(new ItemStack(Material.SULPHUR, 6));
        toReturn.add(new ItemStack(Material.SULPHUR, 8));
        toReturn.add(new ItemStack(Material.SULPHUR, 16));

        toReturn.add(new ItemStack(Material.FISHING_ROD));
        toReturn.add(new ItemStack(Material.BOW));
        toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 1).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 3).build().getItemStack());
        toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_INFINITE, 1).build().getItemStack());

        toReturn.add(new ItemStack(Material.EGG, 12));
        toReturn.add(new ItemStack(Material.EGG, 16));
        toReturn.add(new ItemStack(Material.SNOW_BALL, 12));
        toReturn.add(new ItemStack(Material.SNOW_BALL, 16));

        toReturn.add(new ItemStack(Material.ARROW, 8));
        toReturn.add(new ItemStack(Material.ARROW,  16));
        toReturn.add(new ItemStack(Material.ARROW, 24));

        toReturn.add(new ItemStack(Material.EXP_BOTTLE, 12));
        toReturn.add(new ItemStack(Material.EXP_BOTTLE, 24));
        toReturn.add(new ItemStack(Material.EXP_BOTTLE, 32));

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

        toReturn.add(new ItemStack(Material.WOOD, 32));
        toReturn.add(new ItemStack(Material.WOOD, 36));
        toReturn.add(new ItemStack(Material.WOOD, 48));
        toReturn.add(new ItemStack(Material.WOOD, 20));
        toReturn.add(new ItemStack(Material.WOOD, 18));

        toReturn.add(new ItemStack(Material.COBBLESTONE, 32));
        toReturn.add(new ItemStack(Material.COBBLESTONE, 36));
        toReturn.add(new ItemStack(Material.COBBLESTONE, 48));
        toReturn.add(new ItemStack(Material.COBBLESTONE, 20));
        toReturn.add(new ItemStack(Material.COBBLESTONE, 18));

        toReturn.add(new ItemStack(Material.FLINT_AND_STEEL));

        toReturn.add(new ItemStack(Material.WATER_BUCKET));
        toReturn.add(new ItemStack(Material.LAVA_BUCKET));

        toReturn.add(new ItemStack(Material.DIAMOND_PICKAXE));
        toReturn.add(new ItemStack(Material.IRON_PICKAXE));
        toReturn.add(new MenuItemBuilder(Material.STONE_PICKAXE).enchantment(Enchantment.DIG_SPEED, 3).build().getItemStack());

        toReturn.add(new ItemStack(Material.DIAMOND_AXE));
        toReturn.add(new ItemStack(Material.DIAMOND_AXE));

        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 2));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 2));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 3));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 3));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 1));
        toReturn.add(new ItemStack(Material.BREWING_STAND_ITEM, 1));

        return toReturn;
    }
    
    
    
}
