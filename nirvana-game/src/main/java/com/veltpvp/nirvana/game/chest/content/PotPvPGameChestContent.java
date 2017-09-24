package com.veltpvp.nirvana.game.chest.content;

import com.veltpvp.nirvana.game.chest.GameChestTier;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PotPvPGameChestContent implements GameChestContent {

    private static final Random RANDOM = new Random();

    @Override
    public List<ItemStack> getItems(GameChestTier tier) {
        List<ItemStack> toReturn = new ArrayList<>();

        if (tier == GameChestTier.BASIC) {
            toReturn.add(new ItemStack(Material.DIAMOND_PICKAXE));
            toReturn.add(new ItemStack(Material.DIAMOND_AXE));

            toReturn.add(new ItemStack(Material.BAKED_POTATO, 16));
            toReturn.add(new ItemStack(Material.BAKED_POTATO, 8));
            toReturn.add(new ItemStack(Material.COOKED_BEEF, 16));
            toReturn.add(new ItemStack(Material.COOKED_BEEF, 8));
            toReturn.add(new ItemStack(Material.GOLDEN_CARROT, 8));
            toReturn.add(new ItemStack(Material.GOLDEN_CARROT, 16));

            toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 1).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 3).build().getItemStack());

            toReturn.add(new ItemStack(Material.FISHING_ROD));
            toReturn.add(new ItemStack(Material.ARROW, RANDOM.nextInt(10) + 5));

            toReturn.add(new ItemStack(Material.ENDER_PEARL, RANDOM.nextInt(2) + 1));

            toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.POTION).amount(2).durability(16421).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.POTION).durability(8226).build().getItemStack());
        }

        if (tier == GameChestTier.BUFFED) {
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_PICKAXE).enchantment(Enchantment.DIG_SPEED, 3).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_AXE).enchantment(Enchantment.DIG_SPEED, 3).build().getItemStack());

            toReturn.add(new MenuItemBuilder(Material.POTION).durability(16421).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.POTION).amount(2).durability(16421).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.POTION).amount(3).durability(16421).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.POTION).amount(4).durability(16421).build().getItemStack());

            toReturn.add(new MenuItemBuilder(Material.POTION).durability(8226).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.POTION).amount(2).durability(8226).build().getItemStack());

            toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 2).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 4).build().getItemStack());

            toReturn.add(new ItemStack(Material.ARROW, RANDOM.nextInt(20) + 5));

            toReturn.add(new MenuItemBuilder(Material.POTION).durability(16420).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.POTION).durability(16426).build().getItemStack());

            toReturn.add(new MenuItemBuilder(Material.POTION).durability(8259).build().getItemStack());

            toReturn.add(new ItemStack(Material.ENDER_PEARL, RANDOM.nextInt(4) + 1));
        }

        if (tier == GameChestTier.OP) {
            toReturn.add(new MenuItemBuilder(Material.POTION).amount(3).durability(16421).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.POTION).amount(4).durability(16421).build().getItemStack());

            toReturn.add(new MenuItemBuilder(Material.POTION).durability(16388).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.POTION).durability(16458).build().getItemStack());

            toReturn.add(new MenuItemBuilder(Material.POTION).amount(2).durability(8226).build().getItemStack());

            toReturn.add(new ItemStack(Material.ARROW, 64));

            toReturn.add(new ItemStack(Material.TNT, RANDOM.nextInt(7) + 1));
            toReturn.add(new ItemStack(Material.ENDER_PEARL, RANDOM.nextInt(8) + 1));

            toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 5).build().getItemStack());
        }

        return toReturn;
    }

    @Override
    public List<ItemStack> getArmor(GameChestTier tier) {
        List<ItemStack> toReturn = new ArrayList<>();

        if (tier == GameChestTier.BASIC) {
            toReturn.add(new ItemStack(Material.DIAMOND_HELMET));
            toReturn.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
            toReturn.add(new ItemStack(Material.DIAMOND_LEGGINGS));
            toReturn.add(new ItemStack(Material.DIAMOND_BOOTS));
        }

        if (tier == GameChestTier.BUFFED) {
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        }

        if (tier == GameChestTier.OP) {
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build().getItemStack());
        }

        return toReturn;
    }

    @Override
    public List<ItemStack> getWeapons(GameChestTier tier) {
        List<ItemStack> toReturn = new ArrayList<>();

        if (tier == GameChestTier.BASIC) {
            toReturn.add(new ItemStack(Material.DIAMOND_SWORD));
        }

        if (tier == GameChestTier.BUFFED) {
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build().getItemStack());
        }

        if (tier == GameChestTier.OP) {
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).enchantment(Enchantment.FIRE_ASPECT, 1).build().getItemStack());
        }

        return toReturn;
    }

    @Override
    public List<ItemStack> getBlocks(GameChestTier tier) {
        List<ItemStack> toReturn = new ArrayList<>();

        if (tier == GameChestTier.BASIC) {
            toReturn.add(new ItemStack(Material.WOOD, RANDOM.nextInt(20) + 10));
            toReturn.add(new ItemStack(Material.COBBLESTONE, RANDOM.nextInt(15) + 10));
        }

        if (tier == GameChestTier.BUFFED) {
            toReturn.add(new ItemStack(Material.WOOD, 64));
            toReturn.add(new ItemStack(Material.COBBLESTONE, 64));
        }

        return toReturn;
    }

}
