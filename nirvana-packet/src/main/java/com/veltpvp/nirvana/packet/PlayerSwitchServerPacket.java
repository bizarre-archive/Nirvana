package com.veltpvp.nirvana.packet;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerSwitchServerPacket extends Packet {

    @Getter private String name;
    @Getter private UUID playerUuid;
    @Getter private String server;

    public PlayerSwitchServerPacket(String name, UUID uuid, String server) {
        this.name = name;
        this.playerUuid = uuid;
        this.server = server;
    }

    public PlayerSwitchServerPacket() {
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(playerUuid.toString());
        out.writeUTF(server);
    }

    public void decode(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.playerUuid = UUID.fromString(in.readUTF());
        this.server = in.readUTF();
    }
}
