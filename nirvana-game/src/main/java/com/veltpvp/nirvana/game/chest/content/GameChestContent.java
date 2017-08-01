package com.veltpvp.nirvana.game.chest.content;

import org.bukkit.entity.Player;
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

    static boolean containsItemByType(Player player, String type) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null) {
                if (type.equalsIgnoreCase("FOOD") && itemStack.getType().isEdible()) {
                    return true;
                }
                if (itemStack.getType().name().toUpperCase().contains(type.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    static List<ItemStack> getItemsByType(GameChestContent content, String type) {
        List<ItemStack> toReturn = new ArrayList<>();

        for (ItemStack itemStack : content.getItems()) {
            if (itemStack != null) {
                if (type.equalsIgnoreCase("FOOD") && itemStack.getType().isEdible()) {
                    toReturn.add(itemStack);
                }
                if (itemStack.getType().name().toUpperCase().contains(type.toUpperCase())) {
                    toReturn.add(itemStack);
                }
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
