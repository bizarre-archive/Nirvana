package us.ikari.nirvana.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;
import us.ikari.nirvana.game.lobby.GameLobby;
import us.ikari.nirvana.game.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class Game {

    @Getter private final GameLobby lobby;
    @Getter private final List<GamePlayer> players;
    @Getter private final List<BukkitRunnable> activeTasks;
    @Getter @Setter private GameState state;

    public Game() {
        this.lobby = new GameLobby();
        this.players = new ArrayList<>();
        this.activeTasks = new ArrayList<>();
        this.state = GameState.LOBBY;
    }

}
