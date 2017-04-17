package us.ikari.nirvana.game.kit.type;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import us.ikari.nirvana.game.kit.PermissibleGameKit;
import us.ikari.nirvana.game.kit.PowerfulGameKit;
import us.ikari.nirvana.game.kit.ability.GameKitAbility;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

import java.util.Arrays;
import java.util.List;

public class BomberGameKit extends PowerfulGameKit implements PermissibleGameKit {

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{new MenuItemBuilder(Material.TNT).amount(24).build().getItemStack(), new MenuItemBuilder(Material.WOOD_PLATE).amount(6).build().getItemStack(), new MenuItemBuilder(Material.WATER_BUCKET).build().getItemStack()};
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
    public String getPermission() {
        return "kit.bomber";
    }

    @Override
    public List<GameKitAbility> getAbilities() {
        return Arrays.asList(GameKitAbility.EXPLODE_ON_DEATH);
    }
}
