package com.veltpvp.nirvana.packet.party;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    @Getter private final UUID uuid;
    @Getter private final List<PartyMember> members;
    @Getter private final List<PartyMember> invitedPlayers;

    public Party(UUID uuid) {
        this.uuid = uuid;
        this.members = new ArrayList<PartyMember>();
        this.invitedPlayers = new ArrayList<PartyMember>();
    }

    public boolean isLeader(PartyMember member) {
        return members.indexOf(member) == 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Party party = (Party) o;

        return uuid.equals(party.getUuid());
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
