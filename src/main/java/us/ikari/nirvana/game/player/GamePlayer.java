package us.ikari.nirvana.game.player;

import lombok.Getter;

import java.util.UUID;

public class GamePlayer {

    @Getter private final UUID uuid;
    @Getter private final String name;
    @Getter private final GamePlayerData data;

    public GamePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.data = new GamePlayerData();
    }

}
