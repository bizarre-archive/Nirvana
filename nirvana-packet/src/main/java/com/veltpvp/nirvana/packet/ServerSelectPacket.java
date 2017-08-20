package com.veltpvp.nirvana.packet;

import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import lombok.Getter;
import us.ikari.phoenix.network.redis.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerSelectPacket extends Packet {

    @Getter private NirvanaServerType type;
    @Getter private List<String> players;

    public ServerSelectPacket() {
    }

    public ServerSelectPacket(NirvanaServerType type, List<String> players) {
        this.type = type;
        this.players = players;
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(type.name());
        out.writeInt(players.size());

        for (String name : players) {
            out.writeUTF(name);
        }
    }

    public void decode(DataInputStream in) throws IOException {
        this.type = NirvanaServerType.valueOf(in.readUTF());
        this.players = new ArrayList<String>();
        int count = in.readInt();

        for (int i = 0; i < count; i++) {
            players.add(in.readUTF());
        }

    }
}
