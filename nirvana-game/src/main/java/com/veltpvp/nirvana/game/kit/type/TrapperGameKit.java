package com.veltpvp.nirvana.game.kit.type;

import com.veltpvp.nirvana.game.kit.GameKit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TrapperGameKit implements GameKit {

    @Override
    public ItemStack[] getContents() {
        ItemStack pistons = new ItemStack(Material.PISTON_BASE, 16);
        ItemStack slimeBalls = new ItemStack(Material.SLIME_BALL, 8);
        ItemStack door = new ItemStack(Material.IRON_DOOR, 1);
        ItemStack redstone = new ItemStack(Material.REDSTONE_BLOCK, 8);
        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);

        return new ItemStack[]{pickaxe, door, pistons, slimeBalls, redstone, new ItemStack(Material.IRON_INGOT, 8), new ItemStack(Material.LOG, 8), new ItemStack(Material.COBBLESTONE, 16)};
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{new ItemStack(Material.CHAINMAIL_HELMET), null, null, null};
    }

    @Override
    public String getIdentifier() {
        return "trapper";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.PISTON_STICKY_BASE);
    }

}
