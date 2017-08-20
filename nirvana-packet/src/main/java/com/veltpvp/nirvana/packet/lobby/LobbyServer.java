package com.veltpvp.nirvana.packet.lobby;

import lombok.Getter;
import lombok.Setter;

public class LobbyServer {

    @Getter private String id;
    @Getter @Setter private int players, maxPlayers;
    @Getter @Setter private long updatedAt;

    public LobbyServer(String id, int players, int maxPlayers, long updatedAt) {
        this.id = id;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.updatedAt = updatedAt;
    }

}
