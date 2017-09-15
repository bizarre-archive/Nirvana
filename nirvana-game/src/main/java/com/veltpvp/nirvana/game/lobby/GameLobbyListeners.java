package com.veltpvp.nirvana.game.lobby;

import com.veltpvp.nirvana.Nirvana;
import com.veltpvp.nirvana.game.Game;
import com.veltpvp.nirvana.game.GameUtils;
import com.veltpvp.nirvana.game.kit.menu.GameKitSelectionMenu;
import com.veltpvp.nirvana.game.player.GamePlayer;
import com.veltpvp.nirvana.game.task.GameStartTask;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

public class GameLobbyListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GameLobbyListeners(Nirvana main, Game game) {
        this.main = main;
        this.game = game;
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GamePlayer gamePlayer = game.getByPlayer(player);
        if (gamePlayer != null) {

            event.setJoinMessage(null);

            if (!gamePlayer.getData().alive()) {
                return;
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(main.getLangFile().getString("LOBBY.JOIN", LanguageConfigurationFileLocale.ENGLISH, player.getDisplayName(), game.getPlayers().size(), game.getLobby().getSpawnLocations().size()));
            }

            game.getLobby().prepare(player);

            for (Location location : game.getLobby().getSpawnLocations()) {
                if (game.getBySpawnLocation(location) == null) {
                    player.teleport(location);
                    gamePlayer.getData().spawnLocation(location);

                    GameUtils.Freezing.freeze(player);

                    break;
                }
            }

        } else {
            //TODO: Throw into spectate mode??
        }
    }

    @EventHandler
    public void onPlayerInteractKitSelectorEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().name().contains("RIGHT")) {
            ItemStack item = event.getItem();
            if (item != null && item.equals(GameLobby.getKitSelector(LanguageConfigurationFileLocale.ENGLISH))) { //TODO CHANGE
                GamePlayer gamePlayer = game.getByPlayer(player);
                if (gamePlayer != null) {
                    player.openInventory(new GameKitSelectionMenu(player, gamePlayer).getInventory());
                }
            }
        }
    }

    @EventHandler
    public void onEntityDismountEvent(VehicleExitEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(null);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(main.getLangFile().getString("LOBBY.QUIT", LanguageConfigurationFileLocale.ENGLISH, player.getDisplayName(), game.getPlayers().size() - 1, game.getLobby().getSpawnLocations().size()));
        }

        if (Bukkit.getOnlinePlayers().size() - 1 == 0 && main.getLocalNirvanaServer().getType() != NirvanaServerType.PENDING) {
            main.getLocalNirvanaServer().setType(NirvanaServerType.PENDING);
            main.setNetworkStatus(main.getLocalNirvanaServer().getStatus());
        }

        org.bukkit.entity.Entity entity = player.getVehicle();
        if (entity != null) {
            player.leaveVehicle();
            entity.remove();
        }

        GamePlayer gamePlayer = game.getByPlayer(player);
        if (gamePlayer != null) { // don't save if they quit b4 gam start
            game.getPlayers().remove(gamePlayer);
        }

        BukkitRunnable task = game.getTask(GameStartTask.class);
        if (task != null && !game.hasEnoughPlayers()) {
            task.cancel();
            game.getActiveTasks().remove(task);
        }
    }

}
