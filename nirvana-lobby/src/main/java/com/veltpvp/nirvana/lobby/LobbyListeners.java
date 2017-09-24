package com.veltpvp.nirvana.lobby;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.gamemode.Gamemode;
import com.veltpvp.nirvana.lobby.profile.LobbyProfile;
import com.veltpvp.nirvana.menu.GameMenu;
import com.veltpvp.nirvana.parties.event.PartyCreateEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import us.ikari.phoenix.npc.NPC;
import us.ikari.phoenix.npc.event.PlayerInteractNPCEvent;
import us.ikari.phoenix.scoreboard.scoreboard.Board;
import us.ikari.phoenix.scoreboard.scoreboard.cooldown.BoardCooldown;
import us.ikari.phoenix.scoreboard.scoreboard.cooldown.BoardFormat;

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
            LobbyProfile profile = LobbyProfile.getByPlayer((Player) event.getEntity());

            if (profile != null && profile.getParkour() != null && profile.getParkour().getCheckpoint() != null) {
                Location location = profile.getParkour().getCheckpoint().getLocation().clone();

                location.setDirection(event.getEntity().getLocation().getDirection());

                event.getEntity().teleport(location);
            } else {
                event.getEntity().teleport(main.getLobby().getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        LobbyProfile profile = LobbyProfile.getByPlayer(player);
        ItemStack itemStack = event.getItem();

        if (profile != null) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);

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

                    /*if (itemStack.equals(LobbyItems.LOBBY_SELECTOR)) {
                        player.openInventory(new LobbyMenu(player, main.getLobbies()).getInventory());
                        return;
                    }*/

                    if (itemStack.equals(LobbyItems.PARTY_CREATOR)) {
                        player.performCommand("party create");
                        return;
                    }

                    if (itemStack.equals(LobbyItems.PARTY_DISBANDER)) {
                        player.performCommand("party disband");
                        return;
                    }

                    if (itemStack.equals(LobbyItems.INFORMATION_BOOK)) {
                        player.openInventory(new GameMenu(player).getInventory());
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
                main.getLobby().queue(player, gamemode);
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

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata("HydrogenPrefix")) {
            String prefix = player.getMetadata("HydrogenPrefix").get(0).asString();
            event.setFormat(prefix + "%s: %s");
        } else {
            event.setFormat("%s: %s");
        }
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Animals) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPartyCreateEvent(PartyCreateEvent event) {
        Player player = event.getLeader();

        LobbyProfile profile = LobbyProfile.getByPlayer(player);

        if (profile != null) {
            profile.getMembers().put(player.getUniqueId(), player.getName());
            player.getInventory().clear();
            player.getInventory().setHeldItemSlot(0);

            profile.setLeader(true);

            player.getInventory().setItem(0, LobbyItems.INFORMATION_BOOK);
            player.getInventory().setItem(8, LobbyItems.PARTY_DISBANDER);
            player.updateInventory();
        }

    }

}
