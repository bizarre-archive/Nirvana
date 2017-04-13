package us.ikari.nirvana.game;

import us.ikari.nirvana.Nirvana;

public class GameLoader {

    private Nirvana main;

    public GameLoader(Nirvana main) {
        this.main = main;
    }

    public Game getGame() {
        return new Game();
    }

}
