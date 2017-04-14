package us.ikari.nirvana.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.task.GameStartTask;

public class GameListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GameListeners(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        int size = game.getPlayers().size();

        if (size >= (game.getLobby().getSpawnLocations().size() / 2)) {
            game.getActiveTasks().add(new GameStartTask(game));
        }


    }

}
