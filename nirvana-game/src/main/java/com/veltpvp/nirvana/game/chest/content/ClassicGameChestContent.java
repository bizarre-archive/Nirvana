package com.veltpvp.nirvana.game.chest.content;

import com.veltpvp.nirvana.game.chest.GameChestTier;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClassicGameChestContent implements GameChestContent {

    private static final Random RANDOM = new Random();

    @Override
    public List<ItemStack> getItems(GameChestTier tier) {
        List<ItemStack> toReturn = new ArrayList<>();

        if (tier == GameChestTier.BASIC) {
            toReturn.add(new ItemStack(Material.IRON_PICKAXE));
            toReturn.add(new ItemStack(Material.IRON_AXE));

            toReturn.add(new ItemStack(Material.BAKED_POTATO, 16));
            toReturn.add(new ItemStack(Material.BAKED_POTATO, 8));
            toReturn.add(new ItemStack(Material.COOKED_BEEF, 16));
            toReturn.add(new ItemStack(Material.COOKED_BEEF, 8));
            toReturn.add(new ItemStack(Material.GOLDEN_CARROT, 8));
            toReturn.add(new ItemStack(Material.GOLDEN_CARROT, 16));

            toReturn.add(new MenuItemBuilder(Material.BOW).build().getItemStack());

            toReturn.add(new ItemStack(Material.APPLE, RANDOM.nextInt(10) + 1));

            toReturn.add(new ItemStack(Material.FISHING_ROD));
            toReturn.add(new ItemStack(Material.FLINT_AND_STEEL));

            toReturn.add(new ItemStack(Material.ARROW, RANDOM.nextInt(10) + 5));
        }

        if (tier == GameChestTier.BUFFED) {
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_PICKAXE).enchantment(Enchantment.DIG_SPEED, 3).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_AXE).enchantment(Enchantment.DIG_SPEED, 3).build().getItemStack());

            toReturn.add(new MenuItemBuilder(Material.POTION).durability(16453).build().getItemStack());

            toReturn.add(new ItemStack(Material.GOLDEN_APPLE, RANDOM.nextInt(3) + 1));

            toReturn.add(new ItemStack(Material.ARROW, RANDOM.nextInt(20) + 5));
        }

        if (tier == GameChestTier.OP) {
            toReturn.add(new MenuItemBuilder(Material.POTION).amount(1).durability(16421).build().getItemStack());

            toReturn.add(new ItemStack(Material.ARROW, 64));

            toReturn.add(new ItemStack(Material.GOLDEN_APPLE, RANDOM.nextInt(5) + 1));

            toReturn.add(new ItemStack(Material.TNT, RANDOM.nextInt(7) + 1));
            toReturn.add(new ItemStack(Material.ENDER_PEARL, RANDOM.nextInt(3) + 1));

            toReturn.add(new MenuItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 1).build().getItemStack());
        }

        return toReturn;
    }

    @Override
    public List<ItemStack> getArmor(GameChestTier tier) {
        List<ItemStack> toReturn = new ArrayList<>();

        if (tier == GameChestTier.BASIC) {
            toReturn.add(new ItemStack(Material.LEATHER_HELMET));
            toReturn.add(new ItemStack(Material.LEATHER_CHESTPLATE));
            toReturn.add(new ItemStack(Material.LEATHER_LEGGINGS));
            toReturn.add(new ItemStack(Material.LEATHER_BOOTS));

            toReturn.add(new ItemStack(Material.CHAINMAIL_HELMET));
            toReturn.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
            toReturn.add(new ItemStack(Material.CHAINMAIL_LEGGINGS));
            toReturn.add(new ItemStack(Material.CHAINMAIL_BOOTS));

            toReturn.add(new ItemStack(Material.GOLD_HELMET));
            toReturn.add(new ItemStack(Material.GOLD_CHESTPLATE));
            toReturn.add(new ItemStack(Material.GOLD_LEGGINGS));
            toReturn.add(new ItemStack(Material.GOLD_BOOTS));

            toReturn.add(new ItemStack(Material.IRON_HELMET));
            toReturn.add(new ItemStack(Material.IRON_CHESTPLATE));
            toReturn.add(new ItemStack(Material.IRON_LEGGINGS));
            toReturn.add(new ItemStack(Material.IRON_BOOTS));
        }

        if (tier == GameChestTier.BUFFED) {
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_HELMET).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_CHESTPLATE).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_LEGGINGS).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.DIAMOND_BOOTS).build().getItemStack());
        }

        if (tier == GameChestTier.OP) {
            toReturn.add(new MenuItemBuilder(Material.IRON_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.IRON_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.IRON_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
            toReturn.add(new MenuItemBuilder(Material.IRON_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build().getItemStack());
        }

        return toReturn;
    }

    @Override
    public List<ItemStack> getWeapons(GameChestTier tier) {
        List<ItemStack> toReturn = new ArrayList<>();

        if (tier == GameChestTier.BASIC) {
            toReturn.add(new ItemStack(Material.STONE_SWORD));
            toReturn.add(new ItemStack(Material.WOOD_SWORD));
            toReturn.add(new ItemStack(Material.GOLD_SWORD));
        }

        if (tier == GameChestTier.BUFFED) {
            toReturn.add(new ItemStack(Material.IRON_SWORD));
            toReturn.add(new ItemStack(Material.DIAMOND_SWORD));
        }

        if (tier == GameChestTier.OP) {
            toReturn.add(new MenuItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build().getItemStack());
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
            toReturn.add(new ItemStack(Material.WOOD, 32));
            toReturn.add(new ItemStack(Material.COBBLESTONE, 32));
        }

        return toReturn;
    }

}
