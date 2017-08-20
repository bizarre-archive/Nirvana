package com.veltpvp.nirvana.game.chest;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Set;

public class GameChestUtils {

    public static boolean hasFullArmor(Player player) {
        PlayerInventory inventory = player.getInventory();
        return containsType(inventory, "HELMET")
                && containsType(inventory, "CHESTPLATE")
                && containsType(inventory, "LEGGINGS")
                && containsType(inventory, "BOOTS");
    }

    public static ItemStack getBetterSword(Player player, List<ItemStack> items, Inventory inventory) {
        for (ItemStack item : items) {

            if (containsType(inventory, getType(item))) {
                continue;
            }

            ItemStack sword = getSword(player);
            if (sword != null) {
                if (isBetter(item, sword)) {
                    return item;
                }
            }

            return item;
        }
        return null;
    }

    public static ItemStack getBetterArmorItem(Player player, List<ItemStack> items, Inventory inventory) {
        for (ItemStack item : items) {

            if (containsType(inventory, getType(item))) {
                continue;
            }

            if (containsType(player.getInventory(), getType(item))) {
                continue;
            }

            if (isHelmet(item) && player.getInventory().getHelmet() != null) {
                ItemStack helmet = player.getInventory().getHelmet();

                if (!(hasFullArmor(player))) {
                    continue;
                }

                if (isBetter(item, helmet)) {
                    return item;
                }

                continue;
            }

            if (isChestplate(item) && player.getInventory().getChestplate() != null) {
                ItemStack chestplate = player.getInventory().getChestplate();

                if (!(hasFullArmor(player))) {
                    continue;
                }

                if (isBetter(item, chestplate)) {
                    return item;
                }

                continue;
            }

            if (isLeggings(item) && player.getInventory().getLeggings() != null) {
                ItemStack leggings = player.getInventory().getLeggings();

                if (!(hasFullArmor(player))) {
                    continue;
                }

                if (isBetter(item, leggings)) {
                    return item;
                }

                continue;
            }

            if (isBoots(item) && player.getInventory().getBoots() != null) {
                ItemStack boots = player.getInventory().getBoots();

                if (isBetter(item, boots)) {
                    return item;
                }

                continue;
            }

            return item;
        }

        return null;
    }


    public static ItemStack getSword(Player player) {
        ItemStack previous = null;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType().name().contains("SWORD")) {
                if (previous == null) {
                    previous = itemStack;
                } else {
                    if (isBetter(itemStack, previous)) {
                        previous = itemStack;
                    }
                }
            }
        }

        return previous;
    }
    private static boolean containsType(Inventory inventory, String type) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null) {
                if (itemStack.getType().name().contains(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getType(ItemStack itemStack) {
        String toReturn = itemStack.getType().name();

        if (itemStack.getType().name().contains("HELMET")) toReturn = "HELMET";
        if (itemStack.getType().name().contains("CHESTPLATE")) toReturn = "CHESTPLATE";
        if (itemStack.getType().name().contains("LEGGINGS")) toReturn = "LEGGINGS";
        if (itemStack.getType().name().contains("BOOTS")) toReturn = "BOOTS";
        if (itemStack.getType().name().contains("SWORD")) toReturn = "SWORD";

        return toReturn;
    }

    private static int getTier(ItemStack itemStack) {
        int toReturn = 0;

        if (itemStack.getType().name().contains("DIAMOND")) {
            toReturn = 4;
        } else if (itemStack.getType().name().contains("IRON")) {
            toReturn = 3;
        } else if (itemStack.getType().name().contains("GOLD") || itemStack.getType().name().contains("LEATHER")) {
            toReturn = 2;
        } else if (itemStack.getType().name().contains("STONE")) {
            toReturn = 1;
        }

        return toReturn;
    }

    private static boolean isBetter(ItemStack first, ItemStack second) {
        int firstLevels = 0;
        for (Enchantment enchantment : first.getEnchantments().keySet()) {
            firstLevels += first.getEnchantmentLevel(enchantment);
        }

        int secondLevels = 0;
        for (Enchantment enchantment : second.getEnchantments().keySet()) {
            secondLevels += second.getEnchantmentLevel(enchantment);
        }

        if (getTier(first) > getTier(second)) {
            if (secondLevels >= firstLevels + 1) {
                return false;
            }
            return true;
        }

        if (getTier(first) == getTier(second)) {
            return firstLevels > secondLevels;
        }

        return false;
    }

    public static boolean containsBlocks(Inventory inventory) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getType().isBlock()) {
                return true;
            }
        }
        return false;
    }


    public static boolean isHelmet(ItemStack itemStack) {
        return itemStack.getType().name().contains("HELMET");
    }

    public static boolean isChestplate(ItemStack itemStack) {
        return itemStack.getType().name().contains("CHESTPLATE");
    }

    public static boolean isLeggings(ItemStack itemStack) {
        return itemStack.getType().name().contains("LEGGINGS");
    }

    public static boolean isBoots(ItemStack itemStack) {
        return itemStack.getType().name().contains("BOOTS");
    }

}
