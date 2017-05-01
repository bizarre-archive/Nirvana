package us.ikari.nirvana.game.kit.type;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import us.ikari.nirvana.game.kit.GameKit;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

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
