package com.veltpvp.nirvana.bukkit.game.chest.content;

import com.veltpvp.nirvana.bukkit.Nirvana;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.lang.file.AbstractConfigurationFile;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableGameChestContent implements GameChestContent {

    private static Nirvana main = Nirvana.getInstance();

    private final List<ItemStack> items;

    public ConfigurableGameChestContent() {
        this.items = new ArrayList<>();
    }

    private List<ItemStack> setItems(AbstractConfigurationFile config, String path) {
        List<ItemStack> toReturn = new ArrayList<>();

        this.items.clear();
        this.items.addAll(toReturn);

        return toReturn;
    }

    @Override
    public List<ItemStack> getItems() {
        return items;
    }
}
