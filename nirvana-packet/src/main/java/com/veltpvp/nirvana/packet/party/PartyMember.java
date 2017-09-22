package com.veltpvp.nirvana.packet.party;

import lombok.Getter;

import java.util.UUID;

public class PartyMember {

    @Getter private final String name;
    @Getter private final UUID uuid;

    public PartyMember(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof PartyMember)) return false;
        if (obj == this) return true;

        PartyMember other = (PartyMember) obj;

        return other.getName().equalsIgnoreCase(name) && other.getUuid().equals(uuid);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + uuid.hashCode();
        return result;
    }
}
