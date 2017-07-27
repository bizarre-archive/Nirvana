package com.veltpvp.nirvana.game.kit.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.veltpvp.nirvana.game.kit.GameKit;

public class MinerGameKit implements GameKit {

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{new ItemStack(Material.IRON_PICKAXE), new ItemStack(Material.IRON_AXE), new ItemStack(Material.IRON_SPADE)};
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{new ItemStack(Material.IRON_HELMET), null, null, null};
    }

    @Override
    public String getIdentifier() {
        return "miner";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_PICKAXE);
    }

}
