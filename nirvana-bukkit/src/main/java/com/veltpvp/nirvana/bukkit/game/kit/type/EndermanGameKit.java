package com.veltpvp.nirvana.bukkit.game.kit.type;

import com.veltpvp.nirvana.bukkit.game.kit.PowerfulGameKit;
import com.veltpvp.nirvana.bukkit.game.kit.ability.GameKitAbility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EndermanGameKit extends PowerfulGameKit {

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{};
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{null, null, null, null};
    }

    @Override
    public String getIdentifier() {
        return "enderman";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.ENDER_PEARL);
    }

    @Override
    public List<GameKitAbility> getAbilities() {
        return Arrays.asList(GameKitAbility.PEARL_ON_KILL);
    }
}
