package com.veltpvp.nirvana.gamemode;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.util.LocationSerialization;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import us.ikari.phoenix.lang.file.type.BasicConfigurationFile;
import us.ikari.phoenix.npc.NPC;

import java.util.ArrayList;
import java.util.List;

public class Gamemode {

    private static Nirvana main = Nirvana.getInstance();
    private static List<Gamemode> gamemodes = new ArrayList<>();

    @Getter private final String id;
    @Getter private final String name;
    @Getter @Setter private Location npcLocation;
    @Getter @Setter private BlockFace face;

    public Gamemode(String id, String name) {
        this.id = id;
        this.name = name;

        System.out.println("LOADED GAME MODE '" + id + "'!");

        gamemodes.add(this);
    }

    public Location getExactNPCLocation() {
        Location location = npcLocation;
        if (location != null) {
            return location.clone().add(0.5, -0.5, 0.5);
        }

        return null;
    }

    public static Gamemode getById(String id) {
        for (Gamemode gamemode : gamemodes) {
            if (gamemode.getId().equalsIgnoreCase(id)) {
                return gamemode;
            }
        }
        return null;
    }

    public static Gamemode getByName(String name) {
        for (Gamemode gamemode : gamemodes) {
            if (gamemode.getName().equalsIgnoreCase(name)) {
                return gamemode;
            }
        }
        return null;
    }

    public static Gamemode getByNPC(NPC npc) {
        for (Gamemode gamemode : gamemodes) {
            if (gamemode.getExactNPCLocation() != null) {
                if (gamemode.getExactNPCLocation().equals(npc.getEntityPlayer().getBukkitEntity().getLocation())) {
                    return gamemode;
                }
            }
        }

        return null;
    }

    public static void load() {
        BasicConfigurationFile config = main.getMainConfig();

        for (String key : config.getConfiguration().getConfigurationSection("GAMEMODES").getKeys(false)) {
            new Gamemode(key, config.getString("GAMEMODES." + key + ".NAME"));
        }
    }

    public static void loadLocations() {
        BasicConfigurationFile config = main.getMainConfig();
        for (Gamemode gamemode : gamemodes) {
            if (gamemode.getNpcLocation() == null) {
                if (config.getConfiguration().contains("GAMEMODES." + gamemode.getId() + ".NPC_LOCATION")) {
                    gamemode.setNpcLocation(LocationSerialization.deserializeLocation(config.getString("GAMEMODES." + gamemode.getId() + ".NPC_LOCATION")));
                }
            }
        }
    }

    public static List<Gamemode> getGamemodes() {
        return gamemodes;
    }
}
