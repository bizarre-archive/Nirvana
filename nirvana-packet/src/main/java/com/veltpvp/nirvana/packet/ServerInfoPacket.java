package com.veltpvp.nirvana.packet;

import com.veltpvp.nirvana.packet.server.NirvanaServer;
import com.veltpvp.nirvana.packet.server.NirvanaServerStatus;
import com.veltpvp.nirvana.packet.server.NirvanaServerType;
import lombok.Getter;
import us.ikari.phoenix.network.redis.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerInfoPacket extends Packet {

    @Getter private NirvanaServer server;

    public ServerInfoPacket() {
    }

    public ServerInfoPacket(NirvanaServer server) {
        this.server = server;
    }

    public ServerInfoPacket(NirvanaServerType type, String id, NirvanaServerStatus status) {
        this.server = new NirvanaServer(type, id);
        this.server.setStatus(status);
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(server.getType().name());
        out.writeUTF(server.getId());
        out.writeUTF(server.getStatus().name());
        out.writeInt(server.getPlayers());
        out.writeInt(server.getMaxPlayers());
    }

    public void decode(DataInputStream in) throws IOException {
        this.server = new NirvanaServer(NirvanaServerType.valueOf(in.readUTF()), in.readUTF());
        this.server.setStatus(NirvanaServerStatus.valueOf(in.readUTF()));
        this.server.setPlayers(in.readInt());
        this.server.setMaxPlayers(in.readInt());
    }
}
