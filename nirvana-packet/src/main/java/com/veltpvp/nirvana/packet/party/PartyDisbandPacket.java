package com.veltpvp.nirvana.packet.party;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyDisbandPacket extends Packet {

    @Getter private PartyMember leader;

    public PartyDisbandPacket(PartyMember leader) {
        this.leader = leader;
    }

    public PartyDisbandPacket() {
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(leader.getName());
        out.writeUTF(leader.getUuid().toString());
    }

    public void decode(DataInputStream in) throws IOException {
        this.leader = new PartyMember(in.readUTF(), UUID.fromString(in.readUTF()));
    }

}
