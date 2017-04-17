package us.ikari.nirvana.game;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.chest.GameChest;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameLoader {

    private Nirvana main;
    private final List<Location> spawnLocations;

    public GameLoader(Nirvana main) {
        this.spawnLocations = new ArrayList<>();

        File folder = new File(main.getDataFolder() + File.separator + "schematics");
        if (!folder.exists()) {
            folder.mkdir();
            Bukkit.getLogger().warning("SCHEMATIC FOLDER DOES NOT EXIST, CREATING NOW.");
        }

        List<File> files = Arrays.asList(folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".schematic");
            }
        }));

        if (files.isEmpty()) {
            Bukkit.getLogger().severe("NO SCHEMATICS FOUND, STOPPING SERVER.");
            Bukkit.getServer().shutdown();
            return;
        }

        Location location = new Location(Bukkit.getWorlds().get(0), 0, 70, 0);

        File file;
        CuboidClipboard clipboard;
        try {
            file = files.get(new Random().nextInt(files.size()));
            System.out.println("Loading " + file.getPath() + "...");
            clipboard = CuboidClipboard.loadSchematic(files.get(new Random().nextInt(files.size())));
            System.out.println("Loaded " + file.getPath() + "!");
        } catch (DataException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Pasting " + file.getPath() + "...");
        try {
            clipboard.paste(new EditSession(new BukkitWorld(location.getWorld()), Integer.MAX_VALUE), new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()), true);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
            return;
        }

        for (Chunk chunk : location.getWorld().getLoadedChunks()) {
            for (BlockState state : chunk.getTileEntities()) {
                if (state instanceof Sign) {
                    Sign sign = (Sign) state;
                    org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) sign.getData();

                    Block block = sign.getBlock().getRelative(materialSign.getAttachedFace());

                    if (block.getType() != Material.CHEST) {
                        Location spawnLocation = sign.getLocation();
                        spawnLocation.setDirection(new Location(spawnLocation.getWorld(), 0, spawnLocation.getBlockY(), 0).toVector().subtract(spawnLocation.toVector()));
                        spawnLocations.add(spawnLocation);
                    } else {
                        int identifier;
                        try {
                            identifier = Integer.parseInt(sign.getLine(0).replaceAll("[^0-9]", ""));
                        } catch (Exception ex) {
                            identifier = 0;
                        }

                       GameChest.getByIdentifier(identifier).getInstances().add((Chest) block.getState());
                    }

                    sign.getBlock().setType(Material.AIR);
                }
            }
        }

        System.out.println("Pasted " + file.getPath() + "!");

        this.main = main;
    }

    public Game getGame() {
        return new Game(spawnLocations);
    }

}
