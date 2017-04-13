package us.ikari.nirvana.game;

import lombok.Getter;
import lombok.Setter;
import us.ikari.nirvana.game.lobby.GameLobby;
import us.ikari.nirvana.game.player.GamePlayer;
import us.ikari.nirvana.game.state.GameState;

import java.util.ArrayList;
import java.util.List;

public class Game {

    @Getter private final GameLobby lobby;
    @Getter private final List<GamePlayer> players;
    @Getter @Setter private GameState state;

    public Game() {
        this.lobby = new GameLobby();
        this.players = new ArrayList<>();
        this.state = GameState.WAITING_FOR_PLAYERS;
    }

}
