package us.ikari.nirvana.game;

import org.bukkit.event.Listener;
import us.ikari.nirvana.Nirvana;

public class GameListeners implements Listener {

    private Nirvana main;
    private Game game;

    public GameListeners(Nirvana main) {
        this.main = main;
        this.game = main.getGame();
    }

}
