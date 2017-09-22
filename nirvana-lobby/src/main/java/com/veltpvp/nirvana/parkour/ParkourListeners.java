package com.veltpvp.nirvana.parkour;

import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ParkourListeners implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        LobbyProfile profile = LobbyProfile.getByPlayer(player);
        if (profile == null) {
            return;
        }

        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();

            if (block.getType() == Material.IRON_PLATE) {
                event.setCancelled(true);
                if (block.getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK) {
                    if (Parkour.getStartingCheckpoint() == null) {
                        Parkour.setStartingCheckpoint(new Parkour.Checkpoint(block.getLocation()));
                    }

                    if (profile.getParkour() == null) {
                        Parkour parkour = new Parkour();

                        parkour.getCheckpoint().ring(player);

                        profile.setParkour(parkour);
                        player.sendMessage(ChatColor.YELLOW + "You've started the " + ChatColor.GREEN + "parkour" + ChatColor.YELLOW + " challenge!");
                    }

                } else if (block.getRelative(BlockFace.DOWN).getType() == Material.REDSTONE_BLOCK) {
                    if (profile.getParkour() != null) {
                        player.sendMessage(ChatColor.YELLOW + "You've finished the " + ChatColor.GREEN + "parkour" + ChatColor.YELLOW + " challenge!");
                        profile.getParkour().getCheckpoint().ring(player);
                        profile.setParkour(null);
                    }
                }
            } else if (block.getType() == Material.GOLD_PLATE) {
                event.setCancelled(true);
                Parkour parkour = profile.getParkour();
                if (parkour != null) {
                    Parkour.Checkpoint checkpoint = parkour.getCheckpoint();
                    if (!checkpoint.getLocation().equals(block.getLocation())) {
                        parkour.setCheckpoint(new Parkour.Checkpoint(block.getLocation()));
                        parkour.getCheckpoint().ring(player);
                        player.sendMessage(ChatColor.YELLOW + "You've reached a new" + ChatColor.GOLD + " checkpoint" + ChatColor.YELLOW + "!");
                    }
                }
            }

        }

    }

}
