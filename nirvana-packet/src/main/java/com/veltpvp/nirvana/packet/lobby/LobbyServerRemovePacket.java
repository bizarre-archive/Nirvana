package com.veltpvp.nirvana.packet.lobby;

import lombok.Getter;
import us.ikari.phoenix.network.redis.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LobbyServerRemovePacket extends Packet {

    @Getter private String id;

    public LobbyServerRemovePacket() {

    }

    public LobbyServerRemovePacket(String id) {
        this.id = id;
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(id);
    }

    public void decode(DataInputStream in) throws IOException {
        this.id = in.readUTF();
    }
}
