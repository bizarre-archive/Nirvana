package com.veltpvp.nirvana.bukkit.game.kit.type;

import com.veltpvp.nirvana.bukkit.game.kit.GameKit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MerchantGameKit implements GameKit {

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{new ItemStack(Material.BREAD, 16), new ItemStack(Material.EGG, 16)};
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{null, null, null, null};
    }

    @Override
    public String getIdentifier() {
        return "merchant";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BREAD);
    }

}
