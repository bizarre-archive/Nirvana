package us.ikari.nirvana.game.player;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameState;
import us.ikari.nirvana.game.spectator.GameSpectator;
import us.ikari.nirvana.game.task.GameStartTask;
import us.ikari.phoenix.lang.file.type.language.LanguageConfigurationFileLocale;

import java.text.DecimalFormat;

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

            gamePlayer = new GamePlayer(player.getUniqueId(), player.getName());
            if (game.getPlayers().size() >= game.getLobby().getSpawnLocations().size()) {
                gamePlayer.getData().alive(false);
            }

            game.getPlayers().add(gamePlayer);
        }
    }

    @EventHandler
    public void onPlayerDeathMessageEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Player killer = player.getKiller();
        if (killer != null) {

            GamePlayer gamePlayer = game.getByPlayer(killer);
            if (gamePlayer != null && gamePlayer.getData().alive()) {
                player.sendMessage(main.getLangFile().getString("GAME.HEALTH", LanguageConfigurationFileLocale.EXPLICIT, killer.getDisplayName(), new DecimalFormat("##.0").format(killer.getHealth() / 2)));
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(main.getLangFile().getString("DEATH_MESSAGE.PLAYER", LanguageConfigurationFileLocale.EXPLICIT, player.getDisplayName(), killer.getDisplayName()));
            }

        } else {
            EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();

            for (Player online : Bukkit.getOnlinePlayers()) {
                String message;
                if (cause == null) {
                    message = main.getLangFile().getString("DEATH_MESSAGE.CUSTOM", LanguageConfigurationFileLocale.EXPLICIT, player.getDisplayName());
                } else {
                    message = main.getLangFile().getString("DEATH_MESSAGE." + cause.name().toUpperCase(), LanguageConfigurationFileLocale.EXPLICIT, player.getDisplayName());
                    if (message == null) {
                        message = main.getLangFile().getString("DEATH_MESSAGE.CUSTOM", LanguageConfigurationFileLocale.EXPLICIT, player.getDisplayName());
                    }
                }

                online.sendMessage(message);
            }

        }

        event.setDeathMessage(null);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (game.shouldStart()) {
            game.getActiveTasks().add(new GameStartTask(game));
            game.getGameTime().reset();
        }

        event.setJoinMessage(null);

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

        event.setQuitMessage(null);

        if (game.getState() != GameState.LOBBY) {
            event.setQuitMessage(null);

            if (gamePlayer != null && gamePlayer.getData().spectator() == null && game.getState() != GameState.END) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.sendMessage(main.getLangFile().getString("GAME.QUIT", LanguageConfigurationFileLocale.EXPLICIT, event.getPlayer().getDisplayName()));
                }
            }

            if (gamePlayer != null && gamePlayer.getData().spectator() != null) {
                return;
            }

            if (game.getState() != GameState.END) {
                event.getPlayer().setHealth(0);
            }
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEventScoreboard(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            return;
        }

        Objective objective = scoreboard.registerNewObjective("health", "health");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(ChatColor.DARK_RED + "\u2764");
    }

}
