package us.ikari.nirvana.game.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;

public class GamePlayerListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GamePlayerListeners(Nirvana main) {
        this.main =main;
        this.game = main.getGame();
    }


    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        game.getPlayers().add(new GamePlayer(player.getUniqueId(), player.getName()));
    }

}
