package com.veltpvp.nirvana.gamemode;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.lobby.LobbyProfileQueue;
import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerQueuePacket;
import com.veltpvp.nirvana.util.LocationSerialization;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import us.ikari.phoenix.lang.file.type.BasicConfigurationFile;
import us.ikari.phoenix.network.packet.PacketDeliveryMethod;
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

    public void addToGame(LobbyProfile profile) {
        Player player = profile.getPlayer();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("sendToNirvanaGame");
        out.writeUTF(id);

        if (profile.getMembers().isEmpty()) {
            out.writeInt(1);
            out.writeUTF(player.getName());
        } else {
            int i = 0;
            for (String name : profile.getMembers().values()) {

                if (i == 0) {
                    if (!(name.equalsIgnoreCase(player.getName()))) {
                        player.sendMessage(ChatColor.RED + "You must be the leader in order to summon your party into a game.");
                        return;
                    } else {
                        out.writeInt(profile.getMembers().size());
                    }
                }

                out.writeUTF(name);
                i++;
            }
        }

        player.sendPluginMessage(main, "BungeeCord", out.toByteArray());

        if (!profile.getMembers().isEmpty()) {
            main.getNetwork().sendPacket(new ServerQueuePacket(id, new ArrayList<>(profile.getMembers().values())), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
        } else {
            profile.setQueue(new LobbyProfileQueue(name, System.currentTimeMillis()));
            player.sendMessage(ChatColor.YELLOW + "You've been added to the " + ChatColor.LIGHT_PURPLE + name + ChatColor.YELLOW + " SkyWars queue.");
        }
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
