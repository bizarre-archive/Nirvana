package us.ikari.nirvana.game.kit.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.ikari.nirvana.game.kit.GameKit;
import us.ikari.phoenix.gui.menu.item.MenuItemBuilder;

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
