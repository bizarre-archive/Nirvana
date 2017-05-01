package us.ikari.nirvana.game.kit.type;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import us.ikari.nirvana.game.kit.GameKit;
import us.ikari.nirvana.game.kit.PowerfulGameKit;
import us.ikari.nirvana.game.kit.ability.GameKitAbility;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.util.Arrays;
import java.util.List;

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
