package com.veltpvp.nirvana.packet.lobby;

import com.veltpvp.nirvana.packet.server.NirvanaServer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
import lombok.Getter;
import us.ikari.phoenix.network.redis.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LobbyServerStatusPacket extends Packet {

    @Getter private LobbyServer server;

    public LobbyServerStatusPacket() {

    }

    public LobbyServerStatusPacket(LobbyServer server) {
        this.server = server;
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(server.getId());
        out.writeInt(server.getPlayers());
        out.writeInt(server.getMaxPlayers());
        out.writeLong(server.getUpdatedAt());
    }

    public void decode(DataInputStream in) throws IOException {
        this.server = new LobbyServer(in.readUTF(), in.readInt(), in.readInt(), in.readLong());
    }
}
