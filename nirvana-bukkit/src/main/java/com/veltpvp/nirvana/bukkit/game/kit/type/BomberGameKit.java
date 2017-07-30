package com.veltpvp.nirvana.bukkit.game.kit.type;

import com.veltpvp.nirvana.bukkit.game.kit.PowerfulGameKit;
import com.veltpvp.nirvana.bukkit.game.kit.ability.GameKitAbility;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.util.Arrays;
import java.util.List;

public class BomberGameKit extends PowerfulGameKit {

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{new MenuItemBuilder(Material.TNT).amount(16).build().getItemStack(), new MenuItemBuilder(Material.WOOD_PLATE).amount(6).build().getItemStack(), new MenuItemBuilder(Material.WATER_BUCKET).build().getItemStack()};
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{null, null, null, new MenuItemBuilder(Material.IRON_BOOTS).enchantment(Enchantment.PROTECTION_FALL, 4).build().getItemStack()};
    }

    @Override
    public String getIdentifier() {
        return "bomber";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.TNT);
    }


    @Override
    public List<GameKitAbility> getAbilities() {
        return Arrays.asList(GameKitAbility.EXPLODE_ON_DEATH);
    }
}
