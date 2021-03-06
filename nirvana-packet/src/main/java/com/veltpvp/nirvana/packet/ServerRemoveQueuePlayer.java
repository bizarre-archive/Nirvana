package com.veltpvp.nirvana.packet;

import lombok.Getter;
import us.ikari.phoenix.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerRemoveQueuePlayer extends Packet {

    @Getter private String player;

    public ServerRemoveQueuePlayer() {
    }

    public ServerRemoveQueuePlayer(String player) {
        this.player = player;
    }

    public void encode(DataOutputStream out) throws IOException {
        out.writeUTF(player);
    }

    public void decode(DataInputStream in) throws IOException {
        player = in.readUTF();
    }
}
