package com.veltpvp.nirvana.packet.party;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ErrorPacket extends Packet {

    @Getter private String error;

    public ErrorPacket(String error) {
        this.error = error;
    }

    public ErrorPacket() {

    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(error);
    }

    public void decode(DataInputStream in) throws IOException {
        this.error = in.readUTF();
    }

}
