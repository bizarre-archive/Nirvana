package com.veltpvp.nirvana.game.chest.content;

import com.veltpvp.nirvana.game.chest.GameChestTier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface GameChestContent {
    List<ItemStack> getItems(GameChestTier tier);
    List<ItemStack> getArmor(GameChestTier tier);
    List<ItemStack> getWeapons(GameChestTier tier);
    List<ItemStack> getBlocks(GameChestTier tier);
}
