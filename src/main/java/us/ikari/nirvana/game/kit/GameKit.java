package us.ikari.nirvana.game.kit;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface GameKit {

    List<ItemStack> getContents();
    List<ItemStack> getArmor();
    String getName();

}
