package com.veltpvp.nirvana.packet.party;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyKickPacket extends Packet {

    @Getter private PartyMember leader;
    @Getter private PartyMember toKick;
    @Getter private String server;

    public PartyKickPacket(PartyMember leader, PartyMember toKick, String sever) {
        this.leader = leader;
        this.toKick = toKick;
        this.server = sever;
    }

    public PartyKickPacket() {}

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(leader.getName());
        out.writeUTF(leader.getUuid().toString());
        out.writeUTF(toKick.getName());
        out.writeUTF(toKick.getUuid().toString());
        out.writeUTF(server);
    }

    public void decode(DataInputStream in) throws IOException {
        this.leader = new PartyMember(in.readUTF(), UUID.fromString(in.readUTF()));
        this.toKick = new PartyMember(in.readUTF(), UUID.fromString(in.readUTF()));
        this.server = in.readUTF();
    }

}
