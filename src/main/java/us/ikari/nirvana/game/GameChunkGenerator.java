package us.ikari.nirvana.game;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class GameChunkGenerator extends ChunkGenerator {

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 60, 0);
    }

    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        return new byte[32768];
    }
}
