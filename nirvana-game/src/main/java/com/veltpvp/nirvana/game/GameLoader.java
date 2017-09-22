package com.veltpvp.nirvana.game;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.game.chest.GameChest;
import com.veltpvp.nirvana.game.chest.GameChestTier;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameLoader implements Listener {

    private Nirvana main;
    private final List<Location> spawnLocations;
    private String map;
    private int height;

    public GameLoader(Nirvana main, String map) {
        this.spawnLocations = new ArrayList<>();
        this.map = map;
        this.height = 256;

        Bukkit.getPluginManager().registerEvents(this, main);

        /*File folder = new File(main.getDataFolder() + File.separator + "schematics");
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
        }*/

        Location location = Bukkit.getWorlds().get(0).getSpawnLocation();

        /*File file;
        CuboidClipboard clipboard;
        try {
            file = files.get(new Random().nextInt(files.size()));
            System.out.println("Loading " + file.getPath() + "...");
            clipboard = CuboidClipboard.loadSchematic(file);
            System.out.println("Loaded " + file.getPath() + "!");
            map = FilenameUtils.removeExtension(file.getName());
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
        }*/

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
                            identifier = Integer.parseInt((sign.getLine(0) + sign.getLine(1) + sign.getLine(2) + sign.getLine(3)).replaceAll("[^0-9]", ""));
                        } catch (Exception ex) {
                            identifier = 0;
                        }

                        GameChest chest = GameChest.POTPVP;

                        if (main.getLocalNirvanaServer().getType() != null) {
                            try { chest = GameChest.valueOf(main.getLocalNirvanaServer().getType().name()); } catch (Exception ignored) {}
                        }

                       chest.getInstances().add(new AbstractMap.SimpleEntry<>(block.getLocation(), GameChestTier.getByIdentifier(identifier)));
                    }

                    sign.getBlock().setType(Material.AIR);
                }
            }

            for (BlockState state : chunk.getTileEntities()) {
                if (state instanceof Chest) {
                    Map.Entry<GameChest, GameChestTier> info = GameChest.getByBlock(state.getBlock());
                    if (info == null) {
                        GameChest chest = GameChest.POTPVP;

                        if (main.getLocalNirvanaServer().getType() != null) {
                            try { chest = GameChest.valueOf(main.getLocalNirvanaServer().getType().name()); } catch (Exception ignored) {}
                        }

                        chest.getInstances().add(new AbstractMap.SimpleEntry<>(state.getLocation(), GameChestTier.BASIC));
                    }
                }
            }
        }

        /*height = clipboard.getHeight() + location.getWorld().getSpawnLocation().getBlockY();

        System.out.println("Pasted " + file.getPath() + "!");*/

        this.main = main;
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlock().getLocation().getBlockY() >= height && height > 0) {
            event.setCancelled(true);
        }
    }

    public Game getGame() {
        return new Game(map, spawnLocations);
    }

}
