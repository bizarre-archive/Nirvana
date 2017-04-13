package us.ikari.nirvana.game.kit.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.ikari.nirvana.game.kit.GameKit;

import java.util.Arrays;
import java.util.List;

public class DefaultGameKit implements GameKit {


    @Override
    public List<ItemStack> getContents() {
        return Arrays.asList(new ItemStack(Material.IRON_SWORD));
    }

    @Override
    public List<ItemStack> getArmor() {
        return Arrays.asList(new ItemStack(Material.IRON_HELMET));
    }

    @Override
    public String getName() {
        return "Default";
    }

}
