package com.veltpvp.nirvana.bukkit.game.lobby;

import com.veltpvp.nirvana.bukkit.Nirvana;
import com.veltpvp.nirvana.bukkit.game.GameState;
import com.veltpvp.nirvana.bukkit.game.GameUtils;
import com.veltpvp.nirvana.bukkit.game.player.GamePlayer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
import net.minecraft.server.v1_7_R4.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import com.veltpvp.nirvana.bukkit.game.Game;
import com.veltpvp.nirvana.bukkit.game.kit.menu.GameKitSelectionMenu;
import com.veltpvp.nirvana.bukkit.game.task.GameStartTask;
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

            if (game.getState() == GameState.LOBBY && game.getLobby().getSpawnLocations().size() == game.getPlayers().size()) {
                main.setNetworkStatus(NirvanaServerStatus.FULL);
            }

            game.getLobby().prepare(player);

            for (Location location : game.getLobby().getSpawnLocations()) {
                if (game.getBySpawnLocation(location) == null) {
                    player.teleport(location);
                    gamePlayer.getData().spawnLocation(location);

                    GameUtils.MutedHorse horse = new GameUtils.MutedHorse(player.getWorld());
                    horse.setLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

                    horse.getBukkitEntity().setPassenger(player);

                    ((CraftWorld)player.getWorld()).getHandle().addEntity(horse);
                    ((Horse)horse.getBukkitEntity()).setAdult();
                    ((Horse)horse.getBukkitEntity()).setMaxHealth(2);


                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Entity entity = horse.getBukkitEntity().getHandle();
                            entity.setInvisible(true);
                            GameUtils.removeIntelligence((LivingEntity) horse.getBukkitEntity());
                        }
                    }.runTaskLater(main, 2L);

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

        if (main.getLocalNirvanaServer().getStatus() == NirvanaServerStatus.FULL) {
            main.setNetworkStatus(NirvanaServerStatus.WAITING_FOR_PLAYERS);
        }

        org.bukkit.entity.Entity entity = player.getVehicle();
        if (entity != null) {
            player.leaveVehicle();
            entity.remove();
        }

        GamePlayer gamePlayer = game.getByPlayer(player);
        if (gamePlayer != null) { // don't save if they quit b4 game start
            game.getPlayers().remove(gamePlayer);
        }

        BukkitRunnable task = game.getTask(GameStartTask.class);
        if (task != null && !game.hasEnoughPlayers()) {
            task.cancel();
            game.getActiveTasks().remove(task);
        }
    }

}
