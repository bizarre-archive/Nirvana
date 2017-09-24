package com.veltpvp.nirvana.packet.party;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyJoinPacket extends Packet {

    @Getter private PartyMember member;
    @Getter private PartyMember toJoin;
    @Getter private String server;

    public PartyJoinPacket(PartyMember member, PartyMember toJoin, String sever) {
        this.member = member;
        this.toJoin = toJoin;
        this.server = sever;
    }

    public PartyJoinPacket() {}

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(member.getName());
        out.writeUTF(member.getUuid().toString());
        out.writeUTF(toJoin.getName());
        out.writeUTF(toJoin.getUuid().toString());
        out.writeUTF(server);
    }

    public void decode(DataInputStream in) throws IOException {
        this.member = new PartyMember(in.readUTF(), UUID.fromString(in.readUTF()));
        this.toJoin = new PartyMember(in.readUTF(), UUID.fromString(in.readUTF()));
        this.server = in.readUTF();
    }

}
