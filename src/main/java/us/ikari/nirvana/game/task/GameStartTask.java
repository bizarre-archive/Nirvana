package us.ikari.nirvana.game.task;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.nirvana.Nirvana;
import us.ikari.nirvana.game.Game;

public class GameStartTask extends BukkitRunnable {

    private static final long DEFAULT_DURATION = 30000;

    @Getter private final Game game;
    @Getter private long duration;
    @Getter private long time;

    public GameStartTask(Game game, long duration) {
        this.game = game;
        this.duration = duration;
        this.time = System.currentTimeMillis();

        runTask(Nirvana.getInstance());
    }

    public GameStartTask(Game game) {
        this(game, DEFAULT_DURATION);
    }

    @Override
    public void run() {

    }

}
