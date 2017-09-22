package com.veltpvp.nirvana.packet.party;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyLeavePacket extends Packet {

    @Getter private PartyMember member;

    public PartyLeavePacket(PartyMember leader) {
        this.member = leader;
    }

    public PartyLeavePacket() {
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(member.getName());
        out.writeUTF(member.getUuid().toString());
    }

    public void decode(DataInputStream in) throws IOException {
        this.member = new PartyMember(in.readUTF(), UUID.fromString(in.readUTF()));
    }

}
