package com.veltpvp.nirvana.packet;

import com.veltpvp.nirvana.packet.server.NirvanaServer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
import lombok.Getter;
import us.ikari.phoenix.network.redis.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerStatusPacket extends Packet {

    @Getter private NirvanaServer server;

    public ServerStatusPacket() {
    }

    public ServerStatusPacket(NirvanaServer server) {
        this.server = server;
    }

    public ServerStatusPacket(String type, String id, NirvanaServerStatus status) {
        this.server = new NirvanaServer(type, id);
        this.server.setStatus(status);
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(server.getType());
        out.writeUTF(server.getId());
        out.writeUTF(server.getStatus().name());
    }

    public void decode(DataInputStream in) throws IOException {
        this.server = new NirvanaServer(in.readUTF(), in.readUTF());
        this.server.setStatus(NirvanaServerStatus.valueOf(in.readUTF()));
    }
}
