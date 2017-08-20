package com.veltpvp.nirvana.lobby.profile;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.gamemode.Gamemode;
import net.minecraft.server.v1_7_R4.PacketPlayOutPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.phoenix.hologram.Hologram;
import us.ikari.phoenix.npc.NPC;

import java.util.UUID;

public class LobbyProfileListeners implements Listener {

    private static Nirvana main = Nirvana.getInstance();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LobbyProfile profile = LobbyProfile.getByPlayer(player);

        if (profile == null) {
            profile = new LobbyProfile(player.getUniqueId(), player.getName());
            profile.setHidePlayers(profile.isHidePlayers());

            main.getLobby().setupPlayer(player, profile);

            for (Gamemode gamemode : Gamemode.getGamemodes()) {
                if (gamemode.getNpcLocation() != null) {
                    Location npc = gamemode.getNpcLocation();

                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            NPC nigga;
                            if (((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() >= 47) {
                                nigga = new NPC(player.getUniqueId(), " ").spawn(new Location(npc.getWorld(), npc.getBlockX() + 0.5, npc.getBlockY() - 0.5, npc.getBlockZ() + 0.5, npc.getYaw(), npc.getPitch()), player).hideName();
                            } else {
                                nigga = new NPC(player.getUniqueId(), player.getName()).spawn(new Location(npc.getWorld(), npc.getBlockX() + 0.5, npc.getBlockY() - 0.5, npc.getBlockZ() + 0.5, npc.getYaw(), npc.getPitch()), player).hideName();
                            }

                            new Hologram(nigga.getEntityPlayer().getBukkitEntity().getLocation().clone().add(0, 2, 0)).line("&d&lVeltPvP " + gamemode.getName() + " SkyWars").line("&7Click to join the queue").show(player);
                        }
                    }.runTaskLater(main, 2L);
                }
            }
        }

        for (LobbyProfile other : LobbyProfile.getProfiles().values()) {
            if (other.isHidePlayers()) {
                other.setHidePlayers(other.isHidePlayers());
            }
        }

    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LobbyProfile profile = LobbyProfile.getByPlayer(player);

        if (profile != null) {
            LobbyProfile.getProfiles().remove(profile.getUniqueId());
        }

    }

}
