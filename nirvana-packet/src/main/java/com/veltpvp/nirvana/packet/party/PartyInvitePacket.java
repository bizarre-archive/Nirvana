package com.veltpvp.nirvana.packet.party;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyInvitePacket extends Packet {

    @Getter private PartyMember leader;
    @Getter private PartyMember toInvite;
    @Getter private String server;

    public PartyInvitePacket(PartyMember leader, PartyMember toInvite, String sever) {
        this.leader = leader;
        this.toInvite = toInvite;
        this.server = sever;
    }

    public PartyInvitePacket() {}

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(leader.getName());
        out.writeUTF(leader.getUuid().toString());
        out.writeUTF(toInvite.getName());
        out.writeUTF(toInvite.getUuid().toString());
        out.writeUTF(server);
    }

    public void decode(DataInputStream in) throws IOException {
        this.leader = new PartyMember(in.readUTF(), UUID.fromString(in.readUTF()));
        this.toInvite = new PartyMember(in.readUTF(), UUID.fromString(in.readUTF()));
        this.server = in.readUTF();
    }

}
