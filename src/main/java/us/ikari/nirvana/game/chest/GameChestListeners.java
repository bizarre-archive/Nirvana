package us.ikari.nirvana.game.chest;

import org.bukkit.block.Chest;
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

                for (int i = 0; i < 4; i++) {
                    if (event.getPlayer().getInventory().getArmorContents()[i] == null) {
                        if (new Random().nextInt(100) >= 33) {
                            List<ItemStack> potentialArmor = GameChestContent.getArmorFromSlot(GameChestContent.getArmor(chest.getContent()), i);
                            if (potentialArmor != null) {
                                amount--;

                                int index = new Random().nextInt(inventory.getSize());
                                while (inventory.getItem(index) != null) {
                                    index = new Random().nextInt(inventory.getSize());
                                }

                                inventory.setItem(index, potentialArmor.get(new Random().nextInt(potentialArmor.size())));
                            }
                        }
                    }
                }

                for (int i = 0; i < amount; i++) {

                    ItemStack itemStack = chest.getContent().getItems().get(new Random().nextInt(chest.getContent().getItems().size()));
                    while (event.getInventory().contains(itemStack)) {
                        itemStack = chest.getContent().getItems().get(new Random().nextInt(chest.getContent().getItems().size()));
                    }

                    int index = new Random().nextInt(inventory.getSize());
                    while (inventory.getItem(index) != null) {
                        index = new Random().nextInt(inventory.getSize());
                    }

                    inventory.setItem(index, itemStack);
                }

                GameChest.getLoadedChests().add((Chest) holder);
            }
        }
    }

}
