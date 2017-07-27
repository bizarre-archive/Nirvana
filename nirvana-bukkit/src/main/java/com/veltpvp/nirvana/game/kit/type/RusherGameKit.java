package com.veltpvp.nirvana.game.kit.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.veltpvp.nirvana.game.kit.GameKit;

public class RusherGameKit implements GameKit {

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{new ItemStack(Material.STONE_SWORD), new ItemStack(Material.GLASS, 32)};
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS)};
    }

    @Override
    public String getIdentifier() {
        return "rusher";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GLASS);
    }

}
