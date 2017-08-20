package com.veltpvp.nirvana.lobby;

import lombok.Getter;

public class LobbyProfileQueue {

    @Getter private final String name;
    @Getter private final long init;

    public LobbyProfileQueue(String name, long init) {
        this.name = name;
        this.init = init;
    }

}
