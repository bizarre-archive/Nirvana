package com.veltpvp.nirvana.packet.party;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyInfoPacket extends Packet {

    @Getter private Party party;

    public PartyInfoPacket(Party party) {
        this.party = party;
    }

    public PartyInfoPacket() {
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(party.getUuid().toString());
        out.writeInt(party.getMembers().size());

        for (PartyMember member : party.getMembers()) {
            out.writeUTF(member.getName());
            out.writeUTF(member.getUuid().toString());
        }
    }

    public void decode(DataInputStream in) throws IOException {
        this.party = new Party(UUID.fromString(in.readUTF()));

        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            party.getMembers().add(new PartyMember(in.readUTF(), UUID.fromString(in.readUTF())));
        }
    }



}
