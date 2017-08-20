package com.veltpvp.nirvana.packet;

import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import lombok.Getter;
import us.ikari.phoenix.network.redis.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerSetTypePacket extends Packet {

    @Getter private String id;
    @Getter private NirvanaServerType type;

    public ServerSetTypePacket() {
    }

    public ServerSetTypePacket(String id, NirvanaServerType type) {
        this.id = id;
        this.type = type;
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(type.name());
    }

    public void decode(DataInputStream in) throws IOException {
        this.id = in.readUTF();
        this.type = NirvanaServerType.valueOf(in.readUTF());
    }
}
