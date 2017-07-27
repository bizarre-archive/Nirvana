package com.veltpvp.nirvana.game.chest;

import com.veltpvp.nirvana.game.chest.content.BasicGameChestContent;
import com.veltpvp.nirvana.game.chest.content.BuffedGameChestContent;
import com.veltpvp.nirvana.game.chest.content.GameChestContent;
import com.veltpvp.nirvana.game.chest.content.OverPoweredGameChestContent;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum GameChest {
    BASIC(0, new BasicGameChestContent(), 4, 8),
    BUFFED(1, new BuffedGameChestContent(), 4, 8),
    OP(2, new OverPoweredGameChestContent(), 4, 8);

    private static Set<Chest> loadedChests = new HashSet<>();

    @Getter private final int identifier;
    @Getter private final GameChestContent content;
    @Getter private final List<Location> instances;
    @Getter private final int min, max;

    GameChest(int identifier, GameChestContent content, int min, int max) {
        this.identifier = identifier;
        this.content = content;
        this.min = min;
        this.max = max;
        this.instances = new ArrayList<>();
    }

    public static GameChest getByIdentifier(int identifier) {
        for (GameChest chest : values()) {
            if (chest.getIdentifier() == identifier) {
                return chest;
            }
        }
        return BASIC;
    }

    public static GameChest getByBlock(Block block) {
        if (block.getState() instanceof Chest) {
            for (GameChest chest : values()) {
                if (chest.getInstances().contains(block.getLocation()) && !loadedChests.contains(block.getState())) {
                    return chest;
                }
            }
        }
        return null;
    }

    public static Set<Chest> getLoadedChests() {
        return loadedChests;
    }

}
