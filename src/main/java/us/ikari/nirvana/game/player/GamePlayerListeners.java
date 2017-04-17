package us.ikari.nirvana.game.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;
import us.ikari.nirvana.game.GameState;
import us.ikari.nirvana.game.task.GameStartTask;

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

        game.getPlayers().add(new GamePlayer(player.getUniqueId(), player.getName()));
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (game.shouldStart()) {
            game.getActiveTasks().add(new GameStartTask(game));
            game.getGameTime().reset();
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
        }

        //not going to remove because we want to save all participating players later
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GamePlayer gamePlayer = game.getByPlayer(player);

        if (gamePlayer != null) {
            gamePlayer.getData().alive(false);
        }

    }
}
