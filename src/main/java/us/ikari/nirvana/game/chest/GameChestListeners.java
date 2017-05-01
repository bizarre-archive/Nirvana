package us.ikari.nirvana.game.chest;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import us.ikari.nirvana.game.chest.content.GameChestContent;

import java.util.List;
import java.util.Random;

public class GameChestListeners implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Chest) {
            GameChest chest = GameChest.getByBlock(((Chest) holder).getBlock());
            if (chest != null) {

                int amount = new Random().nextInt(chest.getMax() - chest.getMin()) + chest.getMin();

                outer: for (int i = 0; i < 4; i++) {
                    if (event.getPlayer().getInventory().getArmorContents()[i] == null) {
                        if (new Random().nextInt(2) == 1) {
                            List<ItemStack> potentialArmor = GameChestContent.getArmorFromSlot(GameChestContent.getArmor(chest.getContent()), i);

                            if (potentialArmor != null) {

                                for (ItemStack itemStack : potentialArmor) {
                                    if (event.getPlayer().getInventory().contains(itemStack)) {
                                        break outer;
                                    }
                                }

                                amount--;

                                int attempts = 0;
                                int index = new Random().nextInt(inventory.getSize());
                                while (inventory.getItem(index) != null && attempts <= inventory.getSize()) {
                                    index = new Random().nextInt(inventory.getSize());
                                    attempts++;
                                }


                                inventory.setItem(index, potentialArmor.get(new Random().nextInt(potentialArmor.size())));
                            }
                        }
                    }
                }

                if (!(GameChestContent.containsItemByType((Player) event.getPlayer(), "FOOD"))) {
                    if (new Random().nextInt(2) == 1) {
                        amount--;
                        List<ItemStack> items = GameChestContent.getItemsByType(chest.getContent(), "FOOD");

                        int attempts = 0;
                        int index = new Random().nextInt(inventory.getSize());
                        while (inventory.getItem(index) != null && attempts <= inventory.getSize()) {
                            index = new Random().nextInt(inventory.getSize());
                            attempts++;
                        }


                        inventory.setItem(index, items.get(new Random().nextInt(items.size())));
                    }
                }

                if (!(GameChestContent.containsItemByType((Player) event.getPlayer(), "SWORD"))) {
                    if (new Random().nextInt(2) == 1) {
                        List<ItemStack> items = GameChestContent.getItemsByType(chest.getContent(), "SWORD");
                        amount--;

                        int attempts = 0;
                        int index = new Random().nextInt(inventory.getSize());
                        while (inventory.getItem(index) != null && attempts <= inventory.getSize()) {
                            index = new Random().nextInt(inventory.getSize());
                            attempts++;
                        }


                        inventory.setItem(index, items.get(new Random().nextInt(items.size())));
                    }
                }

                for (int i = 0; i < amount; i++) {

                    ItemStack itemStack = chest.getContent().getItems().get(new Random().nextInt(chest.getContent().getItems().size()));
                    while (event.getInventory().contains(itemStack)) {
                        itemStack = chest.getContent().getItems().get(new Random().nextInt(chest.getContent().getItems().size()));
                    }

                    int attempts = 0;
                    int index = new Random().nextInt(inventory.getSize());
                    while (inventory.getItem(index) != null && attempts <= inventory.getSize()) {
                        index = new Random().nextInt(inventory.getSize());
                        attempts++;
                    }

                    inventory.setItem(index, itemStack);
                }

                GameChest.getLoadedChests().add((Chest) holder);
            }
        }
    }

}
