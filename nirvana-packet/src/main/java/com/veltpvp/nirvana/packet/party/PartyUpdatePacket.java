package com.veltpvp.nirvana.packet.party;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PartyUpdatePacket extends Packet {

    @Getter private Party party;
    @Getter private PartyUpdateType type;
    @Getter private PartyMember optional;

    public PartyUpdatePacket(Party party, PartyUpdateType type, PartyMember optional) {
        this.party = party;
        this.type = type;
        this.optional = optional;
    }

    public PartyUpdatePacket(Party party, PartyUpdateType type) {
        this(party, type, null);
    }

    public PartyUpdatePacket() {
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(type.name());
        out.writeUTF(optional == null ? "" : optional.getName());
        out.writeUTF(optional == null ? "" : optional.getUuid().toString());
        out.writeUTF(party.getUuid().toString());
        out.writeInt(party.getMembers().size());

        for (PartyMember member : party.getMembers()) {
            out.writeUTF(member.getName());
            out.writeUTF(member.getUuid().toString());
        }
    }

    public void decode(DataInputStream in) throws IOException {
        this.type = PartyUpdateType.valueOf(in.readUTF());

        this.optional = new PartyMember(in.readUTF(), UUID.fromString(in.readUTF()));

        if (this.optional.getName().length() == 0) {
            this.optional = null;
        }

        this.party = new Party(UUID.fromString(in.readUTF()));

        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            party.getMembers().add(new PartyMember(in.readUTF(), UUID.fromString(in.readUTF())));
        }
    }

    public enum PartyUpdateType {
        REMOVE_PLAYER_LEAVE,
        REMOVE_PLAYER_KICK,
        DISBAND,
        ADD_PLAYER,
        INVITE_PLAYER
    }

}
