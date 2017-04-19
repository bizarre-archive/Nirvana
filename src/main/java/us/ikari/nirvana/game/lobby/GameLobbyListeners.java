package us.ikari.nirvana.game.lobby;

import net.minecraft.server.v1_7_R4.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameUtils;
import us.ikari.nirvana.game.kit.menu.GameKitSelectionMenu;
import us.ikari.nirvana.game.player.GamePlayer;
import us.ikari.nirvana.game.task.GameStartTask;
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

        event.setJoinMessage(null);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(main.getLangFile().getString("LOBBY.JOIN", LanguageConfigurationFileLocale.EXPLICIT, player.getDisplayName(), game.getPlayers().size(), game.getLobby().getSpawnLocations().size()));
        }

        game.getLobby().prepare(player);

        GamePlayer gamePlayer = game.getByPlayer(player);
        if (gamePlayer != null) {
            for (Location location : game.getLobby().getSpawnLocations()) {
                if (game.getBySpawnLocation(location) == null) {
                    player.teleport(location);
                    gamePlayer.getData().spawnLocation(location);

                    Horse horse = player.getWorld().spawn(player.getLocation(), Horse.class);
                    horse.setPassenger(player);
                    horse.setAdult();
                    horse.setMaxHealth(2);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Entity entity = ((CraftEntity)horse).getHandle();
                            entity.setInvisible(true);
                            GameUtils.removeIntelligence(horse);
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
            if (item != null && item.equals(GameLobby.getKitSelector(LanguageConfigurationFileLocale.EXPLICIT))) { //TODO CHANGE
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
            online.sendMessage(main.getLangFile().getString("LOBBY.QUIT", LanguageConfigurationFileLocale.EXPLICIT, player.getDisplayName(), game.getPlayers().size() - 1, game.getLobby().getSpawnLocations().size()));
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
