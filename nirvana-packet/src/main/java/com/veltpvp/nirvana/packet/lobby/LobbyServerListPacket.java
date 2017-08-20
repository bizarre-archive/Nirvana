package com.veltpvp.nirvana.packet.lobby;

import lombok.Getter;
import us.ikari.phoenix.network.redis.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LobbyServerListPacket extends Packet {

    @Getter private List<LobbyServer> servers;

    public LobbyServerListPacket() {

    }

    public LobbyServerListPacket(List<LobbyServer> servers) {
        this.servers = servers;
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeInt(servers.size());
        for (LobbyServer server : servers) {
            out.writeUTF(server.getId());
            out.writeInt(server.getPlayers());
            out.writeInt(server.getMaxPlayers());
            out.writeLong(server.getUpdatedAt());
        }
    }

    public void decode(DataInputStream in) throws IOException {
        servers = new ArrayList<LobbyServer>();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            servers.add(new LobbyServer(in.readUTF(), in.readInt(), in.readInt(), in.readLong()));
        }
    }
}
