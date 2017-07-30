package com.veltpvp.nirvana.bukkit.game.kit.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.veltpvp.nirvana.bukkit.game.kit.GameKit;

public class GeneratorGameKit implements GameKit {

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{new ItemStack(Material.COBBLESTONE, 64), new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.LAVA_BUCKET)};
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{null, null, null, null};
    }

    @Override
    public String getIdentifier() {
        return "generator";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.COBBLESTONE);
    }

}
