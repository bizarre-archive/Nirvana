package com.veltpvp.nirvana.packet.server;

import lombok.Getter;
import lombok.Setter;

public class NirvanaServer {

    @Getter @Setter private String id;
    @Getter @Setter private NirvanaServerStatus status;
    @Getter @Setter private NirvanaServerType type;
    @Getter @Setter private int maxPlayers;
    @Getter @Setter private int players;

    public NirvanaServer(String id) {
        this.id = id;
        this.status = NirvanaServerStatus.OFFLINE;
    }

    public NirvanaServer(NirvanaServerType type, String id) {
        this.type = type;
        this.id = id;
        this.status = NirvanaServerStatus.OFFLINE;
    }

}
