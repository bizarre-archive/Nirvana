package com.veltpvp.nirvana.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.gamemode.Gamemode;
import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerQueuePacket;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.ikari.phoenix.network.packet.PacketDeliveryMethod;

import java.util.ArrayList;

public class Lobby {

    @Getter private final Location spawnLocation;

    public Lobby(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void setupPlayer(Player player, LobbyProfile profile) {
        player.teleport(spawnLocation);

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setWalkSpeed(0.4F);

        player.getInventory().clear();

        player.getInventory().setItem(0, LobbyItems.INFORMATION_BOOK);
        player.getInventory().setItem(1, profile.isHidePlayers() ? LobbyItems.TOGGLE_VISIBILITY_ON_ITEM : LobbyItems.TOGGLE_VISIBILITY_OFF_ITEM);
        player.getInventory().setItem(8, LobbyItems.PARTY_CREATOR);
        //player.getInventory().setItem(8, LobbyItems.LOBBY_SELECTOR);
    }

    public void queue(Player player, Gamemode gamemode) {
        LobbyProfile profile = LobbyProfile.getByPlayer(player);

        if (profile == null) {
            return;
        }

        if (profile.getQueue() != null) {
            player.sendMessage(ChatColor.RED + "You're already queueing for a game!");
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("sendToNirvanaGame");
        out.writeUTF(gamemode.getId());

        if (profile.getMembers().isEmpty()) {
            out.writeInt(1);
            out.writeUTF(player.getName());
        } else {
            System.out.println("nigga is in a party");
            if (!(profile.isLeader())) {
                player.sendMessage(ChatColor.RED + "You must be the leader in order to summon your party into a game.");
                return;
            }

            out.writeInt(profile.getMembers().size());
            System.out.println(profile.getMembers().size());
            for (String name : profile.getMembers().values()) {
                System.out.println(name);
                out.writeUTF(name);
            }
        }

        player.sendPluginMessage(Nirvana.getInstance(), "BungeeCord", out.toByteArray());

        if (!profile.getMembers().isEmpty()) {
            Nirvana.getInstance().getNetwork().sendPacket(new ServerQueuePacket(gamemode.getId(), new ArrayList<>(profile.getMembers().values())), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
        } else {
            profile.setQueue(new LobbyProfileQueue(gamemode.getName(), System.currentTimeMillis()));
            player.sendMessage(ChatColor.YELLOW + "You've been added to the " + ChatColor.LIGHT_PURPLE + gamemode.getName() + ChatColor.YELLOW + " SkyWars queue.");
        }
    }


}
