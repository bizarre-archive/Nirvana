package com.veltpvp.nirvana.packet.server;

import lombok.Getter;
import lombok.Setter;

public class NirvanaServer {

    @Getter @Setter private String type;
    @Getter @Setter private String id;
    @Getter @Setter private NirvanaServerStatus status;

    public NirvanaServer(String id) {
        this.id = id;
        this.status = NirvanaServerStatus.OFFLINE;
    }

    public NirvanaServer(String type, String id) {
        this.type = type;
        this.id = id;
        this.status = NirvanaServerStatus.OFFLINE;
    }

}
