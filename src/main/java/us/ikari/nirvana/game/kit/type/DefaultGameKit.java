package us.ikari.nirvana.game.kit.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.ikari.nirvana.game.kit.GameKit;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

public class DefaultGameKit implements GameKit {

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[]{new ItemStack(Material.WOOD_PICKAXE), new ItemStack(Material.WOOD_AXE), new ItemStack(Material.WOOD_SPADE)};
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{null, null, null, new MenuItemBuilder(Material.LEATHER_BOOTS).build().getItemStack()};
    }

    @Override
    public String getIdentifier() {
        return "default";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.WOOD_PICKAXE);
    }

}
