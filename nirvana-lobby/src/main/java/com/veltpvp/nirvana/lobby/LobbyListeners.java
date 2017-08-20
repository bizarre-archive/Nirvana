package com.veltpvp.nirvana.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.gamemode.Gamemode;
import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import com.veltpvp.nirvana.packet.NirvanaChannels;
import com.veltpvp.nirvana.packet.ServerQueuePacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.network.redis.packet.PacketDeliveryMethod;
import us.ikari.phoenix.npc.NPC;
import us.ikari.phoenix.npc.event.PlayerInteractNPCEvent;
import us.ikari.phoenix.scoreboard.scoreboard.Board;
import us.ikari.phoenix.scoreboard.scoreboard.cooldown.BoardCooldown;
import us.ikari.phoenix.scoreboard.scoreboard.cooldown.BoardFormat;
import us.ikari.phonix.util.time.Cooldown;

import java.util.ArrayList;
import java.util.List;

public class LobbyListeners implements Listener {

    private static Nirvana main = Nirvana.getInstance();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        List<NPC> npcs = new ArrayList<>(NPC.getNPCs());
        for (NPC npc : npcs) {
            if (npc.getEntityPlayer().getUniqueID().equals(event.getPlayer().getUniqueId())) {
                npc.despawn();
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        event.setCancelled(true);

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID && event.getEntity() instanceof Player) {
            event.getEntity().teleport(main.getLobby().getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        LobbyProfile profile = LobbyProfile.getByPlayer(player);
        ItemStack itemStack = event.getItem();

        if (profile != null) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (itemStack != null) {

                    if (itemStack.equals(LobbyItems.TOGGLE_VISIBILITY_OFF_ITEM)) {

                        Board board = Board.getByPlayer(player);
                        if (board != null) {
                            BoardCooldown cooldown = board.getCooldown("visibility");

                            if (cooldown != null) {
                                player.sendMessage(ChatColor.YELLOW + "You must wait " + ChatColor.RED + cooldown.getFormattedString(BoardFormat.SECONDS) + "s" + ChatColor.YELLOW + " before toggling visibility again.");
                                return;
                            }

                            new BoardCooldown(board, "visibility", 5);
                            player.setItemInHand(LobbyItems.TOGGLE_VISIBILITY_ON_ITEM);
                            profile.setHidePlayers(!profile.isHidePlayers());
                            return;
                        }
                    }

                    if (itemStack.equals(LobbyItems.TOGGLE_VISIBILITY_ON_ITEM)) {
                        Board board = Board.getByPlayer(player);
                        if (board != null) {
                            BoardCooldown cooldown = board.getCooldown("visibility");

                            if (cooldown != null) {
                                player.sendMessage(ChatColor.YELLOW + "You must wait " + ChatColor.RED + cooldown.getFormattedString(BoardFormat.SECONDS) + "s" + ChatColor.YELLOW + " before toggling visibility again.");
                                return;
                            }

                            new BoardCooldown(board, "visibility", 5);
                            player.setItemInHand(LobbyItems.TOGGLE_VISIBILITY_OFF_ITEM);
                            profile.setHidePlayers(!profile.isHidePlayers());
                            return;
                        }
                        return;
                    }

                    if (itemStack.equals(LobbyItems.LOBBY_SELECTOR)) {
                        player.openInventory(new LobbyMenu(player, main.getLobbies()).getInventory());
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractNPCEvent(PlayerInteractNPCEvent event) {
        Player player = event.getPlayer();
        LobbyProfile profile = LobbyProfile.getByPlayer(player);

        if (profile != null) {
            Gamemode gamemode = Gamemode.getByNPC(event.getNpc());

            if (gamemode != null) {
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
                    main.getNetwork().sendPacket(new ServerQueuePacket(gamemode.getId(), new ArrayList<>(profile.getMembers().values())), NirvanaChannels.SLAVE_CHANNEL, PacketDeliveryMethod.CLUSTER);
                } else {
                    profile.setQueue(new LobbyProfileQueue(gamemode.getName(), System.currentTimeMillis()));
                    player.sendMessage(ChatColor.YELLOW + "You've been added to the " + ChatColor.LIGHT_PURPLE + gamemode.getName() + ChatColor.YELLOW + " SkyWars queue.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!(player.isOp())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlacekEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!(player.isOp())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

}
