package com.veltpvp.nirvana.game.chest;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.game.chest.content.ClassicGameChestContent;
import com.veltpvp.nirvana.game.chest.content.GameChestContent;
import com.veltpvp.nirvana.game.chest.content.PotPvPGameChestContent;
import com.veltpvp.nirvana.game.chest.content.UHCGameChestContent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import java.util.*;

public enum GameChest {
    POTPVP(new PotPvPGameChestContent(), "PotPvP"),
    CLASSIC(new ClassicGameChestContent(), "Classic"),
    UHC(new UHCGameChestContent(), "UHC");

    private static Set<Chest> loadedChests = new HashSet<>();

    @Getter private final GameChestContent content;
    @Getter private final String name;
    @Getter @Setter private List<Map.Entry<Location, GameChestTier>> instances;

    GameChest(GameChestContent content, String name) {
        this.content = content;
        this.name = name;
        this.instances = Collections.synchronizedList(new ArrayList<>());
    }

    public static GameChest getCurrent() {
        GameChest chest;

        try {
            chest = GameChest.valueOf(Nirvana.getInstance().getLocalNirvanaServer().getType().name());
        } catch (Exception exception) {
            chest = POTPVP;
        }

        return chest;
    }

    public static Map.Entry<GameChest, GameChestTier> getByBlock(Block block) {

        if (block.getState() instanceof Chest) {
            GameChest chest;
            try {
                chest = GameChest.valueOf(Nirvana.getInstance().getLocalNirvanaServer().getType().name());
            } catch (Exception ex) {
                chest = POTPVP;
            }

            return getPairByLocation(chest, block.getLocation());
        }


        return null;
    }

    private static Map.Entry<GameChest, GameChestTier> getPairByLocation(GameChest chest, Location location) {
        if (chest.getInstances().isEmpty()) {
            return new AbstractMap.SimpleEntry<>(GameChest.POTPVP, GameChestTier.BASIC);
        }
        
        for (Map.Entry<Location, GameChestTier> pair : chest.getInstances()) {
            if (pair.getKey().distance(location) <= 1) {
                return new AbstractMap.SimpleEntry<>(chest, pair.getValue());
            }
        }
        return null;
    }

    public static Set<Chest> getLoadedChests() {
        return loadedChests;
    }

}
