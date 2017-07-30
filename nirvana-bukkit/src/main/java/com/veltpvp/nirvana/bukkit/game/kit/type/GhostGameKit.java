package com.veltpvp.nirvana.bukkit.game.kit.type;

import com.veltpvp.nirvana.bukkit.game.kit.GameKit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

public class GhostGameKit implements GameKit {

    @Override
    public ItemStack[] getContents() {
        ItemStack potion = new Potion(PotionType.INVISIBILITY).toItemStack(1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 30, 0), true);
        potion.setItemMeta(meta);

        return new ItemStack[]{new MenuItemBuilder(Material.WOOD_SWORD).enchantment(Enchantment.DAMAGE_ALL, 3).build().getItemStack(), potion};
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{null, null, null, null};
    }

    @Override
    public String getIdentifier() {
        return "ghost";
    }

    @Override
    public ItemStack getIcon() {
        return getContents()[1];
    }

}
