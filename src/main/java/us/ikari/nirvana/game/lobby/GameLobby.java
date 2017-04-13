package us.ikari.nirvana.game.lobby;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;

public class GameLobby {

    private static final World DEFAULT_WORLD = Bukkit.getWorlds().get(0);

    @Getter private final List<Location> spawnLocations;

    public GameLobby() {
        spawnLocations = defineSpawnLocations();
    }

    private List<Location> defineSpawnLocations() {
        List<Location> toReturn = new ArrayList<>();

        for (Chunk chunk : DEFAULT_WORLD.getLoadedChunks()) {
            for (BlockState blockState : chunk.getTileEntities()) {
                if (blockState instanceof Sign) {
                    if (((Sign) blockState).getLine(0).startsWith("SPAWN")) {
                        toReturn.add(blockState.getLocation());
                    }
                }
            }
        }

        return toReturn;
    }

}
