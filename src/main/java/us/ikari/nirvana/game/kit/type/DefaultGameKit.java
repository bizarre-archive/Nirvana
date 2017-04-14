package us.ikari.nirvana.game.kit.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.ikari.nirvana.game.kit.GameKit;

import java.util.Arrays;
import java.util.List;

public class DefaultGameKit implements GameKit {

    @Override
    public List<ItemStack> getContents() {
        return Arrays.asList(new ItemStack(Material.WOOD_SWORD), new ItemStack(Material.WOOD_AXE), new ItemStack(Material.WOOD_PICKAXE));
    }

    @Override
    public List<ItemStack> getArmor() {
        return Arrays.asList(new ItemStack(Material.LEATHER_HELMET));
    }

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.WOOD_PICKAXE);
    }
}
