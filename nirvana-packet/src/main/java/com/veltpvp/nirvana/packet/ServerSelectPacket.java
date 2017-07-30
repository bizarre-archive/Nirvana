package com.veltpvp.nirvana.packet;

import com.veltpvp.nirvana.packet.server.NirvanaServer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
import lombok.Getter;
import us.ikari.phoenix.network.redis.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerSelectPacket extends Packet {

    @Getter private String type;

    public ServerSelectPacket() {
    }

    public ServerSelectPacket(String type) {
        this.type = type;
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(type);
    }

    public void decode(DataInputStream in) throws IOException {
        this.type = in.readUTF();
    }
}
