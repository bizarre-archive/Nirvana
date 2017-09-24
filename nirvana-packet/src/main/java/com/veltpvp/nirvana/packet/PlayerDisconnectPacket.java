package com.veltpvp.nirvana.packet;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerDisconnectPacket extends Packet {

    @Getter private String name;
    @Getter private UUID playerUuid;

    public PlayerDisconnectPacket(String name, UUID uuid) {
        this.name = name;
        this.playerUuid = uuid;
    }

    public PlayerDisconnectPacket() {
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(playerUuid.toString());
    }

    public void decode(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.playerUuid = UUID.fromString(in.readUTF());
    }
}
