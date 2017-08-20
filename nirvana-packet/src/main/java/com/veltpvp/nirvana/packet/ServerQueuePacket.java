package com.veltpvp.nirvana.packet;

import lombok.Getter;
import us.ikari.phoenix.network.redis.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerQueuePacket extends Packet {

    @Getter private String type;
    @Getter private List<String> players;

    public ServerQueuePacket() {
    }

    public ServerQueuePacket(String type, List<String> players) {
        this.type = type;
        this.players = players;
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(type);
        out.writeInt(players.size());

        for (String player : players) {
            out.writeUTF(player);
        }
    }

    public void decode(DataInputStream in) throws IOException {
        this.type = in.readUTF();
        this.players = new ArrayList<String>();

        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            players.add(in.readUTF());
        }
    }
}
