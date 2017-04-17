package us.ikari.nirvana.game.chest.content;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface GameChestContent {
    List<ItemStack> getItems();

    static List<ItemStack> getArmor(GameChestContent content) {
        List<ItemStack> toReturn = new ArrayList<>();

        for (ItemStack itemStack : content.getItems()) {
            if (itemStack.getType().name().contains("CHESTPLATE") || itemStack.getType().name().contains("LEGGINGS") || itemStack.getType().name().contains("BOOTS") || itemStack.getType().name().contains("HELMET")) {
                toReturn.add(itemStack);
            }
        }

        return toReturn;
    }

    static List<ItemStack> getArmorFromSlot(List<ItemStack> items, int slot) {
        List<ItemStack> toReturn = new ArrayList<>();
        String query;

        if (slot == 0) {
            query = "HELMET";
        } else if (slot == 1) {
            query = "CHESTPLATE";
        } else if (slot == 2) {
            query = "LEGGINGS";
        } else if (slot == 3) {
            query = "BOOTS";
        } else {
            return null;
        }

        for (ItemStack itemStack : items) {
            if (itemStack != null && itemStack.getType().name().contains(query)) {
                toReturn.add(itemStack);
            }
        }

        return toReturn;
    }

}