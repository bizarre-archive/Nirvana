package us.ikari.nirvana.game.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameState;
import us.ikari.nirvana.game.spectator.GameSpectator;
import us.ikari.nirvana.game.task.GameStartTask;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

public class GamePlayerListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GamePlayerListeners(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }


    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);
        if (gamePlayer == null) {
            if (game.getState() != GameState.LOBBY) {
                event.setKickMessage("Game has already started.");
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                return;
            }
            game.getPlayers().add(new GamePlayer(player.getUniqueId(), player.getName()));
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (game.shouldStart()) {
            game.getActiveTasks().add(new GameStartTask(game));
            game.getGameTime().reset();
        }

        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);
        if (gamePlayer != null && !gamePlayer.getData().alive()) {
            gamePlayer.getData().spectator(new GameSpectator(player));
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        GamePlayer gamePlayer = game.getByPlayer(event.getPlayer());

        if (gamePlayer != null) {
            gamePlayer.getData().spawnLocation(null);
            gamePlayer.getData().alive(false);
        }

        if (game.getState() != GameState.LOBBY) {
            event.setQuitMessage(null);
            if (gamePlayer != null && gamePlayer.getData().spectator() == null) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.sendMessage(main.getLangFile().getString("GAME.QUIT", LanguageConfigurationFileLocale.EXPLICIT, event.getPlayer().getDisplayName()));
                }
            }

            if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
                return;
            }

            event.getPlayer().setHealth(0);
        }
        //not going to remove because we want to save all participating players later
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null) {
            gamePlayer.getData().alive(false);

            Location location = player.getLocation();
            if (location.getBlockY() <= 0) {
                location = player.getWorld().getHighestBlockAt(player.getWorld().getSpawnLocation()).getLocation();
            }

            Location finalLocation = location;
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.spigot().respawn();
                    gamePlayer.getData().spectator(new GameSpectator(player));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(finalLocation);
                        }
                    }.runTaskLater(main, 4L);

                }
            }.runTaskLater(main, 1L);

            game.update();
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null && gamePlayer.getData().spectator() == null) {
            gamePlayer.getData().spectator(new GameSpectator(player));
        }
    }

}
