package com.veltpvp.nirvana.game.chest;

import com.veltpvp.nirvana.game.GameUtils;
import com.veltpvp.nirvana.game.chest.content.GameChestContent;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GameChestListeners implements Listener {

    private static final Random RANDOM = new Random();

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Chest) {
            Inventory chestInventory = ((Chest) holder).getBlockInventory();

            if (GameChest.getLoadedChests().contains(holder)) {
                return;
            }

            Map.Entry<GameChest, GameChestTier> chestInformation = GameChest.getByBlock(((Chest) holder).getBlock());

            System.out.println(chestInformation == null);
            for (GameChest chest : GameChest.values()) {
                System.out.println(chest.name() + ":" + chest.getInstances().size());
            }

            if (chestInformation != null) {
                GameChest chest = chestInformation.getKey();
                GameChestTier tier = chestInformation.getValue();
                List<ItemStack> armorItems = chest.getContent().getArmor(tier);
                List<ItemStack> weapons = chest.getContent().getWeapons(tier);

                if (tier == null) {
                    tier = GameChestTier.BASIC;
                }

                int amount = RANDOM.nextInt(4) + 4;

                for (int i = 0; i < amount; i++) {

                    for (int armor = 0; armor < RANDOM.nextInt(4); armor++) {
                        boolean doLoop = GameChestUtils.hasFullArmor(player) ? RANDOM.nextInt(8 - (tier.getIdentifier()) * 2) == 1 : RANDOM.nextBoolean();

                        if (doLoop) {
                            Collections.shuffle(armorItems);
                            ItemStack itemStack = GameChestUtils.getBetterArmorItem(player, armorItems, inventory);
                            if (itemStack != null) {
                                amount--;
                                chestInventory.setItem(RANDOM.nextInt(chestInventory.getSize()), itemStack);
                            }
                        }

                    }

                    ItemStack sword = GameChestUtils.getSword(player);
                    if (sword == null) {
                        Collections.shuffle(weapons);
                        ItemStack itemStack = GameChestUtils.getBetterSword(player, weapons, inventory);
                        if (itemStack != null) {
                            chestInventory.setItem(RANDOM.nextInt(chestInventory.getSize()), itemStack);
                            continue;
                        }
                    } else {
                        if (RANDOM.nextInt(5 - (tier.getIdentifier())) == 1) {
                            Collections.shuffle(weapons);
                            ItemStack itemStack = GameChestUtils.getBetterSword(player, weapons, inventory);
                            if (itemStack != null) {
                                chestInventory.setItem(RANDOM.nextInt(chestInventory.getSize()), itemStack);
                                continue;
                            }
                        }
                    }

                    if (!GameChestUtils.containsBlocks(inventory)) {
                        if (RANDOM.nextBoolean()) {
                            if (!chest.getContent().getBlocks(tier).isEmpty()) {
                                inventory.setItem(RANDOM.nextInt(chestInventory.getSize()), chest.getContent().getBlocks(tier).get(RANDOM.nextInt(chest.getContent().getBlocks(tier).size())));
                                continue;
                            }
                        }
                    }

                    ItemStack itemStack = chest.getContent().getItems(tier).get(RANDOM.nextInt(chest.getContent().getItems(tier).size()));
                    if (!inventory.contains(itemStack)) {
                        inventory.setItem(RANDOM.nextInt(chestInventory.getSize()), itemStack);
                        continue;
                    }

                    i--;
                }


                GameChest.getLoadedChests().add((Chest) holder);
            }
        }
    }

}
